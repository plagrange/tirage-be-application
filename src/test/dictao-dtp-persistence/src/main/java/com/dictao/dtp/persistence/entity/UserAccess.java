package com.dictao.dtp.persistence.entity;

import com.dictao.dtp.persistence.data.AuthenticationInfo;
import com.dictao.dtp.persistence.data.PersonalInfo;
import com.dictao.dtp.persistence.data.UIInfo;
import com.dictao.dtp.persistence.data.conversion.AuthenticationInfoConverter;
import com.dictao.dtp.persistence.data.conversion.PersonalInfoConverter;
import com.dictao.dtp.persistence.data.conversion.UIInfoConverter;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author msauvee
 */
@Entity
@Table(name = "TBL_USERACCESS")
@NamedQueries({
@NamedQuery(name=UserAccess.FIND_BY_ACCESSID,query="Select ua from UserAccess ua where ua.accessID = :accessID"),
@NamedQuery(name=UserAccess.FIND_BY_APP_AND_EXTID,query="Select ua from UserAccess ua where ua.applicationID = :applicationID and ua.externalID = :externalID order by ua.updateTimestamp desc"),
@NamedQuery(name=UserAccess.FIND_BY_TRANSACTIONID,query="Select ua from UserAccess ua where ua.transaction.transactionID = :transactionID order by ua.updateTimestamp desc")
})
public class UserAccess implements Serializable {

    public static final String FIND_BY_ACCESSID = "getUserAccessByAccessId";
    public static final String FIND_BY_APP_AND_EXTID = "getUserAccessByAppIdAndExtId";
    public static final String FIND_BY_TRANSACTIONID = "findUserAccessesByTransactionId";
   
    private static final long serialVersionUID = -3641992073505864620L;

    // Internal Id
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Optimistic locking
    @SuppressWarnings("unused")
    @Version
    private int version;

    // Context identification
    // ----------------------
    @Basic
    @Column(name = "ACCESS_ID", nullable = false)
    private String accessID;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="PARENT_ID")
    private Set<UserAccess> childs;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TRANSACTION_ID")
    private Transaction transaction;

    @Basic
    @Column(name = "APPLICATION_ID", nullable = false)
    private String applicationID;

    @Basic
    @Column(name = "EXTERNAL_ID")
    private String externalID;

    @Column(name = "USER_TYPE")
    @Deprecated
    private String userType;

    // Life cycle
    // ----------
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private UserAccessStatusEnum status;
    @Enumerated(EnumType.STRING)
    @Column(name = "SUB_STATUS")
    private UserAccessSubStatusEnum subStatus;

    // Other
    // -----
    @Column(name = "AAL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date anonymousAccessLimit;

    @Basic
    @Column(name = "WORKFLOW", nullable = true)
    @Deprecated
    private String workflow;

    @Basic
    @Column(name = "BACK_URL", nullable = true)
    @Deprecated
    private String backUrl;

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "METADATAS", nullable = true)
    @Lob
    @Deprecated
    private byte[] metadatas;

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "XML_METADATA")
    @Lob
    private byte[] xmlMetadata;

    @Column(name = "UPDATE_TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTimestamp;
   
    @Basic
    @Column(name = "SINGLE_USAGE")
    private boolean singleUsage;
    
    @Basic
    @Column(name = "UI_INFO", nullable = false)
    private byte[] rawUIInfo;
    
    @Transient
    private UIInfo uiInfo;
    
    @Basic
    @Column(name = "PERSONAL_INFO", nullable = false)
    private byte[] rawPersonalInfo;
    
    @Transient
    private PersonalInfo personalInfo;
    
    @Basic
    @Column(name = "AUTHENTICATION_INFO", nullable = true)
    private byte[] rawAuthenticationInfo;
    
    @Transient
    private AuthenticationInfo authenticationInfo;

    protected UserAccess() {
        this("", null, null, null, null);
    }
    
    public UserAccess(
            String accessId, Transaction transaction, UIInfo uiInfo,
            PersonalInfo personalInfo, AuthenticationInfo authenticationInfo) {
        this.accessID = accessId;
        this.status = UserAccessStatusEnum.Opened;
        this.subStatus = UserAccessSubStatusEnum.Created;
        this.applicationID = transaction != null ? transaction.getApplicationID() : null;
		this.status = UserAccessStatusEnum.Opened;
        this.anonymousAccessLimit = null;
        this.transaction = transaction;
        this.updateTimestamp = new Date();
        this.singleUsage = true;
        this.rawUIInfo = UIInfoConverter.saveUIInfo(uiInfo);
        this.uiInfo = uiInfo;
        this.rawPersonalInfo = PersonalInfoConverter.savePersonalInfo(personalInfo);
        this.personalInfo = personalInfo;
        this.rawAuthenticationInfo = AuthenticationInfoConverter.saveAuthenticationInfo(authenticationInfo);
        this.authenticationInfo = authenticationInfo;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the transaction
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * @param transaction the transaction to set
     */
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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
     * @return the externalID
     */
    public String getExternalID() {
        return externalID;
    }

    /**
     * @param externalID the externalID to set
     */
    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    /**
     * @return the childs
     */
    public Set<UserAccess> getChilds() {
        return childs;
    }

    /**
     * @param child the child to add
     */
    public void addChild(UserAccess child) {
        if (childs == null) {
            childs = new HashSet<UserAccess>();
        }
        childs.add(child);
    }

    /**
     * @return the singleUsage
     */
    public boolean isSingleUsage() {
        return singleUsage;
    }
    /**
     * @param singleUsage the singleUsage to set
     */
    public void setSingleUsage(boolean singleUsage) {
        this.singleUsage = singleUsage;
    }
    /**
     * @return the accessID
     */
    public String getAccessID() {
        return accessID;
    }

    /**
     * @param accessID the accessID to set
     */
    public void setAccessID(String accessID) {
        this.accessID = accessID;
    }

        /**
     * @return the anonymousAccessLimit
     */
    public Date getAnonymousAccessLimit() {
        return anonymousAccessLimit;
    }

    /**
     * @param anonymousAccessLimit the anonymousAccessLimit to set
     */
    public void setAnonymousAccessLimit(Date anonymousAccessLimit) {
        this.anonymousAccessLimit = anonymousAccessLimit;
    }

    /**
     * @return the metadatas
     */
    @Deprecated
    public byte[] getMetadatas() {
        return metadatas;
    }

    /**
     * @param metadatas the metadatas to set
     */
    @Deprecated
    public void setMetadatas(byte[] metadatas) {
        this.metadatas = metadatas;
    }
    
    /**
     * @return the xmlMetadata
     */
    public byte[] getXmlMetadata() {
        return xmlMetadata;
    }

    /**
     * @param xmlMetadata the xmlMetadata to set
     */
    public void setXmlMetadata(byte[] xmlMetadata) {
        this.xmlMetadata = xmlMetadata;
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
     * @return the status
     */
    public UserAccessStatusEnum getStatus() {
        return status;
    }

    /**
     * @return the subStatus
     */
    public UserAccessSubStatusEnum getSubStatus() {
        return subStatus;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(UserAccessStatusEnum status, UserAccessSubStatusEnum subStatus) {
        if (status == UserAccessStatusEnum.Closed && singleUsage) {
            anonymousAccessLimit = null;
        }
        this.status = status;
        this.subStatus = subStatus;
    }

    public UIInfo getUIInfo() {
        
        if(uiInfo != null) {
            return uiInfo;
        }
        
        if(rawUIInfo != null) {
            uiInfo = UIInfoConverter.loadUIInfo(rawUIInfo);
            return uiInfo;
        }   
        // Load ui info from old-style attributes
        uiInfo = new UIInfo(workflow, null, null, null, null, backUrl);
        rawUIInfo = UIInfoConverter.saveUIInfo(uiInfo);

        // Clear old-style attributes
        backUrl = null;
        workflow = null;

        return uiInfo;
    }

    public void setUIInfo(UIInfo uiInfo) {
        
        this.rawUIInfo = UIInfoConverter.saveUIInfo(uiInfo);
        this.uiInfo = uiInfo;
        
        // Clear old-style attributes
        backUrl = null;
        workflow = null;
    }

    public PersonalInfo getPersonalInfo() {
        
        if(personalInfo != null) {
            return personalInfo;
        }
        
        if(rawPersonalInfo != null) {
            personalInfo = PersonalInfoConverter.loadPersonalInfo(rawPersonalInfo);
            return personalInfo;
        } 
            
        // Load personal info from old-style attributes
        personalInfo = new PersonalInfo(userType, null, null, null, null, null, false);
        rawPersonalInfo = PersonalInfoConverter.savePersonalInfo(personalInfo);

        // Clear old-style attributes
        userType = null;

        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        
        this.rawPersonalInfo = PersonalInfoConverter.savePersonalInfo(personalInfo);
        this.personalInfo = personalInfo;
        
        // Clear old-style attributes
        userType = null;
    }
    
    public AuthenticationInfo getAuthenticationInfo() {
        
        if(authenticationInfo != null) {
            return authenticationInfo;
        }
        if(rawAuthenticationInfo != null)
        {
            authenticationInfo = AuthenticationInfoConverter.loadAuthenticationInfo(rawAuthenticationInfo);
            return authenticationInfo;
        }
        return null;
    }

    public void setAuthenticationInfo(AuthenticationInfo authenticationInfo) {
        this.rawAuthenticationInfo = AuthenticationInfoConverter.saveAuthenticationInfo(authenticationInfo);
        this.authenticationInfo = authenticationInfo;
    }
}
