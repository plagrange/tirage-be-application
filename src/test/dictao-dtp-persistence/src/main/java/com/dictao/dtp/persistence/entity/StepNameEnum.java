package com.dictao.dtp.persistence.entity;

import java.util.Set;
import java.util.EnumSet;

/**
 * 
 * Normalization of step names They do not have to match exactly a Transaction
 * status neither UserAccess status.
 * 
 * DO NOT CHANGE ANY EXISTING ENUM VALUES SINCE THEY ARE STORED IN DATABASE.
 * (but you can add some).
 * 
 */
public enum StepNameEnum {

    // Step that occurs on a transaction
    TX_CREATED(EnumSet.of(StepType.TX)),

    TX_USERACCESS_ADDED(EnumSet.of(StepType.TX)),

    TX_SEALING(EnumSet.of(StepType.TX)),

    TX_ARCHIVED(EnumSet.of(StepType.TX)),

    TX_ENDED(EnumSet.of(StepType.TX)),

    TX_CANCELLED(EnumSet.of(StepType.TX)),

    // Step that occurs on a transaction during a user access
    USERACCESS_STARTED(EnumSet.of(StepType.USERACCESS)),

    USERACCESS_AUTHENTICATED(EnumSet.of(StepType.USERACCESS)),
    
    USERACCESS_AUTHENTICATION_USER_NOT_FOUND(EnumSet.of(StepType.USERACCESS)),
    
    USERACCESS_AUTHENTICATION_USER_BLOCKED(EnumSet.of(StepType.USERACCESS)),

    USERACCESS_AUTHENTICATION_FAILED(EnumSet.of(StepType.USERACCESS)),

    USERACCESS_CANCELLED_BY_USER(EnumSet.of(StepType.USERACCESS)),
    
    USERACCESS_CANCELLED_BY_WS(EnumSet.of(StepType.USERACCESS)),
    
    USERACCESS_CERTIFICATE_NOT_FOUND(EnumSet.of(StepType.USERACCESS)),

    USERACCESS_COMPLETION_LATER(EnumSet.of(StepType.USERACCESS)),

    USERACCESS_CHOOSE_INSURANCE(EnumSet.of(StepType.USERACCESS)),

    USERACCESS_CONSENT_CHECKED(EnumSet.of(StepType.USERACCESS)),

    USERACCESS_CONSENT_UNCHECKED(EnumSet.of(StepType.USERACCESS)),
    
    USERACCESS_ENDED_SUCESSFULY(EnumSet.of(StepType.USERACCESS)), 
    
    USERACCESS_DOCUMENT_VIEWED(EnumSet.of(StepType.USERACCESS)),
    
    @Deprecated
    USERACCESS_OTP_BLOCKED(EnumSet.of(StepType.USERACCESS)),
    @Deprecated
    USERACCESS_OTP_EXPIRED(EnumSet.of(StepType.USERACCESS)),
    @Deprecated
    USERACCESS_OTP_INVALID(EnumSet.of(StepType.USERACCESS)),
    @Deprecated
    USERACCESS_OTP_INVALID_USER(EnumSet.of(StepType.USERACCESS)),
    @Deprecated
    USERACCESS_OTP_TRIAL_EXHAUSTED(EnumSet.of(StepType.USERACCESS)),

    USERACCESS_SIGN_BUTTON_CLICKED(EnumSet.of(StepType.USERACCESS)),

    USERACCESS_USER_INTERACTION_EVENT(EnumSet.of(StepType.USERACCESS)),
    
    USERACCESS_ENVIRONMENT_NOT_SUPPORTED(EnumSet.of(StepType.USERACCESS)),

    // Steps that occurs on a transaction during a user access with also a
    // document as context
    // Steps that occurs on a transaction with also a document as context
    // DOCUMENT_SIGNED_BY_ENTITY(EnumSet.of(StepType.USERACCESS)),

    // Steps that occurs on services activity
    SERVICE_USER_PROVISIONED(EnumSet.of(StepType.USERACCESS, StepType.SERVICE)),

    SERVICE_USER_REMOVED(EnumSet.of(StepType.USERACCESS, StepType.SERVICE)),

    SERVICE_OTP_SENT(EnumSet.of(StepType.USERACCESS, StepType.SERVICE)),

    SERVICE_OTP_VERIFIED(EnumSet.of(StepType.USERACCESS, StepType.SERVICE)),

    SERVICE_CERTIFICATE_CREATED(EnumSet.of(StepType.USERACCESS,
            StepType.SERVICE)),

    SERVICE_KEY_GENERATED(EnumSet.of(StepType.USERACCESS, StepType.SERVICE)),

    SERVICE_DOCUMENT_SIGNATURE_VALID(EnumSet.of(StepType.USERACCESS,
            StepType.SERVICE)),
            
    SERVICE_DOCUMENT_SIGNATURE_TIMESTAMPED(EnumSet.of(StepType.USERACCESS,
                    StepType.SERVICE)),

    SERVICE_DOCUMENT_SIGNATURE_INVALID(EnumSet.of(StepType.USERACCESS,
            StepType.SERVICE)),

    SERVICE_DOCUMENT_SIGNED(EnumSet.of(StepType.USERACCESS, StepType.SERVICE)),

    SERVICE_DOCUMENT_ADDED(EnumSet.of(StepType.USERACCESS, StepType.SERVICE)),
    
    SERVICE_DOCUMENT_VIEWED(EnumSet.of(StepType.USERACCESS, StepType.SERVICE)),

    // Ticket Jira : https://jira.dictao.com/browse/DTPJAVA-1503
    // Description : Retirer l'ajout de steps d'erreur lors des redirections en erreur
    // Environment signature service error
    SERVICE_SIGNATURE_ERROR(EnumSet.of(StepType.ERROR)),
    // Environment validation error
    SERVICE_VALIDATION_ERROR(EnumSet.of(StepType.ERROR)),
    // Environment otp/authentication error
    SERVICE_OTP_ERROR(EnumSet.of(StepType.ERROR)),
    // Environment pki error
    SERVICE_PKI_ERROR(EnumSet.of(StepType.ERROR)),
    // Environment pki error
    SERVICE_AUTHENTICATION_ERROR(EnumSet.of(StepType.ERROR)),
   
   @Deprecated
    // Error due to user
    USER_ERROR(EnumSet.of(StepType.ERROR)),
    @Deprecated
    // Environment error
    ENVIRONMENT_ERROR(EnumSet.of(StepType.ERROR)),
    @Deprecated
    // System error
    INTERNAL_ERROR(EnumSet.of(StepType.ERROR));

    public enum StepType {
        TX, USERACCESS, SERVICE, ERROR;
    }

    private Set<StepType> stepTypes;

    private StepNameEnum(Set<StepType> stepTypes) {
        this.stepTypes = stepTypes;
    }

    public Set<StepType> getStepTypes() {
        return stepTypes;
    }

}
