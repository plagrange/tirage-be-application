package com.dictao.dtp.web.proofexplorer;

public class ProofItem {
    private String label;
    private String description;
    private String fileName;
    private String urlExtention;
    private boolean dvsVisualizableProof;

    public ProofItem(String label, String description, String fileName, boolean dvsVisualizableProof, String urlExtention) {
        this.label = label;
        this.description = description;
        this.fileName = fileName;
        this.dvsVisualizableProof = dvsVisualizableProof;
        this.urlExtention = urlExtention;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return wheter the item if a proof that can be visualized by DVS
     */
    public boolean isDvsVisualizableProof() {
        return dvsVisualizableProof;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the urlExtention
     */
    public String getUrlExtention() {
        return urlExtention;
    }

    /**
     * @param urlExtention the urlExtention to set
     */
    public void setUrlExtention(String urlExtention) {
        this.urlExtention = urlExtention;
    }
}
