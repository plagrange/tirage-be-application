package com.dictao.dtp.web.gwt.attachment.shared.entity.callback;

import java.util.List;

import com.dictao.dtp.web.gwt.attachment.client.listener.UploadListener;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.IndexEntryData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UploadCallback implements AsyncCallback<List<IndexEntryData>>{

    UploadListener listener;
    
    public UploadCallback(UploadListener listener)
    {
        super();
        this.listener = listener;
    }

    public void onSuccess(List<IndexEntryData> indexEntries)
    {
        listener.notify(indexEntries);
    }
       
    public void onFailure(Throwable error)
    {
        listener.error(error);
    }
}
