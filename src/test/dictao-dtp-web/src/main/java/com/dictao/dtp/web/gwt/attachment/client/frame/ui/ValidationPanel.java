package com.dictao.dtp.web.gwt.attachment.client.frame.ui;

import com.allen_sauer.gwt.log.client.Log; 

import com.dictao.dtp.web.gwt.attachment.client.listener.ValidateListener;
import com.dictao.dtp.web.gwt.common.client.frame.ui.Panel;
import com.dictao.dtp.web.gwt.common.client.util.WidgetUtil;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Gestion d'un panneau de transmission des pièces justificatives
 * 
 * @author AEY
 */
public class ValidationPanel extends Panel<ValidateListener> implements ClickHandler
{
    /* *********************************************************************/
    /* CONSTANTS */
    /* *********************************************************************/

    /* *********************************************************************/
    /* ATTRIBUTES */
    /* *********************************************************************/
    private Button transmitButton;
    /* *********************************************************************/
    /* PUBLIC METHODS */
    /* *********************************************************************/
    public ValidationPanel(String label)
    {
        super();
        init(label);
    }
    
    public void enable()
    {
        transmitButton.setEnabled(true);
        WidgetUtil.removeDisableStyle(transmitButton, "val-btn");
    }
    
    public void disable()
    {
        transmitButton.setEnabled(false);
        WidgetUtil.addDisableStyle(transmitButton, "val-btn");
    }

    public void onClick(ClickEvent arg0)
    {
        Log.debug(ValidationPanel.class.getName(), "Click on send button");
        for(ValidateListener listener : listeners)
        {
            listener.transmit();
        }
    }
    
    /* *********************************************************************/
    /* PROTECTED/PRIVATE METHODS */
    /* *********************************************************************/
    
    private void init(String label)
    {
        HorizontalPanel panel = new HorizontalPanel();
        transmitButton = new Button();
        transmitButton.setEnabled(false);
        transmitButton.setHTML(label);
        WidgetUtil.addStyle(transmitButton, "val-btn");
        transmitButton.addClickHandler(this);
        panel.add(transmitButton);
        add(panel);
    }
}
