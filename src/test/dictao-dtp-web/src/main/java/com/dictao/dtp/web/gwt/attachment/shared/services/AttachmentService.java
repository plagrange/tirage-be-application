package com.dictao.dtp.web.gwt.attachment.shared.services;

import java.util.List;

import com.dictao.dtp.web.gwt.attachment.shared.entity.data.IndexEntryData;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.InitDataAttachment;
import com.dictao.dtp.web.gwt.common.shared.entity.exception.RedirectException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface AttachmentService extends RemoteService {

    List<IndexEntryData> getIndexedDocumentList(String accessID) throws RedirectException;
    
    InitDataAttachment getInitDataAttachment(String accessID) throws RedirectException;
    
    void notifyUploadedDocuments(String accessID) throws RedirectException;
    
    void removeIndexedDocument(String accessID,String filename) throws RedirectException;
    
    void completeLater(String accessID) throws RedirectException; 
}