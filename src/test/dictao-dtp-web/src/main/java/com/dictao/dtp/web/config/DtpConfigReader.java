package com.dictao.dtp.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.dictao.dtp.core.api.jaxb.JAXBContextCache;
import com.dictao.dtp.core.api.pdf.PdfExplorer;
import com.dictao.dtp.core.data.FontData;
import com.dictao.dtp.core.data.SignatureLayout;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.services.IArchiveService;
import com.dictao.dtp.core.services.archive.ArchiveService;
import com.dictao.dtp.core.services.authentication.AuthenticationService;
import com.dictao.dtp.core.services.authentication.OTPService;
import com.dictao.dtp.core.services.ecm.ECMDbService;
import com.dictao.dtp.core.services.ecm.IndexHandler;
import com.dictao.dtp.core.services.internal.SSLConnection;
import com.dictao.dtp.core.services.mail.MailService;
import com.dictao.dtp.core.services.pki.PKIService;
import com.dictao.dtp.core.services.rendering.DocRenderingEcmRepository;
import com.dictao.dtp.core.services.rendering.DocRenderingService;
import com.dictao.dtp.core.services.rendering.IDocRenderingRepository;
import com.dictao.dtp.core.services.sign.AppletSignService;
import com.dictao.dtp.core.services.sign.SignService;
import com.dictao.dtp.core.services.timestamping.TimeStampingService;
import com.dictao.dtp.core.services.transform.TransformService;
import com.dictao.dtp.core.services.validation.ValidationService;
import com.dictao.dtp.core.transactions.ApplicationConf;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.core.transactions.BusinessTypeConf;
import com.dictao.dtp.core.transactions.CertificateConf;
import com.dictao.dtp.core.transactions.ExportConf;
import com.dictao.dtp.core.transactions.HandlerTypeConf;
import com.dictao.dtp.core.transactions.ServiceConf;
import com.dictao.dtp.core.transactions.TenantConf;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.UserAgentConf;
import com.dictao.dtp.core.transactions.UserAgentWildCard;
import com.dictao.dtp.persistence.ArchiveRepository;
import com.dictao.dtp.persistence.DocumentService;
import com.dictao.dtp.types.fo.config.AppletSignServiceType;
import com.dictao.dtp.types.fo.config.ArchiveServiceType;
import com.dictao.dtp.types.fo.config.ArchiveServiceType.Policy;
import com.dictao.dtp.types.fo.config.AuthenticationServiceType;
import com.dictao.dtp.types.fo.config.AuthorizedCertificates.Certificate;
import com.dictao.dtp.types.fo.config.ECMServiceType;
import com.dictao.dtp.types.fo.config.ECMServiceType.Index.Docsets.Docset;
import com.dictao.dtp.types.fo.config.ECMServiceType.Index.Entry;
import com.dictao.dtp.types.fo.config.EventServiceType;
import com.dictao.dtp.types.fo.config.EventsServerType;
import com.dictao.dtp.types.fo.config.EventsServerType.Rest.Endpoint;
import com.dictao.dtp.types.fo.config.FrontOfficeConfiguration;
import com.dictao.dtp.types.fo.config.FrontOfficeConfiguration.Servers;
import com.dictao.dtp.types.fo.config.HandlerTypes.HandlerType;
import com.dictao.dtp.types.fo.config.HandlerTypes.HandlerType.BusinessTypes.BusinessType;
import com.dictao.dtp.types.fo.config.MailServiceType;
import com.dictao.dtp.types.fo.config.OTPServiceType;
import com.dictao.dtp.types.fo.config.OpenOfficeType;
import com.dictao.dtp.types.fo.config.PKIConnectorServiceType;
import com.dictao.dtp.types.fo.config.PropertyType;
import com.dictao.dtp.types.fo.config.ServerSignServiceType;
import com.dictao.dtp.types.fo.config.SignatureLayoutType;
import com.dictao.dtp.types.fo.config.Style;
import com.dictao.dtp.types.fo.config.TenantConfiguration;
import com.dictao.dtp.types.fo.config.TenantFO;
import com.dictao.dtp.types.fo.config.TenantFO.Application;
import com.dictao.dtp.types.fo.config.TimeStampingServiceType;
import com.dictao.dtp.types.fo.config.TransformServiceType;
import com.dictao.dtp.types.fo.config.TransportType;
import com.dictao.dtp.types.fo.config.UrlMap;
import com.dictao.dtp.types.fo.config.UserAgentRegExpList.UserAgent;
import com.dictao.dtp.types.fo.config.ValidationServiceType;
import com.dictao.dtp.web.services.RestEventService;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import javax.xml.datatype.Duration;
/**
 *
 * @author msauvee
 */
public class DtpConfigReader extends ConfigReader {

    private static final String CONFIG_XSD_FILE = "resources/xsd/fo-config.xsd";
    private static final Logger LOG = LoggerFactory.getLogger(DtpConfigReader.class);
    private static final int defaultServiceRequestTimeout = 30 * 1000; //30s
    private ApplicationListConf appsList;
    private DocumentService documentService;
    private String dtpVersion;
    private ArchiveRepository archiveRepository;
    private Map<String, ApplicationConf> appsMap = new HashMap<String, ApplicationConf>();
    private Map<String, String> refAppsNameId = new HashMap<String, String>();
    private boolean checkTransactionHandlerClass = false;
    private String contextName = null;
    private ThreadPoolExecutor threadPoolExecutor;

    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";

    public DtpConfigReader(String dtpVersion, ApplicationListConf appsList,
            DocumentService documentService,
            ArchiveRepository archiveRepository, boolean checkTransactionHandlerClass, String contextName, ThreadPoolExecutor threadPoolExecutor) {
        this.appsList = appsList;
        this.documentService = documentService;
        this.dtpVersion = dtpVersion;
        this.archiveRepository = archiveRepository;
        this.checkTransactionHandlerClass = checkTransactionHandlerClass;
        this.contextName = contextName;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    /*
     * ********************************************************************
     * PUBLIC METHODS
     * ********************************************************************
     */
    @Override
    public void read(InputStream is, ConfigFileLoader loader) {
        LOG.entering("Load configuration");
        FrontOfficeConfiguration config;
        try {
            config = loadFrontOfficeConfig(is);
            Map<String, AppDesc> apps = new HashMap<String, AppDesc>();
            String externalPath;

            int secureLevel = config.getSecureLevel();

            if (config.getTenants() != null) {
                externalPath = config.getTenants().getPath();
                loadExternalTenant(externalPath, loader, config, apps, secureLevel);
            }

            for (TenantFO tenant : config.getTenant()) {
                loadTenant(tenant, loader, config, apps, null, secureLevel);
            }

            Map<String, String> urls = new HashMap<String, String>();
            if (null != config.getUrls()) {
                UrlMap urlMap = config.getUrls();
                for (UrlMap.Url url : urlMap.getUrl()) {
                    String key = url.getKey();
                    String ap = url.getAbsoluteUrl();
                    urls.put(key, ap);
                }
            }
            String dtpVersionId = config.getDtpVersionId();
            if (secureLevel > 0 && dtpVersionId == null) {
                throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                        "No dtpVersionId set in high security level mode.");
            }
            appsList.setConfigurations(dtpVersion, dtpVersionId, appsMap, urls, refAppsNameId);
        } catch (Exception ex) {
            throw new EnvironmentException(ex,
                    EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "Unable to load configuration");
        }
        LOG.info("%s configuration has loaded correctly.", contextName);
        LOG.exiting();
    }

    /*
     * ********************************************************************
     * PROTECTED/PRIVATE METHODS
     * ********************************************************************
     */
    private void loadTenant(TenantFO tenant,
            ConfigFileLoader loader,
            FrontOfficeConfiguration config,
            Map<String, AppDesc> apps,
            String tenantStaticResourcesPath, int secureLevel)
            throws FileNotFoundException, IOException {

        TenantConf tenantTmp = new TenantConf(tenant.getName(),
                loader.computeFullURL(tenant.getRedirectURL()),
                loader.getFilePath(tenant.getExportPath()),
                tenant.getLocale(), tenant.getAuditVersion());

        for (Application app : tenant.getApplication()) {
            if (apps.containsKey(app.getId())) {
                throw new EnvironmentException(
                        EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                        "Application '%s' is declared twice. You can't have two applications of the same name even for two different tenants.",
                        app.getName());
            }
            AppDesc appDesc = new AppDesc();
            String appStaticResourceFoler = null;

            if (tenantStaticResourcesPath != null) {
                appStaticResourceFoler = tenantStaticResourcesPath + File.separator + app.getName();
            } else {
                appStaticResourceFoler = app.getExternalResource();
            }


            if (app.getOverride() != null) {
                loadApplication(app.getId(), appDesc,
                        (Application) app.getOverride(), loader, config, tenantStaticResourcesPath, tenant.getName());
                if (tenantStaticResourcesPath != null) {
                    appStaticResourceFoler = tenantStaticResourcesPath + File.separator + ((Application) app.getOverride()).getName();
                } else {
                    appStaticResourceFoler = app.getExternalResource();
                }
            }
            //FIXME KCH: implement  mergeApplication(appDesc, loadApplication(app.getId(), appDesc, app, loader, config))
            loadApplication(app.getId(), appDesc, app, loader, config, tenantStaticResourcesPath, tenant.getName());

            final ApplicationConf appConf = new ApplicationConf(
                    app.getId(),
                    app.getName(),
                    tenantTmp,
                    appDesc.certificates,
                    appDesc.userAgentsWhiteList,
                    appDesc.userAgentsBlackList,
                    appDesc.appServices,
                    appDesc.companies,
                    appDesc.handlerTypes,
                    appDesc.urlMap,
                    loader.computeFullURL(app.getErrorURL()),
                    appStaticResourceFoler,
                    tenant.isActive(),
                    secureLevel,
                    appDesc.userAgentsWildCard);
            appConf.setExports(appDesc.openExport, appDesc.pendingExport, appDesc.modifiedExport, appDesc.closedExport);
            appsMap.put(app.getId(), appConf);

            refAppsNameId.put(tenant.getName() + ":" + app.getName(), app.getId());
        }
    }

    private TransportType getTransportType(Map<String, Object> map, String id) {
        TransportType transportType = (TransportType) map.get(id);
        if (transportType == null) {
            throw new EnvironmentException(
                    EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "Unable to load configuration. No reference to server [id='%s']", id);
        }
        return transportType;
    }

    private OpenOfficeType getOpenOfficeType(Map<String, Object> map, String id) {
        OpenOfficeType openOfficeType = (OpenOfficeType) map.get(id);
        if (openOfficeType == null) {
            throw new EnvironmentException(
                    EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "Unable to load configuration. No reference to server [id='%s']", id);
        }
        return openOfficeType;
    }

    /**
     * Load an application from configuration and add it to an AppDesc instance.
     * This method is used to override the appDesc parameter if not null.
     * @param appDesc
     * @param app
     * @param tenant TODO
     *
     * @throws FileNotFoundException
     */
    protected void loadApplication(String idApp, AppDesc appDesc,
            Application app,
            ConfigFileLoader loader,
            FrontOfficeConfiguration config,
	    String tenantStaticResourcesPath, String tenant)
            throws FileNotFoundException, IOException {

        Servers servers = config.getServers();
        List<Certificate> listCerts = app.getAuthorizedCertificates().getCertificate();
        appDesc.certificates.addAll(convertCertificates(listCerts));
        
        if (app.getAuthorizedUserAgents() != null) {
            if (app.getAuthorizedUserAgents().getWhiteList() != null) {
                appDesc.userAgentsWhiteList = new ArrayList<UserAgentConf>();
                for (UserAgent ua : app.getAuthorizedUserAgents().getWhiteList().getUserAgent()){
                    appDesc.userAgentsWhiteList.add(new UserAgentConf(ua.getOs(), ua.getBrowser()));
                }
            }
            if (app.getAuthorizedUserAgents().getBlackList() != null) {
                appDesc.userAgentsBlackList = new ArrayList<UserAgentConf>();
                for (UserAgent ua : app.getAuthorizedUserAgents().getBlackList().getUserAgent()){
                    appDesc.userAgentsBlackList.add(new UserAgentConf(ua.getOs(), ua.getBrowser()));
                }
            }
        }
        
        if(app.getUserAgentsStyles() != null){
        	appDesc.userAgentsWildCard = new ArrayList<UserAgentWildCard>();
            for (Style ua : app.getUserAgentsStyles().getStyle()) {
            	List<String> x= new ArrayList<String>();
            	
            	for (com.dictao.dtp.types.fo.config.Style.UserAgent z : ua.getUserAgent()){
            		x.add(z.getPattern());
            	}
            	appDesc.userAgentsWildCard.add(new UserAgentWildCard(ua.getCssName(), x));
            }
        }

        // Load handlers
        if (null != app.getHandlerTypes()) {
            
            // default expire duration 
            Duration defaultExpireAfter = app.getHandlerTypes().getDefaultExpireAfter();
            for (HandlerType handler : app.getHandlerTypes().getHandlerType()) {
                if (this.checkTransactionHandlerClass
                        && !TransactionFactory.isValidTransactionHandler(handler.getClazz())) {
                    throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                            "Cannot load configuration: Unable to instanciate transaction handler '%s' in '%s'",
                            handler.getName(), handler.getClazz());
                }
                List<BusinessTypeConf> businessTypes = new ArrayList<BusinessTypeConf>();
                if (handler.getBusinessTypes() != null) {
                    for (BusinessType bt : handler.getBusinessTypes().getBusinessType()) {
                        // compute expiration time
                        Long time = null;
                        if(bt.getExpireAfter() != null) {
                            time = bt.getExpireAfter().getTimeInMillis(new Date());
                        } else if(defaultExpireAfter != null) {
                            time = defaultExpireAfter.getTimeInMillis(new Date());
                        }
                        // add business type
                        businessTypes.add(new BusinessTypeConf(bt.getName(), time));
                    }
                }
                // base TransactionHandler is used if no check TransactionHandler class is required.
                Class<?> clazz = com.dictao.dtp.core.transactions.TransactionHandler.class;
                if (this.checkTransactionHandlerClass) {
                    clazz = getHandlerClazzFromName(handler.getClazz());
                }

                // compute expiration time
                Long durationMs = null;
                if (handler.getExpireAfter() != null) {
                    durationMs = new Long(handler.getExpireAfter().getTimeInMillis(new Date()));
                } else if(defaultExpireAfter != null) {
                    durationMs = new Long(defaultExpireAfter.getTimeInMillis(new Date()));
                }
                
                //boolean sealAuto = handler.isSealAuto();
                appDesc.handlerTypes.put(handler.getName(), new HandlerTypeConf(clazz, businessTypes, durationMs, handler.isSealAuto()));
                LOG.debug("add businessType [name=%s, class='%s', expireAfter='%s']", handler.getName(),
                        handler.getClazz(), durationMs == null ? "not specified" : durationMs.toString());
            }
        }

        // Load ECM services
        for (ECMServiceType svc : app.getServices().getEcmDbService()) {
            DocRenderingService renderingService = null;

            if (svc.getDocRenderingService() != null && svc.getDocRenderingService().isActive()) {
                IDocRenderingRepository renderingRepo = new DocRenderingEcmRepository();

                boolean disableClickToSign;
                if (null == svc.getDocRenderingService().isDisableClickToSign()) {
                    disableClickToSign = false;
                } else {
                    disableClickToSign = svc.getDocRenderingService().isDisableClickToSign();
                }
                String timeZone = svc.getDocRenderingService().getTimeZone();
                
                String temporaryPath = tenantStaticResourcesPath + File.separator + "tmp";  
                File file = new File(temporaryPath);
                if(!file.exists()){
                    file.mkdirs();
                }
                //init temporary directory for PDFLibrary wrapper.
                PdfExplorer.temporaryPath = temporaryPath;
                renderingService = new DocRenderingService(renderingRepo, disableClickToSign, timeZone, threadPoolExecutor);
            }

            ECMDbService ecmdb = new ECMDbService(svc.getName(),
                    idApp, svc.getDescription(), documentService, renderingService, tenant);
            if (svc.getMaxUploadSize() != null) {
                ecmdb.setMaxUploadSize(svc.getMaxUploadSize().intValue());
            }
            // configure index handler
            if (svc.getIndex() != null) {
                IndexHandler ih = new IndexHandler();
                if (svc.getIndex().getLoadBundle() != null) {
                    ih.setResourceBundleName(svc.getIndex().getLoadBundle().getBasename());
                } // Entries
                if (svc.getIndex().getEntry() != null
                        && svc.getIndex().getEntry().size() > 0) {
                    for (Entry entry : svc.getIndex().getEntry()) {
                        boolean isUnique = (entry.isUnique() == null) ? true
                                : entry.isUnique();
                        ih.addEntryInfo(entry.getType(), isUnique,
                                entry.getTransform(), entry.isDvsVisualizableProof(),
                                entry.getSizeMax(), entry.getMimeType(), entry.getFileName(), entry.isUserAccesAllowed());
                    }
                } // DocSets
                if (svc.getIndex().getDocsets() != null
                        && svc.getIndex().getDocsets().getDocset() != null
                        && svc.getIndex().getDocsets().getDocset().size() > 0) {
                    for (Docset docset : svc.getIndex().getDocsets().getDocset()) {
                        String[] tab = docset.getList().split(",");
                        ih.addDocSets(docset.getName(), tab);
                    }
                }
                ecmdb.setIndexHandler(ih);
            }
            appDesc.putService(new ServiceConf(svc.getName(), svc.getCompany(), ecmdb, svc.getAuthorizedHandlers()));
        }

        Map<String, Object> mapTransportType = new HashMap<String, Object>();
        for (JAXBElement<?> ja : servers.getD2SServerOrDvsServerOrDacsServer()) {
            if (ja.getValue() instanceof TransportType) {
                TransportType transportType = (TransportType) ja.getValue();
                String id = transportType.getId();
                if (mapTransportType.get(id) == null) {
                    mapTransportType.put(id, transportType);
                }
            } else if (ja.getValue() instanceof OpenOfficeType) {
                OpenOfficeType openOfficeType = (OpenOfficeType) ja.getValue();
                String id = openOfficeType.getId();
                if (mapTransportType.get(id) == null) {
                    mapTransportType.put(id, openOfficeType);
                }
            } else if (ja.getValue() instanceof EventsServerType) {

                EventsServerType serverEventsSubscriber = (EventsServerType) ja.getValue();
                String id = serverEventsSubscriber.getId();
                if (mapTransportType.get(id) == null) {
                    mapTransportType.put(id, serverEventsSubscriber);
                }
            }
        }

        // Load signatures layout
        if (null != app.getSignatureLayouts()) {
            Map<String, SignatureLayout> layouts = new HashMap<String, SignatureLayout>();
            for (SignatureLayoutType lst : app.getSignatureLayouts().getSignatureLayout()) {
                SignatureLayout sl = new SignatureLayout(
                        lst.getFormat().getWidth(),
                        lst.getFormat().getHeight(),
                        lst.getDisposition(),
                        lst.getTextKeys().getText(),
                        new FontData(lst.getFont().getName(),
                        lst.getFont().getStyle(),
                        lst.getFont().getSize()));
		//Signature
                if (lst.getSignature() != null) {
		    //Image path
                    if (lst.getSignature().getImagePath() != null) {
                        sl.setImage(loader.getFileStream(lst.getSignature().getImagePath()));
		    //HandWritting
                    } else if (lst.getSignature().getHandWritting() != null) {
                        FontData fd = new FontData(lst.getSignature().getHandWritting().getFont().getName(),
                                lst.getSignature().getHandWritting().getFont().getStyle(),
                                lst.getSignature().getHandWritting().getFont().getSize());
                        sl.setHandWritting(fd, lst.getSignature().getHandWritting().getText());
	            //Type from ecm
		    } else if (lst.getSignature().getTypeFromEcm() != null) {
			sl.setTypeFromECM(lst.getSignature().getTypeFromEcm());
                    }
                }
                if (lst.getBackgroundColor() != null) {
                    sl.setColorBackground(lst.getBackgroundColor().getR(),
                            lst.getBackgroundColor().getG(),
                            lst.getBackgroundColor().getB(),
                            lst.getBackgroundColor().getA());
                }
                boolean argb = lst.isArgbMode() != null && lst.isArgbMode().booleanValue();
                sl.setArgbMode(argb);
                layouts.put(lst.getName(), sl);
            }  
            appDesc.layouts.putAll(layouts);
        }

        // Load signature services
        for (ServerSignServiceType srv : app.getServices().getSignService()) {
            TransportType transportType = getTransportType(mapTransportType, (String) srv.getD2S());
            SSLConnection d2sSSLConnection = createSSLConnection(
                    transportType, srv.getServiceRequestTimeout(), loader);
            SignService signSrv = new SignService(srv.getName(),
                    srv.getDescription(),
                    transportType.getId(), d2sSSLConnection,
                    srv.getTransactionID(), srv.getSignatureLabelField(),
                    srv.getD2SParameters(), config.getSecureLevel());
            if (null != srv.getSignatureImagePath()
                    && srv.getSignatureImagePath().length() != 0) {
                signSrv.setImage(loader.getFileStream(srv.getSignatureImagePath()));
            }

            if (null != srv.getSignatureHashAlgo()
                    && srv.getSignatureHashAlgo().length() != 0) {
                signSrv.setManifestSignatureHashAlgo(srv.getSignatureHashAlgo());
            }
            if (null != srv.getKeyLength()) {
                signSrv.setKeyLength(srv.getKeyLength());
            }

            appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), signSrv, srv.getAuthorizedHandlers()));
        }

        // Load applet signature services
        if (app.getServices().getAppletSignService() != null) {
            for (AppletSignServiceType srv : app.getServices().getAppletSignService()) {
                AppletSignService appletSignSrv = new AppletSignService(srv.getName(),
                        srv.getDescription(),
                        loader.getFileStream(srv.getAdSignerDeploymentPath()), srv.getSignatureHashAlgorithm());

                appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), appletSignSrv, srv.getAuthorizedHandlers()));
            }
        }

        // Load validation services
        for (ValidationServiceType srv : app.getServices().getValidationService()) {
            TransportType transportType = getTransportType(mapTransportType, (String) srv.getDvs());
            SSLConnection dvsConnection = createSSLConnection(transportType, srv.getServiceRequestTimeout(), loader);

            ValidationService validationSrv = new ValidationService(
                    srv.getName(), srv.getDescription(),
                    transportType.getId(), dvsConnection,
                    srv.getTransactionID(), srv.isRefreshCRLs());

            appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), validationSrv, srv.getAuthorizedHandlers()));

        }

        // Load OTP services
        for (OTPServiceType srv : app.getServices().getOtpService()) {
            TransportType transportTypeDvs = getTransportType(mapTransportType, (String) srv.getDvs());
            TransportType transportTypeDvsProvisioning = getTransportType(mapTransportType, (String) srv.getDvsProvisioning());
            SSLConnection dvsConnection = createSSLConnection(transportTypeDvs, srv.getServiceRequestTimeout(), loader);
            SSLConnection provConnection = createSSLConnection(transportTypeDvsProvisioning, srv.getServiceRequestTimeout(), loader);

            OTPService otpSrv = new OTPService(srv.getName(),
                    srv.getDescription(),
                    transportTypeDvsProvisioning.getId(),
                    provConnection,
                    transportTypeDvs.getId(), dvsConnection,
                    srv.getTransactionID(), srv.getDvsGroupID(),
                    srv.getAuthenticationFormat(), srv.getAuthenticationType());

            appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), otpSrv, srv.getAuthorizedHandlers()));

        }

        // Load authentication services
        for (AuthenticationServiceType srv : app.getServices().getAuthenticationService()) {
            TransportType transportType = getTransportType(mapTransportType, (String) srv.getDacs());
            SSLConnection dacsConnection = createSSLConnection(transportType, srv.getServiceRequestTimeout(), loader);

            AuthenticationService authSrv = new AuthenticationService(srv.getName(), srv.getDescription(), dacsConnection,
                    srv.getDefaultSecurityLevel(), srv.getDacsAppID(), srv.getDacsGroupID(), srv.getDacsContextID());

            appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), authSrv, srv.getAuthorizedHandlers()));
        }

        // Load PKI Connector services
        for (PKIConnectorServiceType srv : app.getServices().getPkiService()) {
            TransportType transportType = getTransportType(mapTransportType, (String) srv.getPkiconnector());
            SSLConnection pkiSSLConnection = createSSLConnection(
                    transportType, srv.getServiceRequestTimeout(), loader);
            PKIService pkiSrv = new PKIService(srv.getName(),
                    srv.getDescription(),
                    transportType.getId(),
                    pkiSSLConnection, srv.getApplictionID(), srv.getPolicyID());

            appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), pkiSrv, srv.getAuthorizedHandlers()));
        }

        for (TransformServiceType srv : app.getServices().getTransformService()) {
            OpenOfficeType openOfficeType = getOpenOfficeType(mapTransportType, (String) srv.getOpenOffice());
            TransformService transformSrv = new TransformService(openOfficeType.getPort());
            if (null != srv.getOutputMimeType()
                    && srv.getOutputMimeType().length() != 0) {
                transformSrv.setOutputMimeType(srv.getOutputMimeType());
            }
            appDesc.putService(new ServiceConf(srv.getName(), null, transformSrv, null));
        }

        if (app.getServices().getMailService() != null) {
            final MailServiceType srv = app.getServices().getMailService();

            String hostTmp = app.getServices().getMailService().getHost();
            final String host = (!hostTmp.isEmpty()) ? hostTmp : null;

            String protocolTmp = app.getServices().getMailService().getProtocol().toString().toLowerCase();
            final String protocol = (protocolTmp != null && !protocolTmp.isEmpty()) ? protocolTmp
                    : null;

            final Integer port = app.getServices().getMailService().getPort();

            String userTmp = app.getServices().getMailService().getUsername();
            final String user = (userTmp != null && !userTmp.isEmpty()) ? userTmp : null;

            String passTmp = app.getServices().getMailService().getPassword();
            final String password = (passTmp != null && !passTmp.isEmpty()) ? passTmp : null;

            final MailService mailSrv = new MailService(host, protocol, port,
                    user, password);

            if ((srv.getJavaMailProperty() != null)
                    && (srv.getJavaMailProperty().size() > 0)) {
                final Properties props = new Properties();
                for (PropertyType prop : srv.getJavaMailProperty()) {
                    if (prop.getValue() != null && prop.getValue().length() > 0) {
                        props.put(prop.getKey(), prop.getValue());
                    }
                }
                mailSrv.setJavaMailProperties(props);
            }

            appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), mailSrv, srv.getAuthorizedHandlers()));
        }

        if (app.getServices().getArchiveService() != null && archiveRepository != null) {
            final ArchiveServiceType srv = app.getServices().getArchiveService();

            List<Policy> policiesXml = app.getServices().getArchiveService().getPolicy();
            final Map<String, String> policies = new HashMap<String, String>();
            for (Policy policy : policiesXml) {
                final String company;
                if (policy.getCompany() != null) {
                    company = policy.getCompany();
                } else {
                    company = IArchiveService.DEFAULT_POLICY;
                }
                if (policy.getValue() == null) {
                    throw new EnvironmentException(
                            EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                            "Policy must be set");
                }
                if (policies.get(company) != null) {
                    throw new EnvironmentException(
                            EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                            "A policy for the company '%s' is already declared", company);
                }
                policies.put(company, policy.getValue());
            }
            boolean isMock;
            if (app.getServices().getArchiveService().isIsMock() == null) {
                isMock =  false;
            } else {
                isMock = app.getServices().getArchiveService().isIsMock();
            }
            final IArchiveService archiveSrv = new ArchiveService(policies, archiveRepository, isMock);

            appDesc.putService(new ServiceConf(srv.getName(), null, archiveSrv, null));
        }

	//timeStamp
	for (TimeStampingServiceType srv : app.getServices().getTimeStampingService()) {
	    TransportType transportType = getTransportType(mapTransportType, (String) srv.getTimestamp());

	    String temporaryPath = tenantStaticResourcesPath + File.separator + "tmp";  
	    File file = new File(temporaryPath);
	    if(!file.exists()){
		file.mkdirs();
	    }
	    TimeStampingService timeSrv = null;
	    
	    if (transportType.getSsl().equals(ENABLED)) {
		if (transportType.getSslConfiguration() == null) {
		    throw new EnvironmentException(
			    EnvironmentException.Code.DTP_ENV_CONFIGURATION,
			    "Not allowed to disable the ssl connection for this service [id='"
			    + transportType.getId() + "'] [endpointURL='"
			    + transportType.getEndpointURL() + "'], [activation='"
			    + transportType.getSsl() + "']");
		}
		timeSrv = new TimeStampingService(
			srv.getName(), srv.getDescription(), transportType.getId(),
			transportType.getEndpointURL(),
			srv.getServiceRequestTimeout(),
			transportType.getSslConfiguration().getKeyStore().getFilePath(),
			transportType.getSslConfiguration().getKeyStore().getType(),
			transportType.getSslConfiguration().getKeyStore().getPassword(),
			transportType.getSslConfiguration().getTrustStore().getFilePath(),
			transportType.getSslConfiguration().getTrustStore().getType(),
			transportType.getSslConfiguration().getTrustStore().getPassword(),
			srv.getTimestampHashAlgorithm(),
			srv.getReqPolicy(),
			srv.getSignatureLabelField(),
			srv.isIncludeTimestampingCert(),
			temporaryPath);
	    } else {
		timeSrv = new TimeStampingService(
			srv.getName(), srv.getDescription(), transportType.getId(),
			transportType.getEndpointURL(),
			srv.getServiceRequestTimeout(),
			srv.getTimestampHashAlgorithm(),
			srv.getReqPolicy(),
			srv.getSignatureLabelField(),
			srv.isIncludeTimestampingCert(),
			temporaryPath);
	    }
	    appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), timeSrv, srv.getAuthorizedHandlers()));
	}
	
	
        // Load dvs timeStampService services
        for (ValidationServiceType srv : app.getServices().getTimeStampService()) {
            TransportType transportType = getTransportType(mapTransportType, (String) srv.getDvs());
        
            SSLConnection dvsConnection = createSSLConnection(transportType, srv.getServiceRequestTimeout(), loader);

            ValidationService timeStampSvc = new ValidationService(
                    srv.getName(), srv.getDescription(),
                    transportType.getId(), dvsConnection,
                    srv.getTransactionID(), srv.isRefreshCRLs());

            appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), timeStampSvc, srv.getAuthorizedHandlers()));

        }
        
        // Load dvs long time validation services
        for (ValidationServiceType srv : app.getServices().getLtvService()) {
            TransportType transportType = getTransportType(mapTransportType, (String) srv.getDvs());
            
            SSLConnection dvsConnection = createSSLConnection(transportType, srv.getServiceRequestTimeout(), loader);

            ValidationService timeStampSvc = new ValidationService(
                    srv.getName(), srv.getDescription(),
                    transportType.getId(), dvsConnection,
                    srv.getTransactionID(), srv.isRefreshCRLs());

            appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), timeStampSvc, srv.getAuthorizedHandlers()));

        }
        
        // Load event services
        for (EventServiceType srv : app.getServices().getEventService()) {
            EventsServerType eventServer =  (EventsServerType) mapTransportType.get(srv.getServer());
            if (eventServer == null) {
                throw new EnvironmentException(
                        EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                        "Unable to load configuration. No reference to server [id='%s']", srv.getServer());
            } 
            List<SSLConnection> restEndpoints = new ArrayList<SSLConnection>();
            for (Endpoint e : eventServer.getRest().getEndpoint()) {

                SSLConnection cnx = (null == e.getSslConfiguration()) ? null : 
                    createSSLConnection(
                        (null == e.getSslConfiguration().getKeyStore())? null :
                            e.getSslConfiguration().getKeyStore().getFilePath(),
                        (null == e.getSslConfiguration().getKeyStore())? null : 
                            e.getSslConfiguration().getKeyStore().getPassword(), 
                        (null == e.getSslConfiguration().getKeyStore())? null : 
                            e.getSslConfiguration().getKeyStore().getType(),
                        e.getSslConfiguration().getTrustStore().getFilePath(),
                        e.getSslConfiguration().getTrustStore().getPassword(),
                        e.getSslConfiguration().getTrustStore().getType(),
                        e.getUrl(),
                        new Integer(defaultServiceRequestTimeout), loader);
                
                restEndpoints.add(cnx);
            } 
            
            RestEventService eventSrv = new RestEventService(srv.getName(),srv.getDescription(),
                    restEndpoints, eventServer.isInsecure(), eventServer.getId());

            appDesc.putService(new ServiceConf(srv.getName(), srv.getCompany(), eventSrv, srv.getAuthorizedHandlers()));
        }

        if (null != app.getExternalResource() && !app.getExternalResource().isEmpty()) {
            appDesc.externalResource = app.getExternalResource();
        }

        if (null != app.getUrls()) {
            UrlMap urlMap = app.getUrls();
            for (UrlMap.Url url : urlMap.getUrl()) {
                String key = url.getKey();
                String ap = url.getAbsoluteUrl();
                appDesc.urlMap.put(key, ap);
            }
        }
        if (null != app.getAllowedCompanies()) {
            appDesc.companies = app.getAllowedCompanies().getCompany();
        }

        if (app.getExports() != null) {
            if (app.getExports().getOpen() != null) {
                // Periodicity and Retention can't be null (required attributes 
                String periodicity = app.getExports().getOpen().getPeriodicity();
                String retention = app.getExports().getOpen().getRetention();
                ExportConf exportConf = new ExportConf(periodicity, retention);
                appDesc.openExport = exportConf;
            }
            if (app.getExports().getPending() != null) {
                // Periodicity and Retention can't be null (required attributes 
                String periodicity = app.getExports().getPending().getPeriodicity();
                String retention = app.getExports().getPending().getRetention();
                ExportConf exportConf = new ExportConf(periodicity, retention);
                appDesc.pendingExport = exportConf;
            }
            if (app.getExports().getModified() != null) {
                // Periodicity and Retention can't be null (required attributes 
                String periodicity = app.getExports().getModified().getPeriodicity();
                String retention = app.getExports().getModified().getRetention();
                ExportConf exportConf = new ExportConf(periodicity, retention);
                appDesc.modifiedExport = exportConf;
            }
            if (app.getExports().getClosed() != null) {
                String periodicity = app.getExports().getClosed().getPeriodicity();
                String retention = app.getExports().getClosed().getRetention();
                ExportConf exportConf = new ExportConf(periodicity, retention);
                appDesc.closedExport = exportConf;
            }
        }
    }

    private Class<?> getHandlerClazzFromName(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new EnvironmentException(ex,
                    EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "Unable to load the class '%s'. Check the configuration.",
                    className);
        }
        Class<?> parentClazz = clazz;
        while ((parentClazz = parentClazz.getSuperclass()) != null) {
            // loop till no parent or parent is TransactionHandler
            if (parentClazz.equals(com.dictao.dtp.core.transactions.TransactionHandler.class)) {
                break;
            }
        }
        if (parentClazz == null) {
            throw new EnvironmentException(
                    EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "Class '%s' should inherit from TransactionHandler. Check the configuration.",
                    className);
        }

        return clazz;
    }

    /**
     * loadFrontOfficeConfig
     *
     * @param is
     * @return
     * @throws FileNotFoundException
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException
     */
    private static FrontOfficeConfiguration loadFrontOfficeConfig(InputStream is)
            throws FileNotFoundException, JAXBException, SAXException,
            IOException {
    
        FrontOfficeConfiguration config = JAXBContextCache.<FrontOfficeConfiguration> unmarshall(
                FrontOfficeConfiguration.class, CONFIG_XSD_FILE, is);
        
        return config;
    }

    /**
     * loadTenantConfiguration
     *
     * @param is
     * @return
     * @throws FileNotFoundException
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException
     */
    private static TenantConfiguration loadTenantConfiguration(InputStream is)
            throws FileNotFoundException, JAXBException, SAXException,
            IOException {
        
        TenantConfiguration config = JAXBContextCache.<TenantConfiguration> unmarshall(
                TenantConfiguration.class, CONFIG_XSD_FILE, is);
        
        return config;
    }

    /**
     * LoadTenants
     *
     * @param path
     * @param loader
     * @param config
     * @param apps
     */
    private void loadExternalTenant(String path, ConfigFileLoader loader,
            FrontOfficeConfiguration config,
            Map<String, AppDesc> apps, int secureLevel) {
        LOG.entering("Load configuration");
        String folderName = "";
        String configFilePath = "";
        try {
            File dir = new File(path);
            for (File child : dir.listFiles()) {
                File confFile = new File(child.getAbsolutePath() + File.separator + "conf.xml");
                if (!confFile.exists()) {
                    LOG.warn("Tenant configuration path does not exist [path='" + confFile.getAbsolutePath() + "'");
                } else {
                    LOG.debug("Tenant configuration path '" + confFile.getAbsolutePath() + "'");
                    folderName = child.getName();
                    configFilePath = confFile.getAbsolutePath();
                    FileInputStream fis = new FileInputStream(confFile);
                    TenantConfiguration tc = loadTenantConfiguration(fis);
                    TenantFO tenant = tc.getTenant();
                    if (!tenant.getName().equals(folderName)) {
                        throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                                "Configuration folder name [folder name='" + folderName + "'] "
                                + "is different to tenant name [tenant name='" + tenant.getName() + "']");
                    }

                    ConfigFileLoader tenantLoader = new ConfigFileLoader(child.getAbsolutePath(), loader.getBaseURL());
                    loadTenant(tenant, tenantLoader, config, apps, child.getAbsolutePath(), secureLevel);

                    for (Application app : tenant.getApplication()) {
                        ApplicationConf appConf = appsMap.get(app.getId());
                        String pathApp = appConf.getExternalResource();
                        if (!new File(pathApp).exists()) {
                            throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                                    "Application folder '" + app.getId() + "' "
                                    + "does not exist [complete path='" + pathApp + "']");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new EnvironmentException(ex,
                    EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "Unable to load tenant configuration [folder name='" + folderName + "', configuration file path='" + configFilePath + "']");
        }
        LOG.info("DTP tenant configuration has loaded correctly.");
        LOG.exiting();
    }

    /**
     * Convert certificate bo into CertificateConf
     *
     * @param certificate bo
     * @return CertificateConf
     */
    private CertificateConf convertCertificate(Certificate cert) {
        CertificateConf certConf = new CertificateConf(cert.getX509Data().getX509SubjectName(), cert.getX509Data().getX509IssuerName());
        return certConf;

    }

    /**
     * Convert a list of certificate bo into a list of CertificateConf
     *
     * @param list of certificate bo
     * @return list of CertificateConf
     */
    private List<CertificateConf> convertCertificates(List<Certificate> listcert) {
        List<CertificateConf> finalist = new ArrayList<CertificateConf>();
        for (Certificate cert : listcert) {
            finalist.add(convertCertificate(cert));
        }
        return finalist;
    }
    
    
    

    private SSLConnection createSSLConnection(
            TransportType transport,
            Integer timeoutInSec,
            ConfigFileLoader loader) {
	if(transport.getSsl().equals(DISABLED) || transport.getSslConfiguration() == null){
	    throw new EnvironmentException(
                    EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "Not allowed to disable the ssl connection for this service [id='"
		    + transport.getId() + "'] [endpointURL='"
		    + transport.getEndpointURL() + "']");
	}
        return this.createSSLConnection(
                transport.getSslConfiguration().getKeyStore().getFilePath(),
                transport.getSslConfiguration().getKeyStore().getPassword(),
                transport.getSslConfiguration().getKeyStore().getType(),
                transport.getSslConfiguration().getTrustStore().getFilePath(),
                transport.getSslConfiguration().getTrustStore().getPassword(),
                transport.getSslConfiguration().getTrustStore().getType(),
                transport.getEndpointURL(),
                timeoutInSec != null ? new Integer(timeoutInSec * 1000) : new Integer(defaultServiceRequestTimeout),
                loader);
    }
}