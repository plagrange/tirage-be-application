package com.dictao.dtp.persistence.entity;

import com.dictao.dtp.persistence.data.Signatures;
import com.dictao.dtp.persistence.data.conversion.InvalidPersistenceDataException;
import com.dictao.dtp.persistence.data.conversion.SignaturesConverter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author msauvee
 */
@Entity
@Table(name = "TBL_DOCUMENT")
@NamedQueries({
@NamedQuery(name=Document.FIND_DOC_BY_APP_FOLDER_FILENAME,query="Select t from Document t where t.applicationId = :applicationId and t.folder = :folder and t.filename = :filename"),
@NamedQuery(name=Document.FIND_DOCS_BY_APP_FOLDER,query="Select t from Document t where t.applicationId = :applicationId and t.folder = :folder")
})
public class Document implements Serializable {

    public static final String FIND_DOC_BY_APP_FOLDER_FILENAME = "getDocumentByAppAndFolderAndFilename";
    public static final String FIND_DOCS_BY_APP_FOLDER = "getDocumentsByAppAndFolder";

    private static final long serialVersionUID = -3501635020453070415L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // Context identification (uniqueness)
    // ----------------------
    @Basic
    @Column(name = "APPLICATION_ID", nullable = false)
    private String applicationId;
    @Basic
    @Column(name = "FOLDER", nullable = false)
    private String folder;
    @Basic
    @Column(name = "FILENAME", nullable = false)
    private String filename;

    // Other attributes
    // ----------------
    @Column(name = "CREATION_DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "UPDATE_DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Basic
    @Column(name = "MIMETYPE", nullable = false)
    private String contentMimeType;

    @Basic
    @Column(name = "LABEL")
    private String label;
        
    @Basic
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Basic(fetch=FetchType.LAZY)
    @Column(name = "SIGNATURES")
    @Lob
    private byte[] rawSignatures;

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "CONTENT")
    @Lob
    private byte[] content;
    
    @Basic(fetch=FetchType.LAZY)
    @Column(name = "PREVIOUS_CONTENT")
    @Lob
    private byte[] previousContent;   

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "XML_METADATA")
    @Lob
    private byte[] xmlMetadata;

    public Document() {
        creationDate = new Date();
        updateDate = creationDate;
    }

    /*
     * Unicity is based on applicationId, folder and filename
     * Use this constructor in business layer. The no arg constructor
     * is for test and passivation.
     */
    public Document(String applicationId, String folder, String filename) {
        this();
        this.applicationId = applicationId;
        this.folder = folder;
        this.filename = filename;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the applicationID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * @param applicationID the applicationID to set
     */
    public void setApplicationID(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * @return the folder
     */
    public String getFolder() {
        return folder;
    }

    /**
     * @param folder the folder to set
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return the contentMimeType
     */
    public String getContentMimeType() {
        return contentMimeType;
    }

    /**
     * @param contentMimeType the contentMimeType to set
     */
    public void setContentMimeType(String contentMimeType) {
        this.contentMimeType = contentMimeType;
    }
    
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return the signatures
     */
    byte[] getInternalRawSignatures() {
        return rawSignatures;
    }
    
    /**
     * @return the signatures
     */
    public Signatures getSignatures() throws InvalidPersistenceDataException {
        return SignaturesConverter.loadSignatures(rawSignatures);
    }

    /**
     * @param signatures the signatures to set
     */
    public void setSignatures(Signatures signatures) throws InvalidPersistenceDataException {
        this.rawSignatures = SignaturesConverter.saveSignatures(signatures);
    }

    /**
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }
    
    /**
     * @return the previous content
     */
    public byte[] getPreviousContent() {
        return previousContent; 
    }

    /**
     * @param previousContent the previous content to set
     */
    public void setPreviousContent(byte[] previousContent) {
        this.previousContent = previousContent; 
    }

    /**
     * @return the xmlMetadata
     */
    public byte[] getXmlMetadata() {
        return xmlMetadata;
    }

    /**
     * @param xmlMetadata the xmlMetadata to set
     */
    public void setXmlMetadata(byte[] xmlMetadata) {
        this.xmlMetadata = xmlMetadata;
    }

}
