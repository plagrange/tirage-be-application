package com.dictao.dtp.web.gwt.attachment.client.frame.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.dictao.dtp.web.gwt.attachment.client.listener.CompleteLaterListener;
import com.dictao.dtp.web.gwt.common.client.frame.ui.Panel;
import com.dictao.dtp.web.gwt.common.client.util.WidgetUtil;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class CompleteLaterPanel extends Panel<CompleteLaterListener> implements ClickHandler{

    /* *********************************************************************/
    /* CONSTANTS */
    /* *********************************************************************/

    /* *********************************************************************/
    /* ATTRIBUTES */
    /* *********************************************************************/
	
	private Button completeLaterButton;
	
    public CompleteLaterPanel(String label)
    {
        super();
        init(label);
    }
    
    public void enable()
    {
    	completeLaterButton.setEnabled(true);
        WidgetUtil.removeDisableStyle(completeLaterButton, "completeLater-btn");	
    }
    
    public void disable()
    {
    	completeLaterButton.setEnabled(false);
        WidgetUtil.addDisableStyle(completeLaterButton, "completeLater-btn");
    }
    
	@Override
	public void onClick(ClickEvent event) {
		  Log.debug(CompleteLaterPanel.class.getName(), "click on complete later button");
	        for(CompleteLaterListener listener : listeners)
	        {
	            listener.completeLater();
	        }
		
	}
	
    /* *********************************************************************/
    /* PROTECTED/PRIVATE METHODS */
    /* *********************************************************************/
    
    private void init(String label)
    {
    	   HorizontalPanel panel = new HorizontalPanel();
    	   completeLaterButton = new Button();
    	   completeLaterButton.setHTML(label);
           WidgetUtil.addStyle(completeLaterButton, "completeLater-btn");
           completeLaterButton.addClickHandler(this);
           panel.add(completeLaterButton);
           
           add(panel);
    }
    
}
