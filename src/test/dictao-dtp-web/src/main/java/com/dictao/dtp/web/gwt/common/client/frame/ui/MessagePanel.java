package com.dictao.dtp.web.gwt.common.client.frame.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.dictao.dtp.web.gwt.common.client.util.WidgetUtil;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MessagePanel extends HorizontalPanel
{

    /* ******************************************************************** */
    /* CONSTANTS */
    /* ******************************************************************** */

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */
    private HTML html;

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */

    public MessagePanel(String label)
    {
        super();
        init(label);
    }
    
    public MessagePanel()
    {
        this("");
    }

    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */

    private void init(String label)
    {
        html = new HTML(label);
        WidgetUtil.addStyle(html, "txtMessageSummaryLabel");
        this.add(html);
    }

    public void setMessage(String label)
    {
        Log.debug(MessagePanel.class.getName(), "Display a message: " + label);
        WidgetUtil.removeErrorStyle(html, "txtMessageSummaryLabel");
        html.setHTML(label);
    }
    
    public void setError(String label)
    {
        Log.debug(MessagePanel.class.getName(), "Display an error message: " + label);
        WidgetUtil.addErrorStyle(html, "txtMessageSummaryLabel");
        html.setHTML("<img src=\"images/warning.png\" class=\"gwt-Image\"/>"+label);
    }
    
    public void clear()
    {
        WidgetUtil.removeErrorStyle(html, "txtMessageSummaryLabel");
        html.setHTML("");
    }
}
