package com.dictao.dtp.web.gwt.adsigner.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.dictao.dtp.web.gwt.adsigner.client.listener.AdSignerListener;
import com.dictao.dtp.web.gwt.adsigner.client.util.Base64;
import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerCancellationCallback;
import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerCertificateErrorCallback;
import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerDeploymentCallback;
import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerFailureCallback;
import com.dictao.dtp.web.gwt.adsigner.rpc.entity.callback.AdSignerSuccessCallback;
import com.dictao.dtp.web.gwt.adsigner.rpc.services.AdSignerServiceProxy;
import com.dictao.dtp.web.gwt.common.shared.entity.data.ClientConstants;
import com.dictao.dtp.web.gwt.common.shared.entity.exception.RedirectException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author kchakali
 *
 */
public class AdSigner extends Composite implements AsyncCallback<Void>
         {

    private List<AdSignerListener> listeners = new ArrayList<AdSignerListener>();
    private HTML html = new HTML();
    private String applet = null;
    private String divId = null;

    private AdSignerServiceProxy service;
    
    public AdSigner(String aid) {
        super();
        initWidget(html);
        getElement().setId("AdSigner");
        init_js(this);
        service = new AdSignerServiceProxy(aid);
    }

    public void addListener(AdSignerListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(AdSignerListener listener) {
        this.listeners.remove(listener);
    }
    
     public  List<AdSignerListener> getListeners() {
        return this.listeners;
    }

    public void deploy(String deployment) {
        Log.debug(AdSigner.class.getName(),"Tag applet: " + deployment);
        this.applet = deployment;
        html.setHTML(applet);
        RootPanel.get(divId).add(this);
    }

    public void undeploy() {
        Log.info(AdSigner.class.getName(),"Undeploy AdSigner");
        html.setHTML("");
    }

    private void onAdSignerError(final int error, final String trace) {
     // notify server first.
     // then, client part afterward through callback.onSuccess implementation.
        if (error == 0) {
            Log.info(AdSigner.class.getName(),"AdSigner send signature success");
            AdSignerSuccessCallback callback = new AdSignerSuccessCallback(this);
            service.onAdSignerSuccess(callback);
            
        } else {
            if (error == 1) {
                Log.error(AdSigner.class.getName(),"AdSigner has returned Cancellation code: " + error);
                AdSignerCancellationCallback callback = new AdSignerCancellationCallback(this);
                service.onAdSignerCancel(callback);

            } else if (error == 121) {
                Log.error(AdSigner.class.getName(),"AdSigner has returned code [121, 'No certificate matches the filter criteria.'],\n tag: "+ applet +"] and trace: [" + Base64.decode(trace)+ "]");
                AdSignerCertificateErrorCallback callback = new AdSignerCertificateErrorCallback(this);
                service.onAdSignerCertificateError(callback);
                
            } else if(error == 71) {
                Log.error(AdSigner.class.getName(),"AdSigner has returned code [71, 'An error has occurred following pin input.'],\n tag: [" + applet + "] and trace: [" + Base64.decode(trace)+ "]");
                AdSignerFailureCallback callback = new AdSignerFailureCallback(this,error);
                service.onAdSignerFailure(error, callback);
                
            } else {
                Log.error(AdSigner.class.getName(),"AdSigner has returned error code: " + error + ",\n with tag: [" + applet + "] \nand trace: [" + Base64.decode(trace)+ "]");
                AdSignerFailureCallback callback = new AdSignerFailureCallback(this,error);
                service.onAdSignerFailure(error, callback);
            }
        }
    }

    private void onSignature(final String signature) {
       
        new Timer() {
            @Override
            public void run() {
                undeploy();
                new Timer() {
                    @Override
                    public void run() {
                        for (AdSignerListener listener : listeners)
                            listener.onAdSignerSuccess();
                    }
                }.schedule(1);
            }
        }.schedule(1);
        // use generic AsyncCallback<Void>
        service.onAdSignerSignature(signature, this);
    }

    private void onProgress(final int step) {
        
        new Timer() {
            @Override
            public void run() {
                for (AdSignerListener listener : listeners)
                    listener.onAdSignerProgress(step);
            }
        }.schedule(1);
        // use generic AsyncCallback<Void>
        service.onAdSignerProgress(step, this);
    }

    public void init(String divId, String locale) {

        try {
            this.divId = divId;

            // https://localhost:10443/dtp/cube/frontoffice/ui/<tenant>/<applicationName>/365af381-32e5-4026-800f-eabfb436eaf3/[page]
            String path = Window.Location.getPath();
            String[] segments = path.split("/");
            if (segments.length < (7 + 1)) {
                throw new Exception("Invalid url : " + path);
            }

            path = Window.Location.getProtocol() + "//"
                    + Window.Location.getHost() + "/" + segments[1] + "/"
                    + segments[2] + "/" + segments[3] + "/";
            AdSignerDeploymentCallback callback = new AdSignerDeploymentCallback(this);
            service.getAdSignerDeployment(path, locale, callback);
        } catch (Throwable ex) {
            onError(ex);
        }
    }

    private native void init_js(AdSigner self) /*-{

                                               if(undefined === $doc.dtp)
                                               $doc.dtp = Object();
                                               if(undefined === $doc.dtp.AdSigner)
                                               $doc.dtp.AdSigner = Object();

                                               $doc.dtp.AdSigner.getError = function(error, trace) {
                                               self.@com.dictao.dtp.web.gwt.adsigner.client.widget.AdSigner::onAdSignerError(ILjava/lang/String;)(error, trace);
                                               }
                                               
                                               $wnd.sendSignature = function(signature) {
                                               self.@com.dictao.dtp.web.gwt.adsigner.client.widget.AdSigner::onSignature(Ljava/lang/String;)(signature);
                                               }
                                               
                                               $wnd.progress = function(step) {
                                               self.@com.dictao.dtp.web.gwt.adsigner.client.widget.AdSigner::onProgress(I)(step);
                                               }
                                               
                                               $doc.dtp.AdSigner.DisplayPdf = function(url) { return true; }
                                               $wnd.DisplayPdf = $doc.dtp.AdSigner.DisplayPdf;
                                               
                                               $doc.dtp.init = function (divId, locale) {
                                               return self.@com.dictao.dtp.web.gwt.adsigner.client.widget.AdSigner::init(Ljava/lang/String;Ljava/lang/String;)(divId, locale);
                                               
                                               $wnd.init = $doc.dtp.init;
                                               }
                                               }-*/;


    public void onError(Throwable caught) {
        // called for RPC call interrupted with an exception
        if (caught instanceof RedirectException) {
            String url = ((RedirectException) caught).getRedirectUrl();
            Log.debug(AdSigner.class.getName(),"The user will be redirected to: " + url);
            Window.Location.replace(url);
        } else {
            Log.error(AdSigner.class.getName(),"An error occured: " + caught);
            // FIXME Internal error 'caught' log into redirectOnError
            // JIRA : https://jira.dictao.com/browse/DTPJAVA-545
            Log.error(AdSigner.class.getName(),"Redirect to error page caused by: " + caught.getMessage());
            Window.Location.replace(ClientConstants.ERROR_URL);
        }

    }

    @Override
    public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSuccess(Void result) {
        // TODO Auto-generated method stub
        
    }
}
