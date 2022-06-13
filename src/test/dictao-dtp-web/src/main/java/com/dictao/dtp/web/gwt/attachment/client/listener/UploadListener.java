package com.dictao.dtp.web.gwt.attachment.client.listener;

import java.util.List;

import com.dictao.dtp.web.gwt.attachment.shared.entity.data.IndexEntryData;

/**
 * Classe d'écoute de fin d'upload d'un fichier
 * 
 * @author AEY
 */
public interface UploadListener {
    
    public void notify(List<IndexEntryData> data);
    
    public void error(Throwable error);
}
