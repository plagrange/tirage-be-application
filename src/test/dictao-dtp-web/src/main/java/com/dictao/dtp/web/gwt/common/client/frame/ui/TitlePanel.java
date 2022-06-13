package com.dictao.dtp.web.gwt.common.client.frame.ui;

import com.dictao.dtp.web.gwt.common.client.util.WidgetUtil;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class TitlePanel extends HorizontalPanel
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

    public TitlePanel(String label)
    {
        super();
        init(label);
    }
    
    public TitlePanel()
    {
        this("");
    }

    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */

    private void init(String label)
    {
        html = new HTML(label);
        WidgetUtil.addStyle(html, "txtTitleLabel");
        this.add(html);
    }

    public void setMessage(String label)
    {
        html.setHTML(label);
    }
    
    public void clear()
    {
        html.setHTML("");
    }
}
