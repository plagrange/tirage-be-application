package com.dictao.dtp.web.proofexplorer;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.ecm.EcmFileSystemService;
import com.dictao.dtp.types.proof.description.Description;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

/**
 * A simple ecm zip with session cached transactions and
 * loaded proof description entries
 */
@SessionScoped
public class SessionEcmZipService extends EcmFileSystemService implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEcmZipService.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + SessionEcmZipService.class.getName());

    int maxZipSize;
    int maxZipEntrySize;
    int maxZipTotalContentSize;

    List<String> activeTransactions = new ArrayList<String>();
    HashMap<String, Description> loadedProofDescriptions = new HashMap<String, Description>();

    @Inject
    SessionEcmZipConfiguration conf;

    public int getMaxZipSize() {
        return maxZipSize;
    }

    public int getMaxZipEntrySize() {
        return maxZipEntrySize;
    }

    public int getMaxZipTotalContentSize() {
        return maxZipTotalContentSize;
    }

    /**
     * Load service configuration
     */
    @PostConstruct
    protected void Initialize(){
        LOG.entering();
        init(conf.getTempStorageFolder());

        maxZipSize = conf.getMaxZipSize();
        maxZipEntrySize = conf.getMaxZipEntrySize();
        maxZipTotalContentSize = conf.getMaxZipTotalContentSize();

        LOG.entering();
    }

    public String AddTransactionFromZip(InputStream inputStream) {

        LOG.info("Loading offline zip transaction");

        // Store zip stream to disk
        String zipId = StoreZipStreamToDisk(inputStream);

        try {
            // Extract zip
            ExtractZipToDisk(zipId);

            // Load/validation zip archive & proof description
            VerifyProof(zipId);

        } catch (RuntimeException ex) {
            // Clean up
            RemoveTransaction(zipId);

            // Rethrow same exception
            throw ex;
        }

        // Everything is OK
        LOG.info("Offline zip transaction successfully loaded. zipId='%s'",
                zipId);

        // Store zip Id for cleanup
        activeTransactions.add(zipId);

        // Return zip Id (just like any other transaction id)
        return zipId;

    }

    /**
     * Deletes a transaction and all it's documents
     * @param zipId
     */
    public void RemoveTransaction(String zipId) {
        LOG.info("Deleting offline zip transaction. zipId='%s'",
                zipId);

        boolean error = false;

        File baseStoragePath = getStoragePath(null, true);

        // Clean up zip
        File zipFile = new File(baseStoragePath, zipId + ".zip");
        if (zipFile.exists()) {
            if(!zipFile.delete()){
                LOG.warn("Failed to delete file '%s'.", zipFile.getAbsoluteFile());
                error = true;
            }
        }

        // Clean up extracted files
        File zipStoragePath = getStoragePath(zipId, false);

        if(zipStoragePath != null) {

            String[] children = zipStoragePath.list();

            for (String child : children) {
                File entry = new File(zipStoragePath, child);
                if(!entry.delete()){
                    LOG.warn("Failed to delete file '%s'", entry.getAbsoluteFile());
                    error = true;
                }
            }

            if(!zipStoragePath.delete()){
                LOG.warn("Failed to delete folder '%s'", zipStoragePath.getAbsoluteFile());
                error = true;
            }
        }

        if(!error)
            LOG.info("Offline zip transaction successfully deleted. zipId='%s'",
                zipId);
    }

    /**
     * Stores a zip input stream to disk
     * @param inputStream
     *              The zip input stream
     * @return unique zip id (transaction id)
     */
    private String StoreZipStreamToDisk(InputStream inputStream) {
        byte[] buffer = new byte[BUFFER_SIZE];

        String zipId = UUID.randomUUID().toString();

        File baseStoragePath = getStoragePath(null, true);

        File tempZipFile = null;
        FileOutputStream tempZipOutputStream;
        try {
            tempZipFile = new File(baseStoragePath, zipId + ".zip");
            tempZipFile.createNewFile();
            tempZipOutputStream = new FileOutputStream(tempZipFile);
        } catch (IOException ex) {

            if (null != tempZipFile) {
                tempZipFile.delete();
            }

            throw new EnvironmentException(ex, EnvironmentException.Code.DTP_ENV_PROOF_VISUALIZATION,
                    "Could not write zip file to disk. Please check access permissions to storage folder '%s'",
                    baseStoragePath.getAbsoluteFile());
        }

        try {

            long totalSize = 0;
            int readBytes;
            while (-1 != (readBytes = inputStream.read(buffer))) {
                totalSize += readBytes;

                if (totalSize > maxZipSize) {
                    throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "Zip file too large. Max zip size = '%d'...", maxZipSize);
                }

                tempZipOutputStream.write(buffer, 0, readBytes);
            }

            tempZipOutputStream.close();

        } catch (IOException ex) {

            try {
                tempZipOutputStream.close();
            } catch (IOException ex2) {
                LOG.warn(ex2, "Failed to close zip file stream '%s'", tempZipFile.getPath());
            }

            if(!tempZipFile.delete()){
                LOG.warn("Failed to delete temp file '%s'", tempZipFile.getPath());
            }

            throw new UserException(ex, UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Could not write zip file to disk.");
        }

        return zipId;
    }

     /**
     * Basic validation of proof archive and proof description.
     *
     * @param zipId
     *          transaction Id
     */
    private void VerifyProof(String zipId) {

        File dir = getStoragePath(zipId, true);

        String[] children = dir.list();

        // Ensure zip contains a [description].xml file
        File descriptionFile = new File(dir, PROOF_DESCRIPTION_DOCUMENT_NAME);

        if (!descriptionFile.exists()) {
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid proof zip. No description file found, zipId='%s'.",
                    zipId);
        }

        InputStream descriptionEntryInputStream = null;
        Description description;

        try {
            descriptionEntryInputStream = new FileInputStream(descriptionFile);
            description = LoadProofDescriptionFromStream(descriptionEntryInputStream);
        } catch (Exception ex) {
            throw new UserException(ex, UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid proof zip. Could not load description file, zipId='%s'. Bad format?",
                    zipId);
        } finally {
            if (descriptionEntryInputStream != null) {
                try {
                    descriptionEntryInputStream.close();
                } catch (IOException ex2) {
                    LOG.warn(ex2, "Failed closing description file stream, zipId='%s'",
                            zipId);
                }
            }
        }

        // Ensure all description items are present in zip archive
        for (Description.Items.Item item : description.getItems().getItem()) {
            if (item.getFilename().contains("/") || item.getFilename().contains("\\")) {
                throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                        "Invalid proof description entry. Invalid document name '%s', zipId='%s'",
                        item.getFilename(), zipId);
            }

            if (PROOF_DESCRIPTION_DOCUMENT_NAME.equalsIgnoreCase(item.getFilename())) {
                throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                        "Invalid proof description entry. Description file cannot reference itself, zipId='%s'",
                        zipId);
            }

            if (!contains(children, item.getFilename())) {
                throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                        "Invalid proof zip. Missing document '%s', zipId='%s'",
                        item.getFilename(), zipId);
            }
        }

        // Ensure there is one and only one sealing file
        String sealingFileName = null;
        for (Description.Items.Item item : description.getItems().getItem()) {
            if (item.getType().equals(SEALING_TYPE)) {
                if (null == sealingFileName) {
                    sealingFileName = item.getFilename();
                } else {
                    throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "Invalid proof zip. Description file cannot contain multiple sealing files, zipId='%s'",
                            zipId);
                }
            }
        }
        if (null == sealingFileName) {
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid proof zip. Description file does not contain a sealing file, zipId='%s'",
                    zipId);
        }

        // Ensure transaction Id is a valid GUID
        try {
            UUID.fromString(description.getTransactionId());
        } catch (Exception ex) {
            throw new UserException(ex, UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid proof zip. Description file does not contain a valid transaction id, txId='%s', zipId='%s'",
                    description.getTransactionId(), zipId);
        }
    }

    private static boolean contains(String[] list, String item) {

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Extracts zip file from disk (/zipId.zip) to disk (/zipId/*) <BR>
     * Zip file is deleted on success.
     * @param zipId
     */
    private void ExtractZipToDisk(String zipId) {

        try {

            File baseStoragePath = getStoragePath(null, true);
            File file = new File(baseStoragePath, zipId + ".zip");
            ZipFile zipFile = new ZipFile(file);

            File dir = getStoragePath(zipId, true);

            Enumeration<ZipEntry> zipEntries = (Enumeration<ZipEntry>) zipFile.entries();

            long totalZipContentSize = 0;

            while (zipEntries.hasMoreElements()) {
                ZipEntry entry = zipEntries.nextElement();

                if (entry.isDirectory()) {
                    throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "Invalid proof zip archive. Proof archive cannot contain folder entries '%s', zipId='%s'",
                            entry.getName(), zipId);
                }

                if (entry.getName().contains("/") || entry.getName().contains("\\")) {
                    throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "Invalid proof zip archive. Invalid zip entry filename '%s', zipId='%s'",
                            entry.getName(), zipId);
                }

                if (entry.getSize() > maxZipEntrySize) {
                    throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "Invalid proof zip archive. Zip entry too large '%s', zipId='%s'. Max entry size='%d'",
                            entry.getName(), zipId, maxZipEntrySize);
                }

                totalZipContentSize += entry.getSize();

                if (totalZipContentSize > maxZipTotalContentSize) {
                    throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "Invalid proof zip archive. Total zip content size cannot exceed '%d', zipId='%s'",
                            maxZipTotalContentSize, zipId);
                }

                File entryFile = new File(dir, entry.getName());
                entryFile.createNewFile();

                InputStream entryInputStream = zipFile.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(entryFile);

                byte[] buffer = new byte[BUFFER_SIZE];

                int readBytes;
                while (-1 != (readBytes = entryInputStream.read(buffer))) {
                    fos.write(buffer, 0, readBytes);
                }
                entryInputStream.close();
                fos.close();
            }

            zipFile.close();
            file.delete();

        } catch (ZipException ex) {
            throw new UserException(ex, UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid proof zip archive. Bad zip format.");
        } catch (IOException ex) {
            throw new EnvironmentException(ex, EnvironmentException.Code.DTP_ENV_PROOF_VISUALIZATION,
                    "Could not extract zip to disk. Possible causes : Disk full, quota reached, missing permissions...");
        }
    }

    @Override
    protected synchronized Description LoadProofDescription(String zipId) {

        // Simple cache for loaded proof descriptions
        Description result;

        if(loadedProofDescriptions.containsKey(zipId)) {
            result = loadedProofDescriptions.get(zipId);
        } else {
            result = super.LoadProofDescription(zipId);
            loadedProofDescriptions.put(zipId, result);
        }

        return result;
    }

    /*
     * Remove all user transactions on session expiration
     */
    @PreDestroy
    protected synchronized void CleanUp(){
        loadedProofDescriptions.clear();

        // Try removing active transactions
        for(String zipId : activeTransactions) {
            try{
                RemoveTransaction(zipId);
            }catch(Exception ex) {
                LOG.warn(ex, "Failed cleaning expired zip proof '%s'",
                        zipId);
            }
        }
        activeTransactions.clear();
    }
}