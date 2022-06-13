package com.dictao.dtp.web.gwt.common.client.frame.ui;

import com.allen_sauer.gwt.log.client.Log;

import com.dictao.dtp.web.gwt.common.client.frame.listener.BackListener;
import com.dictao.dtp.web.gwt.common.client.util.WidgetUtil;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Gestion d'un panneau de retour.
 * 
 * @author gla
 */
public class BackPanel extends Panel<BackListener> implements ClickHandler
{
    /* *********************************************************************/
    /* CONSTANTS */
    /* *********************************************************************/

    /* *********************************************************************/
    /* ATTRIBUTES */
    /* *********************************************************************/
    private Button backButton;
    /* *********************************************************************/
    /* PUBLIC METHODS */
    /* *********************************************************************/
    public BackPanel(String label)
    {
        super();
        init(label);
    }
    
    public void enable()
    {
        backButton.setEnabled(true);
        WidgetUtil.removeDisableStyle(backButton, "back-btn");
    }
    
    public void disable()
    {
        backButton.setEnabled(false);
        WidgetUtil.addDisableStyle(backButton, "back-btn");
    }
    
    public void onClick(ClickEvent arg0)
    {
        Log.debug(BackPanel.class.getName(), "click on back button");
        for(BackListener listener : listeners)
        {
            listener.back();
        }
    }
    
    /* *********************************************************************/
    /* PROTECTED/PRIVATE METHODS */
    /* *********************************************************************/
    
    private void init(String label)
    {
        HorizontalPanel panel = new HorizontalPanel();
        backButton = new Button();
        backButton.setHTML(label);
        WidgetUtil.addStyle(backButton, "back-btn");
        backButton.addClickHandler(this);
        panel.add(backButton);
        
        add(panel);
    }
}
