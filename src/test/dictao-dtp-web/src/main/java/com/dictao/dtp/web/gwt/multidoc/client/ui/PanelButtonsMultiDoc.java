package com.dictao.dtp.web.gwt.multidoc.client.ui;

import com.dictao.dtp.web.gwt.multidoc.client.listener.NavMultiDocumentListener;
import com.dictao.dtp.web.gwt.multidoc.rpc.entity.DocumentInfo;
import com.google.gwt.user.client.ui.HorizontalPanel;
import java.util.LinkedHashMap;
import java.util.Map;

public class PanelButtonsMultiDoc extends HorizontalPanel {

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */
    private Map<String, ButtonMultiDoc> buttons;
    private String firstkey;
    private LinkedHashMap<String, DocumentInfo> documents;

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */
    public PanelButtonsMultiDoc(LinkedHashMap<String, DocumentInfo> listDocuments,
            NavMultiDocumentListener nav) {
        super();
        this.documents = listDocuments;
        buttons = new LinkedHashMap<String,ButtonMultiDoc>();
        boolean isFirst = true;
        for (Map.Entry<String,DocumentInfo> e : listDocuments.entrySet()){
            String key = e.getKey();
            DocumentInfo doc = e.getValue();
            ButtonMultiDoc tmp = new ButtonMultiDoc(doc.getLabel(), key);
            buttons.put(key, tmp);
            buttons.get(key).addListener(nav);
            if(isFirst){
                firstkey = key;
                buttons.get(key).setAlreadyViewed(true);
                isFirst = false;
            }
            add(buttons.get(key));
        }
    }


    public void setButtonsViewedForDocumentsSigned(){
        for (Map.Entry<String,DocumentInfo> e : documents.entrySet()){
            String key = e.getKey();
            DocumentInfo doc = e.getValue();
            if(doc.isSigned() && buttons.get(key)!=null){
                buttons.get(key).setAlreadyViewed(true);
            }
        }
    }


   public void setStyleNameForButtonsViewed(String viewed){
        for (Map.Entry<String,ButtonMultiDoc> e : buttons.entrySet()){
            String key = e.getKey();
            buttons.get(key).setStyleNameForButtonViewed(viewed);
            /*if(firstkey.equals(key)){
                 buttons.get(key).view();
            }*/
            if(buttons.get(key).isAlreadyViewed()){
                buttons.get(key).view();
            }
        }
    }

    public void setStyleNameForButtonsNotViewed(String not_viewed){
        for (Map.Entry<String,ButtonMultiDoc> e : buttons.entrySet()){
            String key = e.getKey();
            buttons.get(key).setStyleNameForButtonNotViewed(not_viewed);
            if(!buttons.get(key).isAlreadyViewed()){
                 buttons.get(key).notview();
            }
        }
    }

    public boolean areAllViewed(){
        boolean areAllViewed = true;
         for (Map.Entry<String,ButtonMultiDoc> e : buttons.entrySet()){
              ButtonMultiDoc button = e.getValue();
              areAllViewed = areAllViewed & button.isAlreadyViewed();
         }
         return areAllViewed;
    }
    
    public void enableAllButtons(){
        for (Map.Entry<String,ButtonMultiDoc> e : buttons.entrySet()){
            e.getValue().getButton().setEnabled(true);
       }
    }
    
    public void disableAllButtons(){
        for (Map.Entry<String,ButtonMultiDoc> e : buttons.entrySet()){
            e.getValue().getButton().setEnabled(false);
       } 
    }

    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */

}
