/*
 * (@)RedirectException.java
 * Copyright © 2010 Dictao. All rights reserved. Confidential and proprietary.
 * Creation on 15 janv. 10
 * Last Modified on 15 janv. 10
 *
 */
package com.dictao.dtp.web.gwt.attachment.shared.entity.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe de données d'initialisation client pour les pièces justificatives.
 * 
 * @author AEY
 */
public class InitDataAttachment implements Serializable
{

    private static final long serialVersionUID = 7827348671806499797L;
    
    private HashMap<String, String> wordingMap;
    
    private List<Attachment> attachments;
    
    private String refTransactionID;
    private String refAccessID;
    private boolean completeLaterOn;

    /**
     * Constructeur.
     */
    public InitDataAttachment()
    {
        super();
    }
    
    public Map<String, String> getWordingMap() {
        return wordingMap;
    }
    
    public void setWordingMap(HashMap<String, String> wm) {
            this.wordingMap = wm;
    }
    
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setRefTransactionID(String refTransactionID) {
        this.refTransactionID = refTransactionID;
    }

    public String getRefTransactionID() {
        return refTransactionID;
    }
    
    public void setRefAccessID(String refAccessID) {
        this.refAccessID = refAccessID;
    }

    public String getRefAccessID() {
        return refAccessID;
    }

	public void setCompleteLaterOn(boolean completeLaterOn) {
		this.completeLaterOn = completeLaterOn;
	}

	public boolean isCompleteLaterOn() {
		return completeLaterOn;
	}

}
