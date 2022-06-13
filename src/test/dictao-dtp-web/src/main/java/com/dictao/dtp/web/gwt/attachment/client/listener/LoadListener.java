package com.dictao.dtp.web.gwt.attachment.client.listener;

/**
 * Classe d'écoute du chargement de la page
 * 
 * @author AEY
 */
public interface LoadListener {

    public void initTransferPanel();
    
    public void startWaiting();
    
    public void stopWaiting();
    
    public void addErrorMessage(String message);
    
    public void clearMessage();
    
    public void notify(String message);

    public void error(Throwable error);
}
