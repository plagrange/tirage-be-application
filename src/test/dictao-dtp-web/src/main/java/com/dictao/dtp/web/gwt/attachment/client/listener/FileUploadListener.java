package com.dictao.dtp.web.gwt.attachment.client.listener;

import java.util.List;

import com.dictao.dtp.web.gwt.attachment.shared.entity.data.IndexEntryData;

/**
 * 
 * 
 * @author AEY
 */
public interface FileUploadListener {
    
    public void notifyUploadedDocuments(List<IndexEntryData> data);
    
    public void error(Throwable error);
}
