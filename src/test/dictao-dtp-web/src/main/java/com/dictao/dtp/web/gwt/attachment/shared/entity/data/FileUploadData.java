package com.dictao.dtp.web.gwt.attachment.shared.entity.data;

import java.io.Serializable;

import com.google.gwt.user.client.ui.FileUpload;

public class FileUploadData implements Serializable {

    private static final long serialVersionUID = 2728302486930090659L;

    private FileUpload fileUpload;
    
    private boolean uploaded =  false;

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public boolean isUploaded() {
        return uploaded;
    }
}
