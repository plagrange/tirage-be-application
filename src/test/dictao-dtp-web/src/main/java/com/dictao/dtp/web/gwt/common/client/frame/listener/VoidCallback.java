/*
 * (@)RedirectException.java
 * Copyright © 2010 Dictao. All rights reserved. Confidential and proprietary.
 * Creation on 15 janv. 10
 * Last Modified on 15 janv. 10
 *
 */
package com.dictao.dtp.web.gwt.common.client.frame.listener;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Classe de retour  d'initialisation client.
 * 
 * @author gla
 */
public class VoidCallback implements AsyncCallback<Void>
{

    /* ******************************************************************** */
    /* CONSTANTS */
    /* ******************************************************************** */
    private static final long serialVersionUID = 7827348671806499797L;

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */
    Listener listener;
    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */

    /**
     * Constructeur.
     */
    public VoidCallback(Listener listener)
    {
        super();
        this.listener = listener;
    }

    public void onSuccess(Void data)
    {
        // rien a faire
    }
       
    public void onFailure(Throwable error)
    {
        listener.error(error);
    }
    
    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */

}
