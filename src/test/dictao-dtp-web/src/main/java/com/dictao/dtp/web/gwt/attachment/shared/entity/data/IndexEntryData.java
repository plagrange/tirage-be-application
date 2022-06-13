package com.dictao.dtp.web.gwt.attachment.shared.entity.data;

import java.io.Serializable;

public class IndexEntryData implements Serializable {

    private static final long serialVersionUID = -4396797908323257012L;
    private String documentFilename;
    private String label;
    private String type;
    
    public void setLabel(String label) {
        this.label = label;
    }

    private int size;

    public String getDocumentFilename() {
        return documentFilename;
    }

    public void setDocumentFilename(String documentFilename) {
        this.documentFilename = documentFilename;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getLabel() {
        return label;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type ) {
        this.type = type;
    }

}
