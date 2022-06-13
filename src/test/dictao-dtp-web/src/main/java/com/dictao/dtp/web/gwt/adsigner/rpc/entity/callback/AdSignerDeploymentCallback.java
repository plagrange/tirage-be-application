package com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback;

import com.allen_sauer.gwt.log.client.Log;
import com.dictao.dtp.web.gwt.adsigner.client.widget.AdSigner;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author kchakali
 * 
 */
public class AdSignerDeploymentCallback implements AsyncCallback<String> {

    AdSigner adsigner;

    public AdSignerDeploymentCallback(AdSigner adsigner) {
        super();
        this.adsigner = adsigner;
    }

    @Override
    public void onFailure(Throwable caught) {
        this.adsigner.onError(caught);
    }

    @Override
    public void onSuccess(String deployement) {
        Log.debug(AdSignerDeploymentCallback.class.getName(), "Tag applet: " + deployement);
        this.adsigner.deploy(deployement);
    }
}
