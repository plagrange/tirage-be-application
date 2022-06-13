package com.dictao.dtp.persistence.data;

import java.util.List;

public class UIInfo {

    final private String ui;
    final private String label;
    final private String type;
    final private String consent;
    final private String termAndConditionsUrl;
    final private String backUrl;
    private List<String> documentTypes;

    public UIInfo(String ui, String label, String type, String consent,
            String termAndConditionsUrl, String backUrl) {
    	
    	this(ui, label, type, consent, termAndConditionsUrl, backUrl,null);

    }
    /**
     * Must be immutable
     * @param ui
	 * @param label
	 * @param type
	 * @param consent
	 * @param termAndConditionsUrl
	 * @param backUrl
	 * @param documentTypes TODO
     */
    public UIInfo(String ui, String label, String type, String consent,
            String termAndConditionsUrl, String backUrl, List<String> documentTypes) {
        
        this.ui = ui;
        this.label = label;
        this.type = type;
        this.consent = consent;
        this.termAndConditionsUrl = termAndConditionsUrl;
        this.backUrl = backUrl;      
        this.documentTypes = documentTypes;
    }
    
   
    /**
     * @return the ui
     */
    public String getUi() {
        return ui;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the consent
     */
    public String getConsent() {
        return consent;
    }
    
    /**
     * @return the termAndConditionsUrl
     */
    public String getTermAndConditionsUrl() {
        return termAndConditionsUrl;
    }

    /**
     * @return the backUrl
     */
    public String getBackUrl() {
        return backUrl;
    }
    
    public List<String> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(List<String> documentTypes) {
		this.documentTypes = documentTypes;
	}
}
