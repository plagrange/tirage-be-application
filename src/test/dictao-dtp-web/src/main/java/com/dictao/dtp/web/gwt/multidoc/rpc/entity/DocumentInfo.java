package com.dictao.dtp.web.gwt.multidoc.rpc.entity;

import java.io.Serializable;


public class DocumentInfo implements Serializable{

    /** 
     */
    private static final long serialVersionUID = -3258738501626446156L;
    private String label;
    private String url;
    private boolean signed;
    private String params;
    
    //empty constructor for GWT-RPC call
    public DocumentInfo() {}

    public DocumentInfo(String label, String url, String params, boolean signed ) {
        this.label = label;
        this.url = url;
        this.signed = signed;
        this.params = params;
    }

    public boolean isSigned(){
        return signed;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public String getPdfDisplayParameters() {
        return params;
    }


}
