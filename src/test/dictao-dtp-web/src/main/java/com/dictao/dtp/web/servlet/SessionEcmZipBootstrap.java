package com.dictao.dtp.web.servlet;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.web.proofexplorer.SessionEcmZipConfiguration;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.File;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Bootstrap listener used to initialize zip ecm parameters. <BR>
 *
 * A SessionEcmZipConfiguration instance (injected singleton) is used
 * to store zip ecm service parameters.
 *
 */
public class SessionEcmZipBootstrap implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEcmZipBootstrap.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + SessionEcmZipBootstrap.class.getName());
    // Parameters in web.xml
    String ECM_ZIP_TEMP_STORAGE_FOLDER_PARAMETER = "ecm_zip_temp_storage_folder";
    String ECM_ZIP_MAX_ZIP_SIZE_PARAMETER = "ecm_zip_max_zip_size";
    String ECM_ZIP_MAX_ZIP_ENTRY_SIZE_PARAMETER = "ecm_zip_max_zip_entry_size";
    String ECM_ZIP_MAX_ZIP_TOTAL_CONTENT_SIZE_PARAMETER = "ecm_zip_max_total_content_size";
    // Default values (if not specified)
    int DEFAULT_ECM_ZIP_MAX_ZIP_SIZE = 10 * 1024 * 1024; // 10 MB
    int DEFAULT_ECM_ZIP_MAX_ZIP_ENTRY_SIZE = 4 * 1024 * 1024; // 4 MB
    int DEFAULT_ECM_ZIP_MAX_ZIP_TOTAL_CONTENT_SIZE = 100 * 1024 * 1024; // 100 MB
    private BeanManager bmgr;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        LOG.entering();

        bmgr = (BeanManager) sce.getServletContext().getAttribute(
                "org.jboss.weld.environment.servlet."
                + BeanManager.class.getName());

        // Initialize SessionEcmZip configuration
        SessionEcmZipConfiguration sessionEcmZipConfiguration = getReference(SessionEcmZipConfiguration.class);

        File containerTempFolder = ((File) sce.getServletContext().getAttribute("javax.servlet.context.tempdir"));
        String tempStorageFolder = sce.getServletContext().getInitParameter(ECM_ZIP_TEMP_STORAGE_FOLDER_PARAMETER);

        File dir;
        if ((null == tempStorageFolder) || tempStorageFolder.isEmpty()) {
            dir = containerTempFolder;
            LOG.info("No zip temporary storage folder specified. Using '%s' instead.", containerTempFolder);
        } else {

            dir = new File(tempStorageFolder).getAbsoluteFile();
            if (dir.exists()) {
                // Already exists, OK
            } else if (dir.mkdirs()) {
                // Created, OK
            } else {
                throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_PROOF_VISUALIZATION, "Could not create zip temporary storage folder '%s'. Missing permissions?", tempStorageFolder);
            }
        }

        File zipStorageDir = new File(dir, "proof_zip");
        if (zipStorageDir.exists()) {
            // Storage already exists... Some clean up may be needed...
            cleanZipStorageDirectory(zipStorageDir);

        } else if (zipStorageDir.mkdirs()) {
            // OK, just created !
        } else {
            throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_PROOF_VISUALIZATION, "Could not create zip temporary storage folder '%s'. Missing permissions?", zipStorageDir.getAbsoluteFile());
        }

        int maxZipSize = readIntegerParameter(sce, ECM_ZIP_MAX_ZIP_SIZE_PARAMETER, DEFAULT_ECM_ZIP_MAX_ZIP_SIZE);
        int maxZipEntrySize = readIntegerParameter(sce, ECM_ZIP_MAX_ZIP_ENTRY_SIZE_PARAMETER, DEFAULT_ECM_ZIP_MAX_ZIP_ENTRY_SIZE);
        int maxZipTotalContentSize = readIntegerParameter(sce, ECM_ZIP_MAX_ZIP_TOTAL_CONTENT_SIZE_PARAMETER, DEFAULT_ECM_ZIP_MAX_ZIP_TOTAL_CONTENT_SIZE);

        sessionEcmZipConfiguration.setTempStorageFolder(zipStorageDir.getAbsolutePath());
        sessionEcmZipConfiguration.setMaxZipSize(maxZipSize);
        sessionEcmZipConfiguration.setMaxZipEntrySize(maxZipEntrySize);
        sessionEcmZipConfiguration.setMaxZipTotalContentSize(maxZipTotalContentSize);

        LOG.exiting();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // NOP
    }

    private int readIntegerParameter(ServletContextEvent sce, String parameterName, int defaultValue) {

        int result;
        String paramValueString = sce.getServletContext().getInitParameter(parameterName);
        if (paramValueString == null || paramValueString.isEmpty()) {

            result = defaultValue;
            LOG.info("No parameter '%s' specified. Using default value '%d' instead.", parameterName, result);

        } else {
            try {
                result = Integer.parseInt(paramValueString);

            } catch (NumberFormatException ex) {
                result = DEFAULT_ECM_ZIP_MAX_ZIP_SIZE;
                LOG.warn("Invalid parameter '%s' specified. Using default value '%d' instead.", parameterName, result);
            }
        }

        return result;
    }

    protected <T> T getReference(Class<?> clazz) {
        Bean<T> bean = (Bean<T>) bmgr.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = bmgr.createCreationalContext(bean);
        return (T) bmgr.getReference(bean, clazz, ctx);
    }

    private boolean cleanZipStorageDirectory(File zipStorageDir) {
        LOG.info("Cleaning temp zip storage folder '%s'", zipStorageDir.getAbsoluteFile());
        return internalDeleteFile(zipStorageDir, false);
    }

    private boolean internalDeleteFile(File fileOrDir, boolean deleteCurrentDir) {

        boolean success = true;

        if (fileOrDir.isDirectory()) {
            String[] children = fileOrDir.list();

            for (String child : children) {
                if (!internalDeleteFile(new File(fileOrDir, child), true)) {
                    success = false;
                }
            }
        }

        if (success && deleteCurrentDir) {
            if(!fileOrDir.delete()){
                success = false;
                LOG.warn("Zip storage directory cleanup : Could not delete file or directory '%s'", fileOrDir.getAbsoluteFile());
            }
        }

        return success;
    }
}
