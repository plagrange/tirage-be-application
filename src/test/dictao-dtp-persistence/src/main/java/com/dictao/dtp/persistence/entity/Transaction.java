package com.dictao.dtp.persistence.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.dictao.dtp.persistence.data.conversion.DocumentTypeListConverter;
import com.dictao.dtp.persistence.data.conversion.InvalidPersistenceDataException;

@Entity
@Table(name = "TBL_TRANSACTION")
@NamedQueries({
    @NamedQuery(name = Transaction.FIND_BY_TXID, query = "Select t from Transaction t where t.transactionID = :transactionID"),
    @NamedQuery(name = Transaction.COUNT_TX, query = "Select count(t) from Transaction t where t.updateTimestamp >= :updateTimestamp"),
    @NamedQuery(name = Transaction.UPDATE_EXPIRED, query = "Update Transaction t set t.status = :statusToSet , t.subStatus = :subStatusToSet, t.endTimestamp = :endTimestamp where t.expirationTimestamp <= :expirationTimestamp and t.applicationID = :applicationID  and not (t.status = :status)"),
    @NamedQuery(name = TransactionBase.FIND_OPEN_TX_BY_UPDATETIMERANGE, query = TransactionBase.FIND_OPEN_TX_BY_UPDATETIMERANGE_QUERY),
    @NamedQuery(name = TransactionBase.COUNT_OPEN_TX_BY_UPDATETIMERANGE, query = TransactionBase.COUNT_OPEN_TX_BY_UPDATETIMERANGE_QUERY),
    @NamedQuery(name = TransactionBase.FIND_MODIFIED_TX_BY_UPDATETIMERANGE, query = TransactionBase.FIND_MODIFIED_TX_BY_UPDATETIMERANGE_QUERY),
    @NamedQuery(name = TransactionBase.COUNT_MODIFIED_TX_BY_UPDATETIMERANGE, query = TransactionBase.COUNT_MODIFIED_TX_BY_UPDATETIMERANGE_QUERY),
    @NamedQuery(name = TransactionBase.FIND_CLOSED_TX_BY_ENDTIMERANGE, query = TransactionBase.FIND_CLOSED_TX_BY_ENDTIMERANGE_QUERY),
    @NamedQuery(name = TransactionBase.COUNT_CLOSED_TX_BY_ENDTIMERANGE, query = TransactionBase.COUNT_CLOSED_TX_BY_ENDTIMERANGE_QUERY)})
public class Transaction implements Serializable {

    public static final String FIND_BY_TXID = "getTransactionByTransactionId";
    public static final String COUNT_TX = "countTransactions";
    public static final String UPDATE_EXPIRED = "updateExpired";
    //FIXME [KCH] retrieve on error transactions only.
    public static final String Q_FIND_TRANSACTIONS = "Select t From Transaction t";
    public static final String Q_COUNT_TRANSACTIONS = "Select count(DISTINCT t.id) From Transaction t";
    public static final String TRANSACTION_TABLE_ALIAS = "t.";
    private static final long serialVersionUID = 3730162062856669659L;
    // Internal Id
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    
    // Optimistic locking
    @Version
    private int version;
    // Context identification
    // ----------------------
    @Basic
    @Column(name = "TRANSACTION_ID", nullable = false)
    private String transactionID;
    @Basic
    @Column(name = "APPLICATION_ID", nullable = false)
    private String applicationID;
    @Basic
    @Column(name = "TENANT_ID", nullable = false)
    private String tenantID;
    @Basic
    @Column(name = "BUSINESS_TYPE")
    private String businessType;
    @Basic
    @Column(name = "BUSINESS_ID")
    private String businessID;
    @Basic
    @Column(name = "COMPANY")
    private String company;
    @Basic
    @Column(name = "HANDLER_NAME", nullable = false)
    private String handler;
    @Basic
    @Column(name = "SERVICE")
    private String service;
    @Basic
    @Column(name = "XML_METADATA")
    private byte[] xmlMetadata;
    @Basic
    @Column(name = "ARCHIVE_DOCUMENT_TYPE_LIST")
    private byte[] rawArchiveDocumentTypeList;
    @Transient
    private List<String> archiveDocumentTypeList;
    @Basic
    @Column(name = "SEAL_DOCUMENT_TYPE_LIST")
    private byte[] rawSealDocumentTypeList;
    @Transient
    private List<String> sealDocumentTypeList;
    @Basic
    @Column(name = "BUSINESS_TAGS")
    private String businessTags;
    
    // Life cycle
    // ----------
    @Column(name = "START_TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTimestamp;
    @Column(name = "UPDATE_TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTimestamp;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIMESTAMP")
    private Date endTimestamp;
    @Column(name = "EXPIRATION_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTimestamp;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private TransactionStatusEnum status;
    @Enumerated(EnumType.STRING)
    @Column(name = "SUB_STATUS")
    private TransactionSubStatusEnum subStatus;
    // This state is different from status as this is a free string
    // This should be used by application to provide more precise information
    // than status...and probably linked to the business status of the
    // transaction
    @Column(name = "CURRENT_STATE")
    private String currentState;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("creationTimetamp ASC")
    private List<Step> steps;

    protected Transaction() throws InvalidPersistenceDataException {
        this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public Transaction(String transactionId, String tenant,
            String application, String businessType, String businessId,
            String company, String handler, String service, byte[] metadata,
            List<String> sealDocumentTypeList,
            List<String> archiveDocumentTypeList, String businessTags) throws InvalidPersistenceDataException {
        super();
        this.startTimestamp = new Date();
        this.updateTimestamp = new Timestamp((new Date()).getTime());
        this.endTimestamp = null;
        this.status = TransactionStatusEnum.Opened;
        this.subStatus = TransactionSubStatusEnum.Started;
        this.transactionID = transactionId;
        this.applicationID = application;
        //TO CHANGE ?
        this.tenantID = tenant == null ? application : tenant;
        this.businessType = businessType;
        this.businessID = businessId;
        this.company = company;
        this.handler = handler;
        this.service = service;
        this.xmlMetadata = metadata;
        this.rawSealDocumentTypeList = DocumentTypeListConverter.saveDocumentTypeList(sealDocumentTypeList);
        this.sealDocumentTypeList = sealDocumentTypeList;
        this.rawArchiveDocumentTypeList = DocumentTypeListConverter.saveDocumentTypeList(archiveDocumentTypeList);
        this.archiveDocumentTypeList = archiveDocumentTypeList;
        this.businessTags = businessTags;
    }

    public Long getId() {
        return id;
    }

    /**
     * @return the startTimestamp
     */
    public Date getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * @param startTimestamp the startTimestamp to set
     */
    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * @return the updateTimestamp
     */
    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    /**
     * @param updateTimestamp the updateTimestamp to set
     */
    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    /**
     * @return the endTimestamp
     */
    public Date getEndTimestamp() {
        return endTimestamp;
    }

    /**
     * @param endTimestamp the endTimestamp to set
     */
    public void setEndTimestamp(Date endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    /**
     * @return the state
     */
    public TransactionStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the state to set
     */
    public void setStatus(TransactionStatusEnum status) {
        this.status = status;
    }

    /**
     * @return the currentState
     */
    public String getCurrentState() {
        return currentState;
    }

    /**
     * @param currentState the currentState to set
     */
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    /**
     * @return the transactionID
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * @return the applicationID
     */
    public String getApplicationID() {
        return applicationID;
    }

    /**
     * @param applicationID the applicationID to set
     */
    public void setApplicationID(String application) {
        this.applicationID = application;
    }

    /**
     * @return the tenantID
     */
    public String getTenant() {
        return tenantID;
    }

    /**
     * @param tenantID the tenantID to set
     */
    public void setTenant(String tenant) {
        this.tenantID = tenant;
    }

    /**
     * @return the businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * @param businessType the businessType to set
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * @return the businessID
     */
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param businessID the businessID to set
     */
    public void setBusinessID(String businessID) {
        this.businessID = businessID;
    }

    /**
     * @return The company.
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return the transaction's metadata
     */
    public byte[] getMetadata() {
        return xmlMetadata;
    }

    /**
     * @param metadata to set
     */
    public void setMetadata(byte[] metadata) {
        this.xmlMetadata = metadata;
    }

    public void addStep(Step step) {
        // FIXME need null test ?
        if (null == steps) {
            steps = new ArrayList<Step>();
        }
        this.setUpdateTimestamp(new Date());
        steps.add(step);
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Step getLastStep() {
        return steps.get(steps.size() - 1);
    }

    /**
     * @param step
     * @return times that step set appears in step list.
     */
    public int countStep(StepNameEnum step) {
        int count = 0;
        for (Step s : steps) {
            if (s.getName().equals(step)) {
                count += 1;
            }
        }
        return count;
    }

    /**
     *
     * @param step
     * @return list step by name
     */
    public List<Step> getStepsByName(StepNameEnum step) {
        List<Step> tmp = new ArrayList<Step>();
        for (Step s : steps) {
            if (s.getName().equals(step)) {
                tmp.add(s);
            }
        }
        return tmp;
    }

    /**
     * @param step step name
     * @return last step entry appears in step list.
     */
    public Step getLastStep(StepNameEnum step) {
        Step lastStep = null;
        for (Step s : steps) {
            if (s.getName().equals(step)) {
                lastStep = s;
            }
        }
        return lastStep;
    }

    /**
     * @param step
     * @return True if transaction has step named as provided stepName, FALSE
     * otherwise.
     */
    public boolean doesExistStep(StepNameEnum stepName) {
        for (Step s : steps) {
            if (s.getName().equals(stepName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param step
     * @return first step entry matches provided stepName
     */
    public Step getFistStep(StepNameEnum stepName) {
        for (Step s : steps) {
            if (s.getName().equals(stepName)) {
                return s;
            }
        }
        return null;
    }

    public int getVersion() {
        return version;
    }

    /**
     * @return the handler
     */
    public String getHandler() {
        return handler;
    }

    /**
     * @param handler the handler to set
     */
    public void setHandler(String handler) {
        this.handler = handler;
    }

    /**
     * @return the subStatus
     */
    public TransactionSubStatusEnum getSubStatus() {
        return subStatus;
    }

    /**
     * @param subStatus the subStatus to set
     */
    public void setSubStatus(TransactionSubStatusEnum subStatus) {
        this.subStatus = subStatus;
    }

    /**
     * @return the expirationTimestamp
     */
    public Date getExpirationTimestamp() {
        return expirationTimestamp;
    }

    /**
     * @param expirationTimestamp the expirationTimestamp to set
     */
    public void setExpirationTimestamp(Date expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    public List<String> getSealDocumentTypeList() throws InvalidPersistenceDataException {
        if(sealDocumentTypeList == null) {
            sealDocumentTypeList =  DocumentTypeListConverter.loadDocumentTypeList(rawSealDocumentTypeList);
        }
        return sealDocumentTypeList;
    }

    public void setSealDocumentTypeList(List<String> sealDocumentTypeList) throws InvalidPersistenceDataException {
        this.sealDocumentTypeList = sealDocumentTypeList;
        rawSealDocumentTypeList = DocumentTypeListConverter.saveDocumentTypeList(sealDocumentTypeList);
    }

    public List<String> getArchiveDocumentTypeList() throws InvalidPersistenceDataException {
        if(archiveDocumentTypeList == null) {
            return archiveDocumentTypeList = DocumentTypeListConverter.loadDocumentTypeList(rawArchiveDocumentTypeList);
        }
        return archiveDocumentTypeList;
    }

    public void setArchiveDocumentTypeList(List<String> archiveDocumentTypeList) throws InvalidPersistenceDataException {
        this.archiveDocumentTypeList = archiveDocumentTypeList;
        rawArchiveDocumentTypeList = DocumentTypeListConverter.saveDocumentTypeList(archiveDocumentTypeList);
    }
    
    @Override
    public String toString() {
        return "Transaction [id=" + id + ", version=" + version
                + ", transactionID=" + transactionID + ", application="
                + applicationID + ", tenant=" + tenantID + ", businessType="
                + businessType + ", businessID=" + businessID + ", company="
                + company + ", handler=" + handler + ", startTimestamp="
                + startTimestamp + ", updateTimestamp=" + updateTimestamp
                + ", endTimestamp=" + endTimestamp + ", expirationTimestamp="
                + expirationTimestamp + ", status=" + status + ", subStatus="
                + subStatus + ", currentState=" + currentState + ", service=" 
                + service + ", businessTags=" + businessTags + "]";
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return the businessTags
     */
    public String getBusinessTags() {
        return businessTags;
    }

    /**
     * @param businessTags the businessTags to set
     */
    public void setBusinessTags(String businessTags) {
        this.businessTags = businessTags;
    }
}
