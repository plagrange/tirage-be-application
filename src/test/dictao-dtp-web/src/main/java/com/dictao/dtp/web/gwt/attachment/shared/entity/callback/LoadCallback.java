package com.dictao.dtp.web.gwt.attachment.shared.entity.callback;

import com.dictao.dtp.web.gwt.attachment.client.listener.LoadListener;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoadCallback implements AsyncCallback<String>{

    LoadListener listener;
    
    public LoadCallback(LoadListener listener)
    {
        super();
        this.listener = listener;
    }

    public void onSuccess(String message)
    {
        listener.notify(message);
    }
       
    public void onFailure(Throwable error)
    {
        listener.error(error);
    }
}
