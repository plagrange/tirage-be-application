package com.dictao.dtp.web.gwt.adsigner.client.listener;

public interface AdSignerListener {

    void onAdSignerSuccess();

    void onAdSignerFailure(int code);

    void onAdSignerCancellation();

    void onAdSignerProgress(int step);
    
    void onAdSignerCertificateError();
    
}
