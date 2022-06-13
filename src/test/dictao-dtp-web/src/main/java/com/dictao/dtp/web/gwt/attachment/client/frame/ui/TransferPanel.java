package com.dictao.dtp.web.gwt.attachment.client.frame.ui;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.dictao.dtp.web.gwt.attachment.client.AttachmentConstants;
import com.dictao.dtp.web.gwt.attachment.client.listener.DeleteListener;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.FileUploadData;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.IndexEntryData;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.InitDataAttachment;
import com.dictao.dtp.web.gwt.common.client.frame.ui.Fpanel;
import com.dictao.dtp.web.gwt.common.client.util.WidgetUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;


public class TransferPanel extends Fpanel<DeleteListener> {

    private static final String ACTION_SERVLET = "document/ecm/";
    private AttachmentConstants attachmentConstants = GWT.create(AttachmentConstants.class);

    private Map<String, String> wordingMap;
    private InitDataAttachment data;
    private Panel panel = new FlowPanel();
    private Map <String, FileUploadData> mappingList;
    private int nbUploaded = 0;

    public TransferPanel(InitDataAttachment data, Map <String, FileUploadData> mappingList)
    {
        super();
        this.wordingMap = data.getWordingMap();
        this.data = data;
        this.mappingList = mappingList;
        init();
    }

    private void init(){

        //Initialisation du titre
        String labelStep = attachmentConstants.txtStepTwoLabel();
        labelStep = replaceWording(wordingMap, labelStep);
        HTML html = new HTML(labelStep);
        WidgetUtil.addStyle(html, "txtMessageStepLabel");
        this.add(html);
        
        panel.addStyleName("attachmentListPanel");
        this.add(panel);
        
    }

    protected String replaceWording(Map<String, String> wordingMap, String label)
    {

        String res = label;
        Set<String> keyList = wordingMap.keySet();
        for (String key : keyList)
        {
            String value = wordingMap.get(key);
            res = res.replaceAll(key, value);
        }
        return res;
    }

    public void addElements(final List<IndexEntryData> indexEntries) {
        panel.clear();

        nbUploaded = indexEntries.size();
        if( nbUploaded <= 0 ){
              this.addStyleName("hide");
        }else{
            this.removeStyleName("hide");
            for (int i = 0; i < indexEntries.size(); i++) {

                 // titre de la pièce justificative
                HTML htmlTitle = new HTML(indexEntries.get(i).getLabel());
                WidgetUtil.addStyle(htmlTitle, "attachment-title");
                panel.add(htmlTitle);

                HorizontalPanel hp = new HorizontalPanel();
                panel.add(hp);


                final String filename = indexEntries.get(i).getDocumentFilename();
                final String type = indexEntries.get(i).getType();
                HTML htmlLabel = new HTML(attachmentConstants.txtFileStepTwoLabel());
                WidgetUtil.addStyle(htmlLabel, "transmitAttachment-label");
                hp.add(htmlLabel);
                //flexTable.setWidget(i, 0, htmlLabel);
                final Image imgTick = new Image("images/tick.png");
                imgTick.setWidth("22");
                hp.add(imgTick);

                HTML htmlSize = new HTML(indexEntries.get(i).getSize()/1024 + " Ko");
                WidgetUtil.addStyle(htmlSize, "transmitAttachment-label");
                hp.add(htmlSize);
                //flexTable.setWidget(i, 1, htmlSize);

                final Image imgFind = new Image("images/find.png");
                WidgetUtil.addStyle(imgFind, "img-find" );
                hp.add(imgFind);
                //flexTable.setWidget(i, 2, imgFind);

                final String labelView =  attachmentConstants.txtViewLabel();
                imgFind.setTitle(labelView);

                imgFind.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        Log.debug(TransferPanel.class.getName(), "Click to see " + filename);
                        String url = GWT.getModuleBaseURL() + "../" + ACTION_SERVLET
                         + data.getRefTransactionID()
                                +  "/" + filename + "?aid=" + data.getRefAccessID();
                        Window.open(url, "_blank", "");
                    }
                });

                final Image imgDelete = new Image("images/trash.png");
                WidgetUtil.addStyle(imgDelete, "img-delete" );
                hp.add(imgDelete);
                //flexTable.setWidget(i, 3, imgDelete);

                final String labelDelete = attachmentConstants.txtDeleteLabel();
                imgDelete.setTitle(labelDelete);

                final TransferPanel tp = this;
                imgDelete.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        Log.debug(TransferPanel.class.getName(), "Click to delete " + filename);
                        for (final DeleteListener listener : listeners) {
                            listener.startWaiting();
                            DeleteAttachmentCallback deleteCallback = new DeleteAttachmentCallback();
                            listener.deleteAttachment(filename, deleteCallback);

                            FileUploadData fileUploadData = mappingList.get(type);
                            if (fileUploadData != null) {
                                fileUploadData.getFileUpload().setEnabled(true);
                                ListPanel lp = (ListPanel) fileUploadData.getFileUpload().getParent().getParent().getParent().getParent();
                                lp.decreaseNbUpload();
                                fileUploadData.getFileUpload().getParent().getParent().getParent().removeStyleName("hide"); // montrer a nouveau le form cache
                                fileUploadData.getFileUpload().getParent().getParent().getParent().getParent().removeStyleName("hide"); // montrer toute la partie a teletransmettre
                                fileUploadData.setUploaded(false);
                                nbUploaded--;
                                if( nbUploaded <= 0 ){
                                    tp.addStyleName("hide");
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    class DeleteAttachmentCallback implements AsyncCallback<Void> {

        @Override
        public void onFailure(Throwable caught) {

        }
        @Override
        public void onSuccess(Void result) {
            //YESSS !!
            for (final DeleteListener listener : listeners) {
                listener.clearAfterDelete();
                listener.stopWaiting();
                init();
                listener.updateTranferPanel();
            }
            
            
        }

    }
}
