package com.dictao.dtp.persistence.data;

public class PersonalSignature {
    
    private VisibleSignatureField visibleSignatureField;
    private String user;
    private String signatureLabel;

    public PersonalSignature(VisibleSignatureField visibleSignatureField,
            String user, String signatureLabel) {
        this.visibleSignatureField = visibleSignatureField;
        this.user = user;
        this.signatureLabel = signatureLabel;
    }
    
    /**
     * @return the visibleSignatureField
     */
    public VisibleSignatureField getVisibleSignatureField() {
        return visibleSignatureField;
    }

    /**
     * @param visibleSignatureField the visibleSignatureField to set
     */
    public void setVisibleSignatureField(VisibleSignatureField visibleSignatureField) {
        this.visibleSignatureField = visibleSignatureField;
    }

    
    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the signatureLabel
     */
    public String getSignatureLabel() {
        return signatureLabel;
    }

    /**
     * @param signatureLabel the signatureLabel to set
     */
    public void setSignatureLabel(String signatureLabel) {
        this.signatureLabel = signatureLabel;
    }

}
