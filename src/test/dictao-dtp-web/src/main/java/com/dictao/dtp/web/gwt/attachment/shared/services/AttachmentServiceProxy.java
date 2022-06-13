package com.dictao.dtp.web.gwt.attachment.shared.services;

import java.util.List;

import com.dictao.dtp.web.gwt.attachment.shared.entity.data.IndexEntryData;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.InitDataAttachment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class AttachmentServiceProxy {
    private String accessID;
    private AttachmentServiceAsync service;
    
    public AttachmentServiceProxy(String accessID) {
        this.accessID = accessID;
        this.service = GWT.create(AttachmentService.class); ;
    }

    public void getIndexedDocumentList(
            AsyncCallback<List<IndexEntryData>> callback) {
      service.getIndexedDocumentList(accessID, callback);     
    }

    public void getInitDataAttachment(AsyncCallback<InitDataAttachment> callback) {
        service.getInitDataAttachment(accessID, callback); 
    }

    public void notifyUploadedDocuments(AsyncCallback<Void> callback) {
        service.notifyUploadedDocuments(accessID, callback);  
    }

    public void removeIndexedDocument(String filename,
            AsyncCallback<Void> callback) {
        service.removeIndexedDocument(accessID, filename, callback);
    }

	public void completeLater(AsyncCallback<Void> callback) {
		service.completeLater(accessID, callback);
	}
}
