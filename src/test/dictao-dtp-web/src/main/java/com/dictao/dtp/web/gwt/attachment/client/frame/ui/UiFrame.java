/**
 * 
 */
package com.dictao.dtp.web.gwt.attachment.client.frame.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.dictao.dtp.web.gwt.attachment.client.AttachmentConstants;
import com.dictao.dtp.web.gwt.attachment.client.listener.CompleteLaterListener;
import com.dictao.dtp.web.gwt.attachment.client.listener.DeleteListener;
import com.dictao.dtp.web.gwt.attachment.client.listener.FileUploadListener;
import com.dictao.dtp.web.gwt.attachment.client.listener.InitDataAttachmentListener;
import com.dictao.dtp.web.gwt.attachment.client.listener.LoadListener;
import com.dictao.dtp.web.gwt.attachment.client.listener.UploadListener;
import com.dictao.dtp.web.gwt.attachment.client.listener.ValidateListener;
import com.dictao.dtp.web.gwt.attachment.shared.entity.callback.FileUploadCallback;
import com.dictao.dtp.web.gwt.attachment.shared.entity.callback.InitDataAttachmentCallBack;
import com.dictao.dtp.web.gwt.attachment.shared.entity.callback.LoadCallback;
import com.dictao.dtp.web.gwt.attachment.shared.entity.callback.UploadCallback;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.FileUploadData;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.IndexEntryData;
import com.dictao.dtp.web.gwt.attachment.shared.entity.data.InitDataAttachment;
import com.dictao.dtp.web.gwt.attachment.shared.services.AttachmentServiceProxy;
import com.dictao.dtp.web.gwt.common.client.frame.DtpFrame;
import com.dictao.dtp.web.gwt.common.client.frame.listener.BackListener;
import com.dictao.dtp.web.gwt.common.client.frame.listener.VoidCallback;
import com.dictao.dtp.web.gwt.common.client.frame.ui.BackPanel;
import com.dictao.dtp.web.gwt.common.client.frame.ui.MessagePanel;
import com.dictao.dtp.web.gwt.common.client.frame.ui.TitlePanel;
import com.dictao.dtp.web.gwt.common.client.util.Elements;
import com.dictao.dtp.web.gwt.common.client.util.WidgetUtil;
import com.dictao.dtp.web.gwt.common.shared.entity.exception.RedirectException;
import com.dictao.dtp.web.gwt.common.shared.services.CommonServiceProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author vreyduboissieu
 * 
 */
public class UiFrame extends DtpFrame implements ValidateListener,
        BackListener, InitDataAttachmentListener, UploadListener, LoadListener,
        DeleteListener, FileUploadListener, CompleteLaterListener {

    @UiTemplate("UiFrame.ui.xml")
    interface UiFrameUiBinder extends UiBinder<Widget, UiFrame> {
    }

    /* **************************** CONSTANTS *************************** */
    private static UiFrameUiBinder uiBinder = GWT.create(UiFrameUiBinder.class);

    /* **************************** ATTRIBUTES ************************** */
    @UiField
    SimplePanel txtErrorSummary;

    @UiField
    SimplePanel txtTitle;

    @UiField
    SimplePanel frameTransferPJDocuments;

    @UiField
    SimplePanel frameSelectPJDocuments;

    @UiField
    SimplePanel btnTransmit;

    @UiField
    SimplePanel btnBack;

    @UiField
    SimplePanel btnCompleteLater;
    
    private AttachmentServiceProxy attachmentService;

    private CommonServiceProxy commonService;

    private MessagePanel messagePanel;

    private TitlePanel titlePanel;

    private ListPanel listPanel;

    private TransferPanel transferPanel;

    private ValidationPanel validationPanel;

    private BackPanel backPanel;
    
    private CompleteLaterPanel completeLaterPanel;

    private String labelInit;

    private InitDataAttachment data;

    private int attachmentToUpload;

    private AttachmentConstants attachmentConstants = GWT
            .create(AttachmentConstants.class);

    private Map<String, FileUploadData> mappingList = new HashMap<String, FileUploadData>();
    
    private final boolean embedded;

    /* ************************** PUBLIC METHODS ************************ */

    public UiFrame(String aid) {
        this(false, aid);

    }

    public UiFrame(final boolean embedded, String aid) {
        super();
        initWidget(uiBinder.createAndBindUi(this));
        commonService = new CommonServiceProxy (aid); 
        attachmentService = new AttachmentServiceProxy(aid);
        InitDataAttachmentCallBack callback = new InitDataAttachmentCallBack(
                this);
        attachmentService.getInitDataAttachment(callback);
        this.embedded = embedded;
    }

    @Override
    public void error(Throwable error) {
        if (error instanceof RedirectException) {
            String url = ((RedirectException) error).getRedirectUrl();
            Log.debug(UiFrame.class.getName(), "The user will be redirected to: " + url);
            redirectJs(url);
        } else {
        	// FIXME Internal error 'caught' log into redirectOnError
    		// JIRA : https://jira.dictao.com/browse/DTPJAVA-545
    		Log.error(UiFrame.class.getName(), "Redirect to error page caused by: " + error.getMessage());
        	redirectOnError(error);
        }
    }

    @Override
    public void transmit() {
        VoidCallback callback = new VoidCallback(this);
        validationPanel.disable();
        backPanel.disable();
        startWaiting();
        commonService.seal(callback);
    }

    @Override
    public void back() {
        VoidCallback callback = new VoidCallback(this);
        commonService.back(callback);
    }

    @Override
    public void onInitDataAttachment(InitDataAttachment data) {
        this.data = data;
        this.attachmentToUpload = data.getAttachments().size();
        FileUploadCallback fileUploadCallback = new FileUploadCallback(this);
        attachmentService.getIndexedDocumentList(fileUploadCallback);
    }

    @Override
    public void initTransferPanel() {
        UploadCallback callback = new UploadCallback(this);
        attachmentService.getIndexedDocumentList(callback);

    }

    @Override
    public void clearAfterDelete() {
        transferPanel.clear();
    }

    @Override
    public void notify(List<IndexEntryData> data) {
        transferPanel.addElements(data);
        if (attachmentToUpload == data.size()) {
            validationPanel.enable();
            completeLaterPanel.disable();
        } else {
            validationPanel.disable();
            completeLaterPanel.enable();
        }
    }

    @Override
    public void deleteAttachment(String filename,
            AsyncCallback<Void> voidCallback) {
        attachmentService.removeIndexedDocument(filename, voidCallback);
    }

    @Override
    public void updateTranferPanel() {
        UploadCallback callback = new UploadCallback(this);
        attachmentService.getIndexedDocumentList(callback);

    }

    @Override
    public void addErrorMessage(String message) {
        LoadCallback loadCallback = new LoadCallback(this);
        commonService.getMessage(message, loadCallback);
    }

    @Override
    public void notify(String message) {
        messagePanel.setError(message);
    }

    @Override
    public void clearMessage() {
        messagePanel.clear();
    }

    @Override
    public void notifyUploadedDocuments(List<IndexEntryData> data) {
        for (IndexEntryData indexEntryData : data) {
            FileUploadData fileUploadData = new FileUploadData();
            fileUploadData.setUploaded(true);
            mappingList.put(indexEntryData.getType(), fileUploadData);
        }
        initWidget();
        VoidCallback voidCallback = new VoidCallback(this);
        attachmentService.notifyUploadedDocuments(voidCallback);

    }

    /* ********************* PROTECTED/PRIVATE METHODS ****************** */

    @Override
    protected void initWidget() {
        if (!embedded) {
            int detectedAcrobat = verifierAdobeReader();
            Log.debug(UiFrame.class.getName(), "function verifierAdobeReader returned: "
                    + detectedAcrobat);
        }

        Map<String, String> wordingMap = data.getWordingMap();

        messagePanel = new MessagePanel();
        WidgetUtil.addStyle(messagePanel, Elements.TXT_ERROR_SUMMARY.getId());
        txtErrorSummary.add(messagePanel);

        // initialisation du message
        labelInit = attachmentConstants.txtFrameTitle();
        labelInit = replaceWording(wordingMap, labelInit);
        titlePanel = new TitlePanel(labelInit);
        WidgetUtil.addStyle(titlePanel, Elements.TXT_TITLE.getId());
        txtTitle.add(titlePanel);

        // initialisation du panel de transfert des PJ
        transferPanel = new TransferPanel(data, mappingList);
        transferPanel.addStyleName("transfer-frame");
        WidgetUtil.addStyle(transferPanel, Elements.FRAME_TRANSFER_PJ_DOCUMENTS.getId());
        frameTransferPJDocuments.add(transferPanel);
        this.initTransferPanel();
        transferPanel.addListener(this);

        // initialisation du panel de la liste des PJ

        listPanel = new ListPanel(data, mappingList);
        listPanel.addStyleName("list-frame");
        WidgetUtil.addStyle(listPanel, Elements.FRAME_SELECT_PJ_DOCUMENTS.getId());
        frameSelectPJDocuments.add(listPanel);
        listPanel.addListener(this);

        // initialisation du bouton de transmission des pièces justificatives
        String labelButton = attachmentConstants.btnTransmitLabel();
        labelButton = replaceWording(wordingMap, labelButton);
        validationPanel = new ValidationPanel(labelButton);
        WidgetUtil.addStyle(validationPanel, Elements.BTN_TRANSMIT.getId());
        btnTransmit.add(validationPanel);
        validationPanel.addListener(this);

        // initialisation du bouton de retour
        String labelBack = attachmentConstants.btnBackLabel();
        labelBack = replaceWording(wordingMap, labelBack);
        backPanel = new BackPanel(labelBack);
        backPanel.addStyleName("cancel");
        WidgetUtil.addStyle(backPanel, Elements.BTN_BACK.getId());
        btnBack.add(backPanel);
        backPanel.addListener(this);
        
        //initialisation du bouton d'ajout de PJ plus tard
        if (data.isCompleteLaterOn()) {
        	Log.info(UiFrame.class.getName(), "Button complete later is required");
        	String labelCompleteLater  = attachmentConstants.btnCompleteLaterLabel();
        	labelCompleteLater = replaceWording(wordingMap, labelCompleteLater);
        	completeLaterPanel = new CompleteLaterPanel(labelCompleteLater);
        	completeLaterPanel.addStyleName("completelater");
        	WidgetUtil.addStyle(completeLaterPanel, Elements.BTN_COMPLETE_LATER.getId());
        	btnCompleteLater.add(completeLaterPanel);
        	completeLaterPanel.addListener(this);
        }
        else {
        	Log.info(UiFrame.class.getName(), "Button complete later is not required");
        }

    }

	@Override
	public void completeLater() {
        VoidCallback callback = new VoidCallback(this);
        attachmentService.completeLater(callback);
		
	}

}
