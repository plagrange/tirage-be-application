package com.dictao.dtp.web.gwt.common.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 */
public interface CommonServiceAsync {
   
    void back(String accessID,AsyncCallback<Void> callback);
    void getMessage(String accessID,String message, AsyncCallback<String> callback);
    void seal(String accessID,AsyncCallback<Void> callback);
    void finish(String accessID,AsyncCallback<Void> callback);

}
