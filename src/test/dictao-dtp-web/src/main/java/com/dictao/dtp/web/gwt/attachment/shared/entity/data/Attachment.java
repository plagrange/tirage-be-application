package com.dictao.dtp.web.gwt.attachment.shared.entity.data;

import java.io.Serializable;
import java.util.List;

public class Attachment implements Serializable {

    private static final long serialVersionUID = -2902985867232962044L;
    
    private String type;
    
    private String label;
    
    private String filename;
    
    private int sizeMax;
    
    private String description;
    
    private List<String> listMineType;
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getListMineType() {
        return listMineType;
    }

    public void setListMineType(List<String> listMineType) {
        this.listMineType = listMineType;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setSizeMax(int sizeMax) {
        this.sizeMax = sizeMax;
    }

    public int getSizeMax() {
        return sizeMax;
    }

 /*   public void setMimeType(ContentType mimeType) {
        this.mimeType = mimeType;
    }

    public ContentType getMimeType() {
        return mimeType;
    }*/
    
    

}
