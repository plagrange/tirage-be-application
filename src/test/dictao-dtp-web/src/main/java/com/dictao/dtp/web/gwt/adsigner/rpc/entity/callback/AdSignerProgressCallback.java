package com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback;

import com.dictao.dtp.web.gwt.adsigner.client.listener.AdSignerListener;
import com.dictao.dtp.web.gwt.adsigner.client.widget.AdSigner;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author kchakali
 * 
 */
public class AdSignerProgressCallback implements AsyncCallback<Integer> {

    AdSigner adsigner;

    public AdSignerProgressCallback(AdSigner adsigner) {
        super();
        this.adsigner = adsigner;
    }

    @Override
    public void onFailure(Throwable caught) {
        this.adsigner.onError(caught);
    }

    @Override
    public void onSuccess(final Integer step) {
        new Timer() {
            @Override
            public void run() {
                adsigner.undeploy();
                new Timer() {
                    @Override
                    public void run() {
                        for (AdSignerListener listener : adsigner.getListeners()) {
                            listener.onAdSignerProgress(step);
                        }
                    }
                }.schedule(1);
            }
        }.schedule(1);
    }
}
