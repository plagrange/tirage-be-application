package com.dictao.dtp.persistence.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author kchakali
 */
public class TransactionSummary  implements Serializable{

    private static final long serialVersionUID = -4173554511327447202L;
    public static final String STATUS_COLUMN = "status";
    public static final String SUB_STATUS_COLUMN = "subStatus";
    public static final String UPDATE_COLUMN = "updateTimestamp";
    public static final String ERROR_SOURCE_COLUMN = "errorSource";
    
	public static final String SERVICE_SIGNATURE_ERROR = "sign service";
	public static final String SERVICE_OTP_ERROR = "otp service";
	public static final String SERVICE_VALIDATION_ERROR = "valid service";
	public static final String SERVICE_PKI_ERROR = "pki service";
    
    private String transactionID;
    private String applicationID;
    private String applicationName = "";
    private String tenant;
    private String company;
    private String handler;

    private Date startTimestamp;
    private Date updateTimestamp;
    private Date endTimestamp;
    private Date expirationTimestamp;

    private TransactionStatusEnum status;
    private TransactionSubStatusEnum subStatus;
    private String errorSource;
    private String ecm;
    
    //Serialization 
    public TransactionSummary() {}

    public TransactionSummary(Transaction tx, String errorSource) {

        this.startTimestamp = tx.getStartTimestamp();
        this.updateTimestamp = tx.getUpdateTimestamp();
        this.endTimestamp = tx.getEndTimestamp();
        this.expirationTimestamp = tx.getExpirationTimestamp();
        this.status = tx.getStatus();
        this.subStatus = tx.getSubStatus();
        this.transactionID = tx.getTransactionID();
        this.applicationID = tx.getApplicationID();
        this.tenant = tx.getTenant();
        this.company = tx.getCompany();
        this.handler = tx.getHandler();
        this.errorSource = "-";

        Step s = tx.getFistStep(StepNameEnum.SERVICE_DOCUMENT_SIGNED);
        this.ecm = (null != s && !s.getEcmServiceName().isEmpty()) ? s.getEcmServiceName() : "N-A";
       
        if (null != errorSource) {
            this.errorSource = errorSource;
        }
        else {
            if (tx.doesExistStep(StepNameEnum.SERVICE_SIGNATURE_ERROR))
                this.errorSource = TransactionSummary.SERVICE_SIGNATURE_ERROR;
            else if (tx.doesExistStep(StepNameEnum.SERVICE_OTP_ERROR))
                this.errorSource = TransactionSummary.SERVICE_OTP_ERROR;
            else if (tx.doesExistStep(StepNameEnum.SERVICE_VALIDATION_ERROR))
                this.errorSource = TransactionSummary.SERVICE_VALIDATION_ERROR;
            else if (tx.doesExistStep(StepNameEnum.SERVICE_PKI_ERROR))
                this.errorSource = TransactionSummary.SERVICE_PKI_ERROR;
        }
    }
    
    public String getTransactionID() {
        return transactionID;
    }
    
    public String getApplicationID() {
        return applicationID;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getTenant() {
        return tenant;
    }

    public String getCompany() {
        return company;
    }

    public String getHandler() {
        return handler;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public Date getEndTimestamp() {
        return endTimestamp;
    }

    public Date getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public TransactionStatusEnum getStatus() {
        return status;
    }

    public TransactionSubStatusEnum getSubStatus() {
        return subStatus;
    }
    
    public String getErrorSource () {
        return errorSource;
    }
    
    public String getTxECM(){
        return ecm;
    }
}
