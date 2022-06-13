package com.dictao.dtp.web.gwt.attachment.client.listener;


import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Classe d'écoute de suppression d'un fichier
 * 
 * @author AEY
 */
public interface DeleteListener {

    public void deleteAttachment(String filename,AsyncCallback<Void> callback);

    public void updateTranferPanel();
    
    public void startWaiting();
    
    public void stopWaiting();
    
    public void clearAfterDelete();
}
