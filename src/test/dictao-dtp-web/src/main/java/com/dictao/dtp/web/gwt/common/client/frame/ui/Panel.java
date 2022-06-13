package com.dictao.dtp.web.gwt.common.client.frame.ui;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Classe principale des différents éléments graphique.
 * 
 * @author gla
 */
public abstract class Panel<T> extends VerticalPanel
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
    public Panel()
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
