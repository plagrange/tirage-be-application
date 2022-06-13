package com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback;

import com.dictao.dtp.web.gwt.adsigner.client.listener.AdSignerListener;
import com.dictao.dtp.web.gwt.adsigner.client.widget.AdSigner;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author kchakali
 * 
 */
public class AdSignerFailureCallback implements AsyncCallback<Void> {

    AdSigner adsigner;
    int code;

    public AdSignerFailureCallback(AdSigner adsigner, int code) {
        super();
        this.adsigner = adsigner;
        this.code = code;
    }

    @Override
    public void onFailure(Throwable caught) {
        this.adsigner.onError(caught);
    }

    @Override
    public void onSuccess(Void result) {
        new Timer() {
            @Override
            public void run() {
                adsigner.undeploy();
                new Timer() {
                    @Override
                    public void run() {
                        for (AdSignerListener listener : adsigner.getListeners()) {
                            listener.onAdSignerFailure(code);
                        }
                    }
                }.schedule(1);
            }
        }.schedule(1);
    }
}
