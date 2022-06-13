package com.dictao.dtp.web.gwt.common.client.frame.ui;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlowPanel;


public abstract class Fpanel<T> extends FlowPanel
{
    /* *********************************************************************/
    /* CONSTANTS */
    /* *********************************************************************/
    
    /* *********************************************************************/
    /* ATTRIBUTES */
    /* *********************************************************************/
    protected ArrayList<T> listeners;
    /* *********************************************************************/
    /* PUBLIC METHODS */
    /* *********************************************************************/
    public Fpanel()
    {
        super();
        listeners = new ArrayList<T>();
    }
    
    public void addListener(T listener)
    {
        listeners.add(listener);
    }
    
    public void removeListener(T listener)
    {
        listeners.remove(listener);
    }
    /* *********************************************************************/
    /* PROTECTED/PRIVATE METHODS */
    /* *********************************************************************/
}
