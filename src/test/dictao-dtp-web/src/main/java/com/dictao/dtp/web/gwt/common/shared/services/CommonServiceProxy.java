package com.dictao.dtp.web.gwt.common.shared.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 */
public class CommonServiceProxy {
    private String accessID;
    private CommonServiceAsync service;

    public CommonServiceProxy(String accessID) {
        this.accessID = accessID;
        this.service = GWT.create(CommonService.class); ;
    }

    public void back( AsyncCallback<Void> callback) {
        service.back(this.accessID ,callback);
    }

    public void getMessage( String message,
            AsyncCallback<String> callback) {
        service.getMessage(this.accessID, message, callback);
    }

    public void seal( AsyncCallback<Void> callback) {
        service.seal(this.accessID , callback);
    }
    
    public void finish( AsyncCallback<Void> callback) {
        service.finish(this.accessID , callback);
    }
}
