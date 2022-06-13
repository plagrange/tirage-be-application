package com.dictao.dtp.web.gwt.multidoc.client.ui;

import com.dictao.dtp.web.gwt.common.client.frame.ui.Panel;
import com.dictao.dtp.web.gwt.multidoc.client.listener.NavMultiDocumentListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;


public class ButtonMultiDoc extends Panel<NavMultiDocumentListener> implements ClickHandler {

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */
    private Button button;
    private boolean alreadyViewed;
    private String key;
    private String viewed;
    private String not_viewed;

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */
    public ButtonMultiDoc(String label, String key) {
        super();
        HorizontalPanel panel = new HorizontalPanel();
        button = new Button();
        this.alreadyViewed = false;
        this.key = key;
        this.viewed = "";
        this.not_viewed = "";
        button.setHTML(label);
        
        button.addClickHandler(this);
        panel.add(button);
        add(panel);
    }

    public void setStyleNameForButtonViewed(String viewed){
        this.viewed = viewed;
    }

    public void setStyleNameForButtonNotViewed(String not_viewed){
        this.not_viewed = not_viewed;
    }

    public void onClick(ClickEvent arg0) {
        view();
        for(NavMultiDocumentListener listener : listeners)
        {
            listener.navigateMultiDocument(key);
        }
    }

    public void setAlreadyViewed(boolean alreadyViewed) {
        this.alreadyViewed = alreadyViewed;
    }

    public boolean isAlreadyViewed() {
        return alreadyViewed;
    }

    public void view(){
        alreadyViewed = true;
        if(viewed!=null && viewed.length()!=0)
            button.setStyleName(viewed);
    }

    public void notview(){
        if(not_viewed!=null && not_viewed.length()!=0)
            button.setStyleName(not_viewed);
    }
    
    public Button getButton(){
        return button;
    }
    
    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */

}
