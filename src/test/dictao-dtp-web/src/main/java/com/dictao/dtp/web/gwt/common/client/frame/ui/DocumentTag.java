package com.dictao.dtp.web.gwt.common.client.frame.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class for the document representation with a iframe or object tag.
 * 
 * @author gla
 */
public class DocumentTag extends Widget {

    static final String DEFAULT_STYLENAME = "gwt-Frame";

    /**
     * Document representation with an object tag.
     */
    public DocumentTag(String url, String type) {

        ObjectElement element = Document.get().createObjectElement();
        setElement(element);
        element.setData(url);
        element.setType(type);
        setStyleName(DEFAULT_STYLENAME);
    }
}
