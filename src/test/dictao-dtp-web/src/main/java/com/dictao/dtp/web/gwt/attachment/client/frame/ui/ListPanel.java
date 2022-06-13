package com.dictao.dtp.web.gwt.attachment.client.frame.ui;

import com.allen_sauer.gwt.log.client.Log;

import java.util.Map;
import java.util.Set;

import com.dictao.dtp.web.gwt.attachment.client.AttachmentConstants;
import com.dictao.dtp.web.gwt.attachment.client.listener.LoadListener;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.Attachment;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.FileUploadData;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.InitDataAttachment;
import com.dictao.dtp.web.gwt.common.client.frame.ui.Fpanel;
import com.dictao.dtp.web.gwt.common.client.util.WidgetUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ListPanel extends Fpanel<LoadListener> implements ClickHandler {

    private static final String ACTION_SERVLET = "document/ecm/";
    private AttachmentConstants attachmentConstants = GWT.create(AttachmentConstants.class);
    private int nbFiles = 0;
    private int nbUploaded = 0;

    private InitDataAttachment data;
    Map <String, FileUploadData> mappingList;

    public ListPanel(InitDataAttachment data, Map <String, FileUploadData> mappingList) {
        super();
        this.data = data;
        this.mappingList = mappingList;
        init();

    }

    private void init() {
        Map<String, String> wordingMap = data.getWordingMap();

        // initialisation du titre
        String labelStep = attachmentConstants.txtStepOneLabel();
        labelStep = replaceWording(wordingMap, labelStep);
        HTML html = new HTML(labelStep);
        WidgetUtil.addStyle(html, "txtMessageStepLabel");
        this.add(html);

        nbFiles = data.getAttachments().size();
        // Ajout des éléments
        for (Attachment attachment : data.getAttachments()) {
            this.addElement(attachment);
        }
        if( nbUploaded >= nbFiles){
           this.addStyleName("hide");
         }
       
    }

    private void addElement(final Attachment attachment) {

        // Create a FormPanel and point it at a service.
        final FormPanel form = new FormPanel();
        String postUrl = GWT.getModuleBaseURL() + "../" + ACTION_SERVLET + data.getRefTransactionID() +
                "?aid=" + data.getRefAccessID() + "&type=" + attachment.getType();
        Log.debug(ListPanel.class.getName(), "Post URL: " + postUrl);
        
        form.setAction(postUrl);
        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multipart MIME encoding.
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        FlowPanel fpanel = new FlowPanel();
        form.setWidget(fpanel);

        // titre de la pièce justificative
        HTML htmlTitle = new HTML(attachment.getLabel());
        WidgetUtil.addStyle(htmlTitle, "attachment-title");
        fpanel.add(htmlTitle);

        // label pièce justificatives
        HTML htmlPJ = new HTML(attachmentConstants.txtFileStepOneLabel());
        WidgetUtil.addStyle(htmlPJ, "attachment-label");

        // Create a panel to hold  the form widgets.
        HorizontalPanel panel = new HorizontalPanel();
        fpanel.add(panel);
        panel.add(htmlPJ);

        HTML htmlSize = new HTML(attachment.getSizeMax() + " " + attachmentConstants.txtSizeMax());
        WidgetUtil.addStyle(htmlSize, "attachment-label");
        panel.add(htmlSize);

        HTML htmlAccept = new HTML(attachmentConstants.txtFileAccepted());
        WidgetUtil.addStyle(htmlAccept, "attachment-label");
        panel.add(htmlAccept);

        // Create a FileUpload widget.
        final FileUpload upload = new FileUpload() {
            @Override
            public void onBrowserEvent(Event event) {
                super.onBrowserEvent(event);
                // Window.alert("onBrowserEvent");
            }
        };
        upload.setWidth("300px");
        upload.setName(attachment.getFilename());
        WidgetUtil.addStyle(upload, "upload-file");
        // add the widgets (fileUpload) to mappingList
        if (mappingList.containsKey(attachment.getType())) {
            FileUploadData fileUploadData = mappingList.get(attachment.getType());
            fileUploadData.setFileUpload(upload);
            mappingList.put(attachment.getType(), fileUploadData);
            if (fileUploadData.isUploaded()){
                nbUploaded++;
                upload.setEnabled(false);
                form.addStyleName("hide");
            }else{
                form.removeStyleName("hide");
            }
        }
        else {
            FileUploadData fileUploadData = new FileUploadData();
            fileUploadData.setFileUpload(upload);
            mappingList.put(attachment.getType(), fileUploadData);
        }
        panel.add(upload);

        upload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                form.submit();
            }
        });

        // Add an event handler to the form.
        form.addSubmitHandler(new SubmitHandler() {
            public void onSubmit(SubmitEvent event) {
                Log.debug(ListPanel.class.getName(), "click on browse button");
                for (LoadListener listener : listeners) {
                    listener.startWaiting();
                    listener.clearMessage();
                }
            }
        });

        final ListPanel globalElement = this;
        form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
            public void onSubmitComplete(SubmitCompleteEvent event) {
                for (LoadListener listener : listeners) {
                    if (!event.getResults().equalsIgnoreCase("<pre></pre>")
                            && !event.getResults().equals("")) {
                        Log.error(ListPanel.class.getName(), "An error occured during upload: " + event.getResults());
                        listener.addErrorMessage(event.getResults());
                    }
                    listener.initTransferPanel();
                    listener.stopWaiting();
                }
                // 
                //if (!event.getResults().contains("error")) {
                if (event.getResults().equalsIgnoreCase("<pre></pre>") || event.getResults().equals("")) {
                    upload.setEnabled(false);
                    form.addStyleName("hide");
                    nbUploaded++;
                    if( nbUploaded >= nbFiles){
                        globalElement.addStyleName("hide");
                    }
                    form.reset();
                }
            }
        });

        this.add(form);

    }

    protected String replaceWording(Map<String, String> wordingMap, String label) {

        String res = label;
        Set<String> keyList = wordingMap.keySet();
        for (String key : keyList) {
            String value = wordingMap.get(key);
            res = res.replaceAll(key, value);
        }
        return res;
    }

    
    @Override
    public void onClick(ClickEvent event) {
        // TODO Auto-generated method stub

    }

    public void decreaseNbUpload(){
        nbUploaded--;
    }

}
