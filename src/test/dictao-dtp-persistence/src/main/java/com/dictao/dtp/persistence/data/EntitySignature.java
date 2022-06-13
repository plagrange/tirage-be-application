package com.dictao.dtp.persistence.data;

public class EntitySignature {
    
    private VisibleSignatureField visibleSignatureField;
    
    public EntitySignature(){
        
    }
    
    public EntitySignature(VisibleSignatureField visibleSignatureField){
        this.visibleSignatureField = visibleSignatureField;
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

}
