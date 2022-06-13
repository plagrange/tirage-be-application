/*
 * (@)RedirectException.java
 * Copyright © 2010 Dictao. All rights reserved. Confidential and proprietary.
 * Creation on 15 janv. 10
 * Last Modified on 15 janv. 10
 *
 */
package com.dictao.dtp.web.gwt.attachment.shared.entity.callback;

import com.dictao.dtp.web.gwt.attachment.client.listener.InitDataAttachmentListener;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.InitDataAttachment;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Classe de retour  d'initialisation client.
 * 
 * @author AEY
 */
public class InitDataAttachmentCallBack implements AsyncCallback<InitDataAttachment>
{

    /* ******************************************************************** */
    /* CONSTANTS */
    /* ******************************************************************** */


    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */
    InitDataAttachmentListener listener;
    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */

    /**
     * Constructeur.
     */
    public InitDataAttachmentCallBack(InitDataAttachmentListener listener)
    {
        super();
        this.listener = listener;
    }

    public void onSuccess(InitDataAttachment initData)
    {
        listener.onInitDataAttachment(initData);
    }
       
    public void onFailure(Throwable error)
    {
        listener.error(error);
    }
    
    
    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */

}
