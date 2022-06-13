package com.dictao.dtp.web.gwt.multidoc.client.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import com.dictao.dtp.web.gwt.common.client.frame.ui.DocumentTag;
import com.dictao.dtp.web.gwt.multidoc.rpc.entity.DocumentInfo;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MultiDocumentPanel extends VerticalPanel {

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */
    private Map<String, Widget> documents;
    private String lastShow;

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */
    public MultiDocumentPanel(LinkedHashMap<String, DocumentInfo> listDocuments) {
        super();
        init(listDocuments);
    }

    public void show(String key) {
        documents.get(lastShow).setVisible(false);
        documents.get(key).setVisible(true);
        lastShow = key;
    }

    public String getLastShow() {
        return lastShow;
    }

    public void addStyleNameForDocuments(String style) {
        if (style == null || style.length() == 0)
            return;
        for (Map.Entry<String, Widget> e : documents.entrySet()) {
            String key = e.getKey();
            documents.get(key).addStyleName(style);
        }
    }

    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */
    private void init(LinkedHashMap<String, DocumentInfo> listDocuments) {
        HorizontalPanel panel = new HorizontalPanel();
        documents = new LinkedHashMap<String, Widget>();
        boolean isFirst = true;
        for (Map.Entry<String, DocumentInfo> e : listDocuments.entrySet()) {
            String key = e.getKey();
            DocumentInfo doc = e.getValue();

            if (isFirst) {
                lastShow = key;
                isFirst = false;
            }

            String url = doc.getUrl();
            if (url.contains("?")) {
                url += "&ms=" + System.currentTimeMillis();
            } else {
                url += "?ms=" + System.currentTimeMillis();
            }
            url += doc.getPdfDisplayParameters();
            // TODO problème avec setVisible et une balise object
            //Widget contract = new DocumentTag(url, "application/pdf");
            Widget contract = new Frame(url);
            documents.put(key, contract);
            documents.get(key).setVisible(false);
            panel.add(documents.get(key));
        }
        documents.get(lastShow).setVisible(true);
        add(panel);
    }
}
