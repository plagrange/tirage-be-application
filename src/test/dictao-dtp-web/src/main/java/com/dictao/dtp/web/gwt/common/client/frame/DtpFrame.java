package com.dictao.dtp.web.gwt.common.client.frame;

import java.util.Map;
import java.util.Set;

import com.dictao.dtp.web.gwt.common.client.frame.listener.Listener;
import com.dictao.dtp.web.gwt.common.client.util.WidgetUtil;
import com.dictao.dtp.web.gwt.common.shared.entity.data.ClientConstants;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Principal frame.
 * @author gla
 */
public abstract class DtpFrame extends Composite implements Listener {

    /* **************************** CONSTANTS *************************** */

    /* **************************** ATTRIBUTES ************************** */

    protected PopupPanel popupWait;

    /* ************************** PUBLIC METHODS ************************ */

     public DtpFrame()
    {
        super();
        // Init du wait
        popupWait = new PopupPanel(true);
        WidgetUtil.addStyle(popupWait, "popupwait-loader");
        popupWait.setGlassEnabled(true);
        popupWait.setAutoHideEnabled(false);
        popupWait.setSize("50px", "50px");
    }

    public DtpFrame(String label)
    {
        super();
        
        // Init du wait
        popupWait = new PopupPanel(true);
        VerticalPanel contents = new VerticalPanel();

        SimplePanel panel = new SimplePanel();
        
        HTML mp = new HTML(label);
        panel.add(mp);
        WidgetUtil.addStyle(panel, "popupwait-text");
        contents.add(panel);

        SimplePanel panel2 = new SimplePanel();
        WidgetUtil.addStyle(panel2, "popupwait-img");
        contents.add(panel2);

        popupWait.add(contents);
        popupWait.setSize("300px", "80px");

        popupWait.setGlassEnabled(true);
        popupWait.setAutoHideEnabled(false);
    }


    
    /**
     * Show a waiting gif and change the cursor image.
     */
    public void startWaiting()
    {
        // On désactive les elements graphiques
        DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",
                "wait");
        popupWait.center();
    }
    
    /**
     * Hide the waiting gif and put the default cursor.
     */
    public void stopWaiting()
    {
        DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
        popupWait.hide();
    }

    /* ********************* PROTECTED/PRIVATE METHODS ****************** */
   
    /**
     * Initialisation des éléments graphiques.
     */
    abstract protected void initWidget();
    
    protected String replaceWording(Map<String, String> wordingMap, String label)
    {

        String res = label;
        Set<String> keyList = wordingMap.keySet();
        for (String key : keyList)
        {
            String value = wordingMap.get(key);
            res = res.replaceAll(key, value);
        }
        return res;
    }

    protected native int verifierAdobeReader()
    /*-{
		return $wnd.VerifierAdobeReader();
	}-*/;
    
    /**
     * redirect user to set URL
     * @param url
     */
    protected void redirectJs(String url){
    	Window.Location.replace(url);
    }

    /**
     * Redirect to the jsp error page 
     * @param caught
     */
    public static void redirectOnError(Throwable caught) {
        // *************************************************
        // FIXME improvement: https://jira.dictao.com/browse/DTPJAVA-545
        // Log.error("Redirect to error page caused by: ", caught);
        // *************************************************
        Window.Location.replace(ClientConstants.ERROR_URL);
    }

}
