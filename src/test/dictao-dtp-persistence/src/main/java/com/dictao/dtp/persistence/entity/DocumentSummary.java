package com.dictao.dtp.persistence.entity;

import com.dictao.dtp.persistence.data.Signatures;
import com.dictao.dtp.persistence.data.conversion.InvalidPersistenceDataException;
import java.util.Date;

/**
 * Lightweight wrapper arround the Document class which does not expose
 * expensive document content.
 * @author msauvee
 */
public class DocumentSummary {
    
    final Document doc;

    public DocumentSummary(final Document doc) {
        this.doc = doc;
    }

    /**
     * @return the applicationId
     */
    public String getApplicationId() {
        return doc.getApplicationId();
    }

    /**
     * @return the folder
     */
    public String getFolder() {
        return doc.getFolder();
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return doc.getFilename();
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return doc.getCreationDate();
    }

    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return doc.getUpdateDate();
    }

    /**
     * @return the contentMimeType
     */
    public String getContentMimeType() {
        return doc.getContentMimeType();
    }
    
    /**
     * @return the label
     */
    public String getLabel() {
        return doc.getLabel();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return doc.getDescription();
    }
    
    /**
     * @return the signatures
     */
    public Signatures getSignatures() throws InvalidPersistenceDataException {
        return doc.getSignatures();
    }

    /**
     * @return the xmlMetadata
     */
    public byte[] getXmlMetadata() {
        return doc.getXmlMetadata();
    }
}
