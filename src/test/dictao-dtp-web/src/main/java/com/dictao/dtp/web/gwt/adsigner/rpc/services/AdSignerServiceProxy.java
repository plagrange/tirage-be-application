package com.dictao.dtp.web.gwt.adsigner.rpc.services;

import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerCancellationCallback;
import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerCertificateErrorCallback;
import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerDeploymentCallback;
import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerFailureCallback;
import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerSuccessCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class AdSignerServiceProxy {

    private String accessID;
    private AdSignerServiceAsync service;

    public AdSignerServiceProxy(String accessID) {
        this.accessID = accessID;
        service = GWT.create(AdSignerService.class);
    }

    public void getAdSignerDeployment(String path, String locale,
            AdSignerDeploymentCallback callback) {
        service.getAdSignerDeployment(accessID, path, locale, callback);
    }

    public void onAdSignerSuccess(AdSignerSuccessCallback callback) {
        service.onAdSignerSuccess(accessID, callback);
    }

    public void onAdSignerFailure(int code, AdSignerFailureCallback callback) {
        service.onAdSignerFailure(accessID, code, callback);
    }

    public void onAdSignerCancel(AdSignerCancellationCallback callback) {
        service.onAdSignerCancel(accessID, callback);
    }
    
    public void onAdSignerCertificateError(AdSignerCertificateErrorCallback callback) {
        service.onAdSignerCertificateError(accessID, callback);
    }

    public void onAdSignerSignature(String signature,
            AsyncCallback<Void> callback) {
        service.onAdSignerSignature(accessID, signature, callback);
    }

    public void onAdSignerProgress(int step,
            AsyncCallback<Void> callback) {
        service.onAdSignerProgress(accessID, step, callback);
    }
}
