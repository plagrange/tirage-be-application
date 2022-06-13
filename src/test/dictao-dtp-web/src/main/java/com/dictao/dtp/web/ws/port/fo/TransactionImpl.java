package com.dictao.dtp.web.ws.port.fo;

import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.MDC;
import org.w3c.dom.Element;

import com.dictao.dtp.core.data.RequestInfo;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.IService;
import com.dictao.dtp.core.services.exceptions.AuthenticationException;
import com.dictao.dtp.core.services.exceptions.SendingOTPException;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.StepNameEnum;
import com.dictao.dtp.persistence.entity.Transaction;
import com.dictao.dtp.web.ws.ExceptionConverter;
import com.dictao.dtp.web.ws.Task;
import com.dictao.dtp.web.ws.TransactionalTask;
import com.dictao.dtp.web.ws.WebServiceImpl;
import com.dictao.dtp.web.ws.conversion.AuthenticationInfoConverter;
import com.dictao.dtp.web.ws.conversion.PersonalInfoConverter;
import com.dictao.dtp.web.ws.conversion.UIInfoConverter;
import com.dictao.wsdl.dtp.frontoffice.v3.SystemFaultException;
import com.dictao.wsdl.dtp.frontoffice.v3.TransactionPort;
import com.dictao.wsdl.dtp.frontoffice.v3.UserFaultException;
import com.dictao.xsd.dtp.common.v2012_03.ArchiveMetadata;
import com.dictao.xsd.dtp.common.v2012_03.AuthenticationInfo;
import com.dictao.xsd.dtp.common.v2012_03.Metadata;
import com.dictao.xsd.dtp.common.v2012_03.PersonalInfo;
import com.dictao.xsd.dtp.common.v2012_03.UIInfo;
import com.dictao.xsd.dtp.common.v2012_03.UserDN;
import com.dictao.xsd.dtp.error.v3.SystemErrorInfo;
import com.dictao.xsd.dtp.error.v3.UserAuthenticationErrorCodeType;
import com.dictao.xsd.dtp.error.v3.UserErrorInfo;
import com.sun.xml.ws.developer.SchemaValidation;

/**
 *
 * @author msauvee
 */
@SchemaValidation
@WebService(serviceName = "FrontOfficeService", portName = "transactionPort", endpointInterface = "com.dictao.wsdl.dtp.frontoffice.v3.TransactionPort", targetNamespace = "http://dictao.com/wsdl/dtp/frontoffice/v3")
public class TransactionImpl extends WebServiceImpl implements TransactionPort {

    private static final ExceptionConverter<UserFaultException, SystemFaultException> ec = new ExceptionConverter<UserFaultException, SystemFaultException>() {

        @Override
        protected UserFaultException newUserException(String msg,
                UserErrorInfo faultInfo) {
            return new UserFaultException(msg, faultInfo);
        }

        @Override
        protected SystemFaultException newEnvironmentException(String msg,
                SystemErrorInfo faultInfo) {
            return new SystemFaultException(msg, faultInfo);
        }
    };

    @Override
    public void archiveTransaction(
            final String transactionId, final String service,
            final List<String> newDocumentTypesToArchive,
            final ArchiveMetadata metadata) throws SystemFaultException, UserFaultException {
        Task<Void, UserFaultException, SystemFaultException> t = new TransactionalTask<Void, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Void run() {
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                TransactionFactory txSvc = getTransactionFactory();
                TransactionHandler handler = txSvc.find(transactionId, getSSLCertificate());
                IService ecm = txSvc.getService(handler.getDatabaseTransaction(), service);
                if (ecm == null || !(ecm instanceof IECMService)) {
                    throw new UserException(
                            UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "ECM Service '%s' not found", ecm);
                }

                handler.setRequestInfo(collectRequestInfo());
                
                List<String> documentTypesToArchive;
                Transaction tx = handler.getDatabaseTransaction();

                if (tx.getArchiveDocumentTypeList() == null) {
                    if (newDocumentTypesToArchive == null || newDocumentTypesToArchive.isEmpty()) {
                        throw new UserException(
                                UserException.Code.DTP_USER_INVALID_PARAMETER,
                                "archive document type list not defined.");
                    }
                    documentTypesToArchive = newDocumentTypesToArchive;

                } else {

                    if (newDocumentTypesToArchive != null && !newDocumentTypesToArchive.isEmpty()) {
                        throw new UserException(
                                UserException.Code.DTP_USER_INVALID_PARAMETER,
                                "archive document type list already defined.");
                    }
                    documentTypesToArchive = tx.getArchiveDocumentTypeList();
                }
                //archive
                handler.archive(metadata, getNullOrNonEmptyList(documentTypesToArchive));
                return null;
            }

            @Override
            public String toString() {
                String docs = null;

                if (newDocumentTypesToArchive != null) {
                    for (String type : newDocumentTypesToArchive) {
                        docs = ((docs == null) ? "" : docs + ",") + type;
                    }
                }

                return String.format("archiveTransaction [transactionId='%s', ecm='%s', archiveDocList='%s']",
                        transactionId, service, docs);
            }
        };
        t.execute();
    }

    @Override
    public String createTransaction(
            final String type, final String tenant,
            final String applicationName,
            final String businessType, final String businessId, final String company,
            final String service,
            final List<String> documentTypesToSeal,
            final List<String> documentTypesToArchive,
            final Metadata metadata,
            final String businessTags) throws UserFaultException, SystemFaultException {
        Task<String, UserFaultException, SystemFaultException> t = new TransactionalTask<String, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public String run() {
                if ((tenant == null) || (tenant.equals(""))) {
                    throw new UserException(
                            UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "No Tenant defined");
                }

                if (type == null) {
                    throw new UserException(
                            UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "No Type associated to application");
                }

                TransactionFactory txSvc = getTransactionFactory();

                TransactionHandler tx = txSvc.create(type, tenant,
                        applicationName, businessType, businessId, company,
                        getSSLCertificate(), service,
                        metadata == null ? null : metadata.getAny(),
                        getNullOrNonEmptyList(documentTypesToSeal),
                        getNullOrNonEmptyList(documentTypesToArchive), businessTags);

                RequestInfo requestInfo = collectRequestInfo();
                tx.setRequestInfo(requestInfo);
                if (tx.getTransactionId() != null) {
                    MDC.put("TID", tx.getTransactionId());
                }
                tx.addStep(StepNameEnum.TX_CREATED);
                return tx.getTransactionId();
            }

            @Override
            public String toString() {
                String docToSeal = null;
                if (documentTypesToSeal != null) {
                    for (String type : documentTypesToSeal) {
                        docToSeal = ((docToSeal == null) ? "" : docToSeal + ",") + type;
                    }
                }
                String docsToArchive = null;
                if (documentTypesToArchive != null) {
                    for (String type : documentTypesToArchive) {
                        docsToArchive = ((docsToArchive == null) ? "" : docsToArchive + ",") + type;
                    }
                }
                return String.format("createTransaction [type='%s', tenantName='%s', applicationName='%s', businessType='%s', "
                        + "businessId='%s', company='%s', service='%s', documentTypesToSeal='%s', "
                        + "documentTypesToArchive='%s', metadata='%s']",
                        type, tenant, applicationName, businessType,
                        businessId, company, service,
                        docToSeal, docsToArchive,
                        metadata == null ? "" : metadataToString(metadata));
            }
        };
        return t.execute();
    }

    @Override
    public void sealTransaction(final String transactionId, final String service,
            final List<String> newDocumentTypesToSeal)
            throws SystemFaultException, UserFaultException {
        Task<Void, UserFaultException, SystemFaultException> t = new TransactionalTask<Void, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Void run() {
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                TransactionFactory txSvc = getTransactionFactory();
                TransactionHandler handler = txSvc.find(transactionId, getSSLCertificate());
                RequestInfo requestInfo = collectRequestInfo();
                handler.setRequestInfo(requestInfo);

                IECMService ecmsrv = (IECMService) txSvc.getService(handler.getTransactionId(), service, getSSLCertificate());

                Transaction tx = handler.getDatabaseTransaction();

                List<String> documentTypesToSeal = getNullOrNonEmptyList(newDocumentTypesToSeal);

                if (tx.getSealDocumentTypeList() != null) {
                    if (documentTypesToSeal != null) {
                        throw new UserException(
                                UserException.Code.DTP_USER_INVALID_PARAMETER,
                                "sealing document types list already defined in transaction. [id='%s']",transactionId);
                    }
                    documentTypesToSeal = tx.getSealDocumentTypeList();
                }
                handler.seal(ecmsrv, getResourceBundleHandlerReference(), documentTypesToSeal);
                return null;
            }

            @Override
            public String toString() {
                String docToSeal = null;
                if (newDocumentTypesToSeal != null) {
                    for (String type : newDocumentTypesToSeal) {
                        docToSeal = ((docToSeal == null) ? "" : docToSeal + ",") + type;
                    }
                }
                return String.format("sealTransaction [transactionId='%s', ecm='%s', docs='%s']",
                        transactionId, service, docToSeal);
            }
        };
        t.execute();
    }

    @Override
    public void finishTransaction(final String transactionId)
            throws UserFaultException, SystemFaultException {
        Task<Void, UserFaultException, SystemFaultException> t = new TransactionalTask<Void, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Void run() {
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                TransactionFactory txSvc = getTransactionFactory();
                TransactionHandler handler = txSvc.find(transactionId, getSSLCertificate());
                RequestInfo requestInfo = collectRequestInfo();
                handler.setRequestInfo(requestInfo);
                handler.finish();
                return null;
            }

            @Override
            public String toString() {
                return String.format("finishTransaction [transactionId='%s']",
                        transactionId);
            }
        };
        t.execute();
    }

    @Override
    public String addUserAccess(
            final String transactionId,
            final PersonalInfo userInfo, final UIInfo uiInfo,
            final AuthenticationInfo authenticationInfo,
            final String externalAccessId,
            final Boolean singleUsage, final Long timeout,
            final Metadata metadata) throws UserFaultException,
            SystemFaultException {
        Task<String, UserFaultException, SystemFaultException> t = new TransactionalTask<String, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public String run() {
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                TransactionFactory txSvc = getTransactionFactory();
                TransactionHandler handler = txSvc.find(transactionId, getSSLCertificate());
                RequestInfo requestInfo = collectRequestInfo();
                handler.setRequestInfo(requestInfo);
                final boolean nonNullSingleUsage =
                        (singleUsage == null) ? true : singleUsage.booleanValue();

                if (uiInfo == null) {
                    throw new UserException(
                            UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "No GUIInfo provided");
                }

                if (userInfo == null) {
                    throw new UserException(
                            UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "No PersonalInfo provided");
                }

                final String accessId;
                Element md = (metadata == null) ? null : metadata.getAny();
                accessId = handler.addUserAccess(
                        externalAccessId, nonNullSingleUsage,
                        UIInfoConverter.WSUIInfoToUIInfo(uiInfo),
                        PersonalInfoConverter.WSPersonalInfoToPersonalInfo(userInfo),
                        AuthenticationInfoConverter.WSAuthenticationInfoToAuthenticationInfo(authenticationInfo),
                        timeout, md);
                MDC.put("AID", accessId);
                return accessId;
            }

            @Override
            public String toString() {
                return String.format("addUserAccess [transactionId='%s', uiInfo='%s', personalInfo='%s', authenticaitonInfo='%s', externalAccessId='%s', singleUsage='%s', timeout='%s', metadata='%s' ]",
                        transactionId, UIInfoToString(uiInfo),
                        PersonalInfoToString(userInfo),
                        AuthenticationInfoToString(authenticationInfo),
                        externalAccessId, singleUsage, timeout, metadataToString(metadata));
            }
        };
        return t.execute();
    }

    private static String PersonalInfoToString(PersonalInfo personalInfo) {
        return String.format("personalInfo [ mainContractor='%s', user='%s', title='%s', firstname='%s', lastname='%s', userDN='%s', birthdate='%s' ]",
                personalInfo.isMainContractor(),
                personalInfo.getUser(),
                personalInfo.getTitle(),
                personalInfo.getFirstName(),
                personalInfo.getLastName(),
                UserDNToString(personalInfo.getUserDN()),
                personalInfo.getBirthdate());
    }

    private static String UserDNToString(UserDN userDN) {
        if (userDN == null) {
            return "null";
        }

        return String.format("userDn [ countryName='%s', organizationName='%s', organizationalUnitName='%s', emailAddress='%s', commonName='%s', subjectAltName='%s' ]",
                userDN.getCountryName(),
                userDN.getOrganizationName(),
                userDN.getOrganizationalUnitName(),
                userDN.getEmailAddress(),
                userDN.getCommonName(),
                userDN.getSubjectAltName());
    }

    private static String UIInfoToString(UIInfo uiInfo) {
        return String.format("uiInfo [ ui='%s', type='%s', backUrl='%s', consent='%s', label='%s', termAndConditionsUrl='%s' ]",
                uiInfo.getUi(),
                uiInfo.getType(),
                uiInfo.getBackUrl(),
                uiInfo.getConsent(),
                uiInfo.getLabel(),
                uiInfo.getTermAndConditionsUrl());
    }

    private static String AuthenticationInfoToString(AuthenticationInfo authenticationInfo) {

        if (authenticationInfo == null) {
            return "null";
        }

        return String.format("authenticationInfo [ userId='%s', phoneNumber='%s', securityLevel='%s' ]",
                authenticationInfo.getUserId(),
                authenticationInfo.getPhoneNumber(),
                authenticationInfo.getSecurityLevel());
    }

    @Override
    public void execUserAccess(final String userAccessId,
            final Metadata metadata) throws UserFaultException,
            SystemFaultException {
        Task<Void, UserFaultException, SystemFaultException> t = new TransactionalTask<Void, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Void run() {
                if (userAccessId != null) {
                    MDC.put("AID", userAccessId);
                }
                TransactionFactory txSvc = getTransactionFactory();
                TransactionHandler handler = txSvc.find(userAccessId);
                RequestInfo requestInfo = collectRequestInfo();
                handler.setRequestInfo(requestInfo);
                if (handler != null && handler.getTransactionId() != null) {
                    MDC.put("TID", handler.getTransactionId());
                }

                // **************************************************
                // FIXME improvement: https://jira.dictao.com/browse/DTPJAVA-908
                // add method get() into TransactionFactory 
                // ***************************************************
                if (!handler.isValidUserAccess()) {
                    throw new UserException(
                            UserException.Code.DTP_USER_UNAUTHORIZED,
                            "Access unauthorized, useraccess through id '%s' is already closed.", userAccessId);
                }
                // ***************************************************   

                if (metadata != null) {
                    handler.execUserAccess(getResourceBundleHandlerReference(), getLocale(), metadata.getAny());
                } else {
                    handler.execUserAccess(getResourceBundleHandlerReference(), getLocale(), null);
                }
                return null;
            }

            @Override
            public String toString() {
                return String.format("execUserAccess [userAccessId='%s', metadata='%s']",
                        userAccessId, metadataToString(metadata));
            }
        };
        t.execute();
    }

    @Override
    public void execUserAccessByApp(final String applicationName,
            final String tenant,
            final String externalAccessId, final Metadata metadata)
            throws UserFaultException, SystemFaultException {
        Task<Void, UserFaultException, SystemFaultException> t = new TransactionalTask<Void, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Void run() {
                if (externalAccessId != null) {
                    MDC.put("AID", externalAccessId);
                }
                TransactionFactory txSvc = getTransactionFactory();
                
                TransactionHandler handler = txSvc.find(applicationName, tenant,
                        externalAccessId);
                // **************************************************
                // FIXME improvement: https://jira.dictao.com/browse/DTPJAVA-908
                // add method get() into TransactionFactory 
                // ***************************************************
                if (!handler.isValidUserAccess()) {
                    throw new UserException(
                            UserException.Code.DTP_USER_UNAUTHORIZED,
                            "Access unauthorized, useraccess through applicationName '%s', "
                            + "tenant '%s' and externalAccessId '%s' is already closed.",
                            applicationName, tenant, externalAccessId);
                }
                // ***************************************************                
                RequestInfo requestInfo = collectRequestInfo();
                handler.setRequestInfo(requestInfo);
                if (handler != null && handler.getTransactionId() != null) {
                    MDC.put("TID", handler.getTransactionId());
                }

                // **************************************************
                // FIXME improvement: https://jira.dictao.com/browse/DTPJAVA-908
                // add method get() into TransactionFactory 
                // ***************************************************
                if (!handler.isValidUserAccess()) {
                    throw new UserException(
                            UserException.Code.DTP_USER_UNAUTHORIZED,
                            "Access unauthorized, useraccess through applicationName '%s', "
                            + "tenant '%s' & externalAccessId '%s' is already closed.",
                            applicationName, tenant, externalAccessId);
                }
                // ***************************************************   

                if (metadata != null) {
                    handler.execUserAccess(getResourceBundleHandlerReference(), getLocale(), metadata.getAny());
                } else {
                    handler.execUserAccess(getResourceBundleHandlerReference(), getLocale(), null);
                }
                return null;
            }

            @Override
            public String toString() {
                return String.format("execUserAccessByAppId [applicationName='%s', "
                        + "tenant '%s' externalAccessId='%s', context='%s']",
                        applicationName, tenant, externalAccessId,
                        metadataToString(metadata));
            }
        };
        t.execute();
    }

    @Override
    public void cancelTransaction(final String transactionId)
            throws SystemFaultException, UserFaultException {

        Task<Void, UserFaultException, SystemFaultException> t =
                new TransactionalTask<Void, UserFaultException, SystemFaultException>(ec, getSSLCertificate()) {

                    @Override
                    public Void run() {
                        if (transactionId != null) {
                            MDC.put("TID", transactionId);
                        }
                        TransactionFactory txSvc = getTransactionFactory();
                        TransactionHandler handler = txSvc.find(transactionId,
                                getSSLCertificate());
                        RequestInfo requestInfo = collectRequestInfo();
                        handler.setRequestInfo(requestInfo);
                        handler.cancel();
                        return null;
                    }

                    @Override
                    public String toString() {
                        return String.format("cancelTransaction [transactionId='%s']",
                                transactionId);
                    }
                };
        t.execute();
    }

    @Override
    public void sendOtp(final String userAccessId) throws UserFaultException,
            SystemFaultException {
        Task<Void, UserFaultException, SystemFaultException> t = new TransactionalTask<Void, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Void run() {
                if (userAccessId != null) {
                    MDC.put("AID", userAccessId);
                }
                TransactionFactory txSvc = getTransactionFactory();
                TransactionHandler handler = txSvc.find(userAccessId);

                RequestInfo requestInfo = collectRequestInfo();
                handler.setRequestInfo(requestInfo);
                if (handler != null && handler.getTransactionId() != null) {
                    MDC.put("TID", handler.getTransactionId());
                }

                // **************************************************
                // FIXME improvement: https://jira.dictao.com/browse/DTPJAVA-908
                // add method get() into TransactionFactory 
                // ***************************************************
                if (!handler.isValidUserAccess()) {
                    throw new UserException(
                            UserException.Code.DTP_USER_UNAUTHORIZED,
                            "Access unauthorized, useraccess through id '%s' is already closed.", userAccessId);
                }
                // ***************************************************
                try {
                    handler.sendOtp();
                } catch (SendingOTPException e) {
                    switch (e.getCode()) {
                        case USER_NOT_FOUND:
                            throw new UserException(
                                    UserAuthenticationErrorCodeType.USER_NOT_FOUND.name(),
                                    UserException.Code.DTP_USER_AUTHENTICATION_FAILED);
                        case USER_BLOCKED:
                            throw new UserException(
                                    UserAuthenticationErrorCodeType.USER_BLOCKED.name(),
                                    UserException.Code.DTP_USER_AUTHENTICATION_FAILED);
                        default:
                            throw new UserException(
                                    UserException.Code.DTP_USER_AUTHENTICATION_FAILED,
                                    "UNKNOWN_DTP_USER_AUTHENTICATION_FAILED");
                    }
                }
                return null;
            }

            @Override
            public String toString() {
                return String.format("sendOtp [userAccessId='%s']",
                        userAccessId);
            }
        };
        t.execute();
    }

    @Override
    public void verifyOtp(final String userAccessId, final String otp) throws UserFaultException,
            SystemFaultException {
        Task<Void, UserFaultException, SystemFaultException> t = new TransactionalTask<Void, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Void run() {
                if (userAccessId != null) {
                    MDC.put("AID", userAccessId);
                }
                TransactionFactory txSvc = getTransactionFactory();
                TransactionHandler handler = txSvc.find(userAccessId);

                RequestInfo requestInfo = collectRequestInfo();
                handler.setRequestInfo(requestInfo);
                if (handler != null && handler.getTransactionId() != null) {
                    MDC.put("TID", handler.getTransactionId());
                }

                // **************************************************
                // FIXME improvement: https://jira.dictao.com/browse/DTPJAVA-908
                // add method get() into TransactionFactory 
                // ***************************************************
                if (!handler.isValidUserAccess()) {
                    throw new UserException(
                            UserException.Code.DTP_USER_UNAUTHORIZED,
                            "Access unauthorized, useraccess through id '%s' is already closed.", userAccessId);
                }
                // ***************************************************

                try {
                    handler.verifyOtp(otp);
                } catch (AuthenticationException e) {
                    switch (e.getCode()) {
                        case USER_NOT_FOUND:
                            throw new UserException(
                                    UserAuthenticationErrorCodeType.USER_NOT_FOUND.name(),
                                    UserException.Code.DTP_USER_AUTHENTICATION_FAILED);
                        case OTP_BLOCKED:
                            throw new UserException(
                                    UserAuthenticationErrorCodeType.OTP_BLOCKED.name(),
                                    UserException.Code.DTP_USER_AUTHENTICATION_FAILED);
                        case OTP_EXPIRED:
                            throw new UserException(
                                    UserAuthenticationErrorCodeType.OTP_EXPIRED.name(),
                                    UserException.Code.DTP_USER_AUTHENTICATION_FAILED);
                        case USER_NOT_AUTHENTICATED:
                            throw new UserException(
                                    UserAuthenticationErrorCodeType.OTP_INVALID.name(),
                                    UserException.Code.DTP_USER_AUTHENTICATION_FAILED);
                        default:
                            throw new UserException(
                                    UserException.Code.DTP_USER_AUTHENTICATION_FAILED,
                                    e.getMessage());
                    }
                }
                return null;
            }

            @Override
            public String toString() {
                return String.format("verifyOtp [userAccessId='%s',otp='%s']",
                        userAccessId, otp);
            }
        };
        t.execute();
    }

    @Override
    public void cancelUserAccess(final String accessId) throws SystemFaultException, UserFaultException {

        Task<Void, UserFaultException, SystemFaultException> t =
                new TransactionalTask<Void, UserFaultException, SystemFaultException>(ec, getSSLCertificate()) {

                    @Override
                    public Void run() {

                        if (accessId != null) {
                            MDC.put("AID", accessId);
                        }
                        TransactionFactory txSvc = getTransactionFactory();
                        // **************************************************
                        // FIXME improvement: https://jira.dictao.com/browse/DTPJAVA-908
                        // add method get() into TransactionFactory 
                        // ***************************************************
                        TransactionHandler handler = txSvc.find(accessId);

                        RequestInfo requestInfo = collectRequestInfo();
                        handler.setRequestInfo(requestInfo);
                        if (handler != null && handler.getTransactionId() != null) {
                            MDC.put("TID", handler.getTransactionId());
                        }
                        
                        handler.userAccessCancelledByWS();

                        return null;
                    }

                    @Override
                    public String toString() {
                        return String.format("cancelUserAccess [accessId='%s']",
                                accessId);
                    }
                };

        t.execute();

    }

    @Override
    public void cancelUserAccessByApp(final String applicationName, final String tenant, final String externalAccessId)
            throws SystemFaultException, UserFaultException {
        Task<Void, UserFaultException, SystemFaultException> t =
                new TransactionalTask<Void, UserFaultException, SystemFaultException>(ec, getSSLCertificate()) {

                    @Override
                    public Void run() {

                        if (externalAccessId != null) {
                            MDC.put("AID", externalAccessId);
                        }
                        TransactionFactory txSvc = getTransactionFactory();
                        // **************************************************
                        // FIXME improvement: https://jira.dictao.com/browse/DTPJAVA-908
                        // add method get() into TransactionFactory 
                        // ***************************************************
                        TransactionHandler handler = txSvc.find(applicationName, externalAccessId);

                        RequestInfo requestInfo = collectRequestInfo();
                        handler.setRequestInfo(requestInfo);
                        if (handler != null && handler.getTransactionId() != null) {
                            MDC.put("TID", handler.getTransactionId());
                        }
                        
                        handler.userAccessCancelledByWS();
                        
                        return null;
                    }

                    @Override
                    public String toString() {
                        return String.format("cancelUserAccessByApp [applicationName='%s', tenant='%s', externalAccessId='%s']",
                                applicationName, tenant, externalAccessId);
                    }
                };

        t.execute();
    }

    private static List<String> getNullOrNonEmptyList(List<String> data) {
        if (data == null || data.isEmpty()) {
            return null;
        } else {
            return data;
        }
    }
}