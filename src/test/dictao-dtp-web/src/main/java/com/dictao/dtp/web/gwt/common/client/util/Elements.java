package com.dictao.dtp.web.gwt.common.client.util;

public enum Elements
{
    AUTH_INPUT_OTP("authInputOTP"),
    AUTH_TXT_OTP("authTxtOTP"),
    AUTH_OTP_SUBMIT("authOTPSubmit"),
    APPLET_ADISGNER_PDF("adsignerPDF"),
    LNK_VIS_DOC("lnkVisDoc"),
    BTN_VIS_DOC1("btnVisDoc1"),
    BTN_VIS_DOC2("btnVisDoc2"),
    CHECK_BOXES_WITH_BROKER("checkBoxesWithBroker"),
    CHECK_BOXES_WITHOUT_BROKER("checkBoxesWithoutBroker"),
    CHECK_BOXES_TXT("checkBoxesTxt"),
    INPUT_OTP("inputOTP"),
    INPUT_OTP_TXT("inputOTPTxt"),
    INPUT_OTP_TXT_OP("inputOTPTxtOp"),
    TXT_POPUP("txtPopup"),
    BTN_SUBMIT("btnSubmit"),
    BTN_BACK("btnBack"),
    BTN_BACK_CENTER("btnBackCenter"),
    BTN_SUBMITPJ("btnSubmitPJ"),
    BTN_COMPLETE_LATER("btnCompleteLater"),
    FRAME_SHOW_DOCUMENT("frameShowDocument"),
    FRAME_NOT_SHOW_DOCUMENT("frameNotShowDocument"),
    FRAME_SHOW_MULTIDOCUMENT("frameShowMultiDocument"),
    BTN_NAV_MULTIDOC("btnNavMultiDocument"),
    TXT_RESUME_USER1("txtResumeUser1"),
    TXT_RESUME_USER0("txtResumeUser0"),
    TXT_BACK("txtBack"),
    TXT_TITLE_1("txtTitle1"),
    TXT_INFO_1("txtInfo1"),
    TXT_TITLE_SIGN("txtTitleSign"),
    TXT_ETAPES("txtEtapes"),
    TXT_ERROR_SUMMARY("txtErrorSummary"),
    TXT_ERROR_SUMMARY_ALL("txtErrorRemAll"),
    TXT_ERROR_SUMMARY_MUL("txtErrorRemMul"),
    TXT_ERROR_SUMMARY_ONE("txtErrorRemOne"),
    TXT_ERROR_SUMMARY_REGEX("txtErrorRegex"),
    TXT_ERROR_SUMMARY_BAD_OTP_CHAR("txtErrorBadOtpChar"),
    TXT_ERROR_SUMMARY_BAD_OTP_LENGTH("txtErrorBadOtpLength"),
    TXT_ERROR_SUMMARY_EXPIRED_OTP("txtErrorExpiredOtp"),
    TXT_INFO_TITLE("infoTitle"),
    TXT_INFO_CONTENT("infoContent"),
    TXT_LINK_1("linkPanel1"),
    TXT_LINK_2("linkPanel2"),
    TXT_FOLDER("txtFolder"),
    FRAME_SELECT_PJ_DOCUMENTS("frameSelectPJDocuments"),
    FRAME_TRANSFER_PJ_DOCUMENTS("frameTransferPJDocuments"),
    BTN_TRANSMIT("btnTransmit"),
    BTN_BROWSE("btnBrowse"),
    TXT_STEP_ONE("txtStepOne"),
    TXT_STEP_TWO("txtStepTwo"),
    TXT_STATE_TRANSMIT("txtStateTransmit"),
    TXT_TITLE("txtTitle"),
    TXT_BROCKER("txtBrocker");
    
    private final String key;

    private Elements(String key)
    {
        this.key = key;
    }

    public String getId()
    {
        return this.key;
    }

}
