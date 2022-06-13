package com.dictao.dtp.web.gwt.attachment.shared.services;

import java.util.List;

import com.dictao.dtp.web.gwt.attachment.shared.entity.data.IndexEntryData;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.InitDataAttachment;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 */
public interface AttachmentServiceAsync {

    void getIndexedDocumentList(String accessID,AsyncCallback<List<IndexEntryData>> callback);
    void getInitDataAttachment(String accessID,AsyncCallback<InitDataAttachment> callback);
    void notifyUploadedDocuments(String accessID,AsyncCallback<Void> callback);
    void removeIndexedDocument(String accessID,String filename, AsyncCallback<Void> callback);
    void completeLater(String accessID,AsyncCallback<Void> callback);
}
