package com.dictao.dtp.web.gwt.adsigner.rpc.services;

import com.dictao.dtp.web.gwt.common.shared.entity.exception.RedirectException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdSignerServiceAsync {

    void getAdSignerDeployment(String accessID, String path, String locale,
            AsyncCallback<String> callback);

    void onAdSignerSuccess(String accessID, AsyncCallback<Void> callback);

    void onAdSignerFailure(String accessID, int code,
            AsyncCallback<Void> callback);

    void onAdSignerCancel(String accessID, AsyncCallback<Void> callback);
    
    void onAdSignerCertificateError(String accessID, AsyncCallback<Void> callback);

    void onAdSignerSignature(String accessID, String signature,
            AsyncCallback<Void> callback) throws RedirectException;

    void onAdSignerProgress(String accessID, int step,
            AsyncCallback<Void> callback) throws RedirectException;
}
