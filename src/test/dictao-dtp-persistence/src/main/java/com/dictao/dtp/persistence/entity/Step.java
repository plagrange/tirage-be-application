package com.dictao.dtp.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
@Table(name = "TBL_STEP")
public class Step implements Serializable {
    /*
     * **************************** CONSTANTS ***************************
     */

    private static final long serialVersionUID = 7947306575427756291L;

    /*
     * **************************** ATTRIBUTES **************************
     */
    // Internal Id
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // Optimistic locking
    @Version
    private int version;
    @Enumerated(EnumType.STRING)
    @Column(name = "NAME", nullable = false)
    private StepNameEnum name;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_TIMESTAMP", nullable = false)
    private Date creationTimetamp;
    @Lob
    @Column(name = "INFORMATION")
    private String information;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID")
    private Transaction transaction;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USERACCESS_ID")
    private UserAccess userAccess;
    @Basic
    @Column(name = "DOC_ECMSVC_NAME")
    private String ecmServiceName;
    @Basic
    @Column(name = "DOC_FILENAME")
    private String documentFilename;
    @Basic
    @Column(name = "DOC_TYPE")
    private String documentType;
    @Basic
    @Column(name = "DOC_HASH")
    private String documentHash;
    @Basic
    @Column(name = "INPUT_REQUEST_ADDRESS")
    private String InputRequestAddress;
    @Basic
    @Column(name = "INPUT_REQUEST_HOST")
    private String InputRequestHost;
    @Basic
    @Column(name = "INPUT_REQUEST_PORT")
    private int InputRequestPort;
    @Basic
    @Column(name = "OUTPUT_SERVICE_NAME")
    private String outputServiceName;
    @Basic
    @Column(name = "OUTPUT_SERVICE_DESCRIPTION")
    private String outputServiceDescription;
    @Lob
    @Column(name = "OUTPUT_SERVICE_PARAMETERS")
    private String outputServiceParameters;
    @Lob
    @Column(name = "OUTPUT_SERVICE_CONTEXT")
    private String outputServiceContext;
    @Basic
    @Column(name = "OUTPUT_SERVER_NAME")
    private String outputServerName;
    @Basic
    @Column(name = "OUTPUT_SERVER_ENDPOINT")
    private String outputServerEndpoint;
    @Basic
    @Column(name = "OUTPUT_SERVER_PARAMETERS")
    private String outputServerParameters;
    @Basic
    @Column(name = "CLIENT_SSL_CERTIFICATE_DN")
    private String clientSslCertificateDN;
    @Basic
    @Column(name = "CLIENT_SSL_CERTIFICATE_HASH")
    private String clientSslCertificateHash;

    /*
     * ************************** PUBLIC METHODS ************************
     */
    // constructeurs
    public Step(StepNameEnum name, String information, Transaction transaction) {
        this.name = name;
        creationTimetamp = new Date();
        this.information = information;
        this.transaction = transaction;
    }

    /**
     * @return the name
     */
    public StepNameEnum getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(StepNameEnum name) {
        this.name = name;
    }

    /**
     * @return the startTimestamp
     */
    public Date getCreationTimestamp() {
        return creationTimetamp;
    }

    /**
     * @param startTimestamp the startTimestamp to set
     */
    public void setCreationTimestamp(Date creationTimetamp) {
        this.creationTimetamp = creationTimetamp;
    }

    /**
     * @return the information
     */
    public String getInformation() {
        return information;
    }

    /**
     * @param information the information to set
     */
    public void setInformation(String information) {
        this.information = information;
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
     * @return the userAccess
     */
    public UserAccess getUserAccess() {
        return userAccess;
    }

    /**
     * @param userAccess the userAccess to set
     */
    public void setUserAccess(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

    /**
     * @return the documentFilename
     */
    public String getDocumentFilename() {
        return documentFilename;
    }

    /**
     * @return the documentType
     */
    public String getDocumentType() {
        return documentType;
    }
    
    /**
     * @return the documentHash
     */
    public String getDocumentHash() {
        return documentHash;
    }

    /**
     * @param documentFilename the documentFilename to set
     */
    public void setDocument(String ecmServiceName, String documentFilename, String documentType, String documentHash) {
        this.documentFilename = documentFilename;
        this.documentType = documentType;
        this.ecmServiceName = ecmServiceName;
        this.documentHash = documentHash;
    }

    /**
     * @return the ecmServiceName
     */
    public String getEcmServiceName() {
        return ecmServiceName;
    }

    /**
     * Returns the Internet Protocol (IP) address of the client or the last proxy.
     *
     * @return The IP address.
     */
    public String getInputRequestAddress() {
        return InputRequestAddress;
    }

    /**
     * Set the Internet Protocol (IP) address of the client or the last proxy.
     *
     * @param remoteAddr The IP address.
     */
    public void setInputRequestAddress(String inputRequestAddress) {
        InputRequestAddress = inputRequestAddress;
    }

    /**
     * Returns the fully qualified name of the client or the last proxy.
     *
     * @return The name.
     */
    public String getInputRequestHost() {
        return InputRequestHost;
    }

    /**
     * Set the fully qualified name of the client or the last proxy.
     *
     * @param remoteHost The name.
     */
    public void setInputRequestHost(String inputRequestHost) {
        InputRequestHost = inputRequestHost;
    }

    /**
     * Returns the Internet Protocol (IP) source port of the client or last proxy.
     *
     * @return The port.
     */
    public int getInputRequestPort() {
        return InputRequestPort;
    }

    /**
     * Set the Internet Protocol (IP) source port of the client or last proxy.
     *
     * @param remotePort The port.
     */
    public void setInputRequestPort(int inputRequestPort) {
        InputRequestPort = inputRequestPort;
    }

    /**
     * Returns the output service name. Attribute name of the service DTP configuration.
     *
     * @return Service name.
     */
    public String getOutputServiceName() {
        return outputServiceName;
    }

    /**
     * Set the service name.
     *
     * @param outputServiceName Service name.
     */
    public void setOutputServiceName(String outputServiceName) {
        this.outputServiceName = outputServiceName;
    }

    /**
     * Returns the output service description. Attribute description of the service DTP configuration.
     *
     * @return The service description.
     */
    public String getOutputServiceDescription() {
        return outputServiceDescription;
    }

    /**
     * Set the output service description.
     *
     * @param outputServiceDescription The service description.
     */
    public void setOutputServiceDescription(String outputServiceDescription) {
        this.outputServiceDescription = outputServiceDescription;
    }

    /**
     * Returns output service parameters. All parameters used by the service.
     *
     * @return Service parameters.
     */
    public String getOutputServiceParameters() {
        return outputServiceParameters;
    }

    /**
     * Set output service parameters.
     *
     * @param outputServiceParameters Service parameters.
     */
    public void setOutputServiceParameters(String outputServiceParameters) {
        this.outputServiceParameters = outputServiceParameters;
    }

    /**
     * Returns output service context. All dynamic parameters used by the service.
     *
     * @return Service context.
     */
    public String getOutputServiceContext() {
        return outputServiceContext;
    }

    /**
     * Set output service context.
     *
     * @param outputServiceContext Service context.
     */
    public void setOutputServiceContext(String outputServiceContext) {
        this.outputServiceContext = outputServiceContext;
    }

    /**
     * Return the output server name. Attribute id of the server DTP configuration.
     *
     * @return Server name.
     */
    public String getOutputServerName() {
        return outputServerName;
    }

    /**
     * Set output server name.
     *
     * @param outputServerName Server name.
     */
    public void setOutputServerName(String outputServerName) {
        this.outputServerName = outputServerName;
    }

    /**
     * Returns output server endpoint. Attribute endpoint of the server DTP configuration.
     *
     * @return Server endpoint.
     */
    public String getOutputServerEndpoint() {
        return outputServerEndpoint;
    }

    /**
     * Set output server endpoint.
     *
     * @param outputServerEndpoint Server endpoint.
     */
    public void setOutputServerEndpoint(String outputServerEndpoint) {
        this.outputServerEndpoint = outputServerEndpoint;
    }

    /**
     * Returns output server parameters. All parameters used by the server call.
     *
     * @return Server parameters.
     */
    public String getOutputServerParameters() {
        return outputServerParameters;
    }

    /**
     * Set output server parameters.
     *
     * @param outputServerParameters Server parameters.
     */
    public void setOutputServerParameters(String outputServerParameters) {
        this.outputServerParameters = outputServerParameters;
    }

    @Override
    public String toString() {
        return "Step [id=" + id + ", version=" + version + ", name=" + name
                + ", creationTimetamp=" + creationTimetamp + ", information="
                + information + ", transactionID=" + transaction.getTransactionID()
                + ", userAccess=" + userAccess + ", ecmServiceName="
                + ecmServiceName + ", documentFilename=" + documentFilename
                + ", documentType=" + documentType
                + ", documentHash=" + documentHash
                + ", InputRequestAddress=" + InputRequestAddress
                + ", InputRequestHost=" + InputRequestHost
                + ", InputRequestPort=" + InputRequestPort
                + ", outputServiceName=" + outputServiceName
                + ", outputServiceDescription=" + outputServiceDescription
                + ", outputServiceParameters=" + outputServiceParameters
                + ", outputServiceContext=" + outputServiceContext
                + ", outputServerName=" + outputServerName
                + ", outputServerEndpoint=" + outputServerEndpoint
                + ", outputServerParameters=" + outputServerParameters
                + ", clientSslCertificateDN=" + clientSslCertificateDN 
                + ", clientSslCertificateHash=" + clientSslCertificateHash + "]";
    }

    /*
     * ********************* PROTECTED/PRIVATE METHODS ******************
     */
    protected Step() {
    }

    /**
     * @return the clientSslCertificateDN
     */
    public String getClientSslCertificateDN() {
        return clientSslCertificateDN;
    }

    /**
     * @param clientSslCertificateDN the clientSslCertificateDN to set
     */
    public void setClientSslCertificateDN(String clientSslCertificateDN) {
        this.clientSslCertificateDN = clientSslCertificateDN;
    }

    /**
     * @return the clientSslCertificateHash
     */
    public String getClientSslCertificateHash() {
        return clientSslCertificateHash;
    }

    /**
     * @param clientSslCertificateHash the clientSslCertificateHash to set
     */
    public void setClientSslCertificateHash(String clientSslCertificateHash) {
        this.clientSslCertificateHash = clientSslCertificateHash;
    }
}