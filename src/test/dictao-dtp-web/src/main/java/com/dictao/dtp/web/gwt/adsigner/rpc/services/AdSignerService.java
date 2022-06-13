package com.dictao.dtp.web.gwt.adsigner.rpc.services;

import com.dictao.dtp.web.gwt.common.shared.entity.exception.RedirectException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("services/adsigner")
public interface AdSignerService extends RemoteService {

    String getAdSignerDeployment(String accessID, String path, String locale) throws RedirectException;
    void onAdSignerSuccess(String accessID) throws RedirectException;
    void onAdSignerFailure(String accessID, int code) throws RedirectException;
    void onAdSignerCancel(String accessID) throws RedirectException;
    void onAdSignerCertificateError(String accessID);

    void onAdSignerSignature(String accessID, String signature)
            throws RedirectException;

    void onAdSignerProgress(String accessID, int step) throws RedirectException;
}
