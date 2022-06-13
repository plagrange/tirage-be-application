package com.dictao.dtp.web.gwt.attachment.client.listener;

import com.dictao.dtp.web.gwt.attachment.shared.entity.data.InitDataAttachment;
import com.dictao.dtp.web.gwt.common.client.frame.listener.Listener;


/**
 * Classe d'écoute des données d'initialisation
 * 
 * @author AEY
 */
public interface InitDataAttachmentListener  extends Listener
{
    /* *********************************************************************/
    /* CONSTANTS */
    /* *********************************************************************/
    
    /* *********************************************************************/
    /* ATTRIBUTES */
    /* *********************************************************************/

    /* *********************************************************************/
    /* PUBLIC METHODS */
    /* *********************************************************************/
    public void onInitDataAttachment(InitDataAttachment initData);
    
    public void error(Throwable error);
    /* *********************************************************************/
    /* PROTECTED/PRIVATE METHODS */
    /* *********************************************************************/
}
