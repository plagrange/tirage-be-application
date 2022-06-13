package com.dictao.dtp.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

@Entity
@Table(name = "TBL_STORAGE", uniqueConstraints = { @UniqueConstraint(columnNames = { "CLM_TRANSACTION_ID" }) })
public class Archive implements Serializable {
    private static final long serialVersionUID = -9200919647407898175L;

    @Id
    @Column(name = "CLM_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "CLM_VERSION", nullable = false)
    @Version
    private int version;

    @Basic
    @Column(name = "CLM_TRANSACTION_ID", nullable = false)
    private String transactionID;

    @Basic
    @Column(name = "CLM_APPLICATION_ID", nullable = false)
    private String applicationID;

    @Basic
    @Column(name = "CLM_STORAGE_ID", nullable = false)
    private String storageID;

    @Basic
    @Column(name = "CLM_DELIVERY_ID")
    private String deliveryID;

    @Lob
    @Column(name = "CLM_DELIVERY_ITEM", nullable = false)
    private byte[] deliveryItem;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CLM_CREATE_DATE", nullable = false)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CLM_ARCHIVE_DATE")
    private Date storageDate;

    @Lob
    @Column(name = "CLM_METADATAS", nullable = true)
    private String storageMetadatas;

    // constructeurs
    public Archive() {
        super();
        transactionID = "";
        applicationID = "";
        storageID = "";
        deliveryID = "";
        deliveryItem = null;
        createDate = new Date();
        storageDate = null;
    }

    public void onStorage(String deliveryID, Date storageDate) {
        this.setDeliveryID(deliveryID);
        this.setStorageDate(storageDate);
    }

    public Integer getId() {
        return id;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the transactionID
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * @param transactionID
     *            the transactionID to set
     */
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    /**
     * @return the applicationID
     */
    public String getApplicationID() {
        return applicationID;
    }

    /**
     * @param applicationID
     *            the applicationID to set
     */
    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public void setStorageMetadatas(String storageMetadatas) {
        this.storageMetadatas = storageMetadatas;
    }

    /**
     * @return the storageID
     */
    public String getStorageID() {
        return storageID;
    }

    /**
     * @param storageID
     *            the storageID to set
     */
    public void setStorageID(String storageID) {
        this.storageID = storageID;
    }

    /**
     * @return the deliveryID
     */
    public String getDeliveryID() {
        return deliveryID;
    }

    /**
     * @param deliveryID
     *            the deliveryID to set
     */
    public void setDeliveryID(String deliveryID) {
        this.deliveryID = deliveryID;
    }

    /**
     * @return the deliveryItem
     */
    public byte[] getDeliveryItem() {
        return deliveryItem;
    }

    /**
     * @param deliveryItem
     *            the deliveryItem to set
     */
    public void setDeliveryItem(byte[] deliveryItem) {
        this.deliveryItem = deliveryItem;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     *            the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the storageDate
     */
    public Date getStorageDate() {
        return storageDate;
    }

    /**
     * @param storageDate
     *            the storageDate to set
     */
    public void setStorageDate(Date archiveDate) {
        this.storageDate = archiveDate;
    }

    public String getStorageMetadatas() {
        return storageMetadatas;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Store [applicationID=" + applicationID + ", storageDate="
                + storageDate + ", createDate=" + createDate + ", deliveryID="
                + deliveryID + ", id=" + id + ", storageID=" + storageID
                + ", transactionID=" + transactionID + ", version=" + version
                + "]";
    }

}