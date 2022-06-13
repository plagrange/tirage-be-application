package com.dictao.dtp.web.gwt.attachment.shared.entity.callback;

import java.util.List;

import com.dictao.dtp.web.gwt.attachment.client.listener.FileUploadListener;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.IndexEntryData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileUploadCallback implements AsyncCallback<List<IndexEntryData>>{

    FileUploadListener listener;
    
    public FileUploadCallback(FileUploadListener listener)
    {
        super();
        this.listener = listener;
    }

    public void onSuccess(List<IndexEntryData> indexEntries)
    {
        listener.notifyUploadedDocuments(indexEntries);
    }
       
    public void onFailure(Throwable error)
    {
        listener.error(error);
    }
}
