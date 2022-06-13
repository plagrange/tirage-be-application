package com.dictao.dtp.web.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dictao.dtp.core.data.SignatureLayout;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.services.internal.SSLConnection;
import com.dictao.dtp.core.services.sign.AppletSignService;
import com.dictao.dtp.core.services.sign.SignService;
import com.dictao.dtp.core.transactions.CertificateConf;
import com.dictao.dtp.core.transactions.ExportConf;
import com.dictao.dtp.core.transactions.HandlerTypeConf;
import com.dictao.dtp.core.transactions.ServiceConf;
import com.dictao.dtp.core.transactions.ServiceMapConf;
import com.dictao.dtp.core.transactions.UserAgentConf;
import com.dictao.dtp.core.transactions.UserAgentWildCard;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import com.dictao.util.net.ssl.ClientSSLParameter;
import com.dictao.util.net.ssl.ServerSSLParameter;

public abstract class ConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);
    private static boolean confUp = true;

    public static boolean isConfUp() {
        return confUp;
    }

    protected class AppDesc {

        List<CertificateConf> certificates = new ArrayList<CertificateConf>();
        List<UserAgentConf> userAgentsWhiteList = null;
        List<UserAgentConf> userAgentsBlackList = null;
        List<UserAgentWildCard> userAgentsWildCard = null;
        protected ServiceMapConf appServices = new ServiceMapConf();
        List<String> companies = new ArrayList<String>();
        Map<String, HandlerTypeConf> handlerTypes = new HashMap<String, HandlerTypeConf>();
        Map<String, String> urlMap = new HashMap<String, String>();
        ExportConf openExport = null;
        ExportConf pendingExport = null;
        ExportConf modifiedExport = null;
        ExportConf closedExport = null;
        String externalResource = null;
        Map<String, SignatureLayout> layouts = new HashMap<String, SignatureLayout>();
        
           
        /**
         * add unique service provider to configuration services
         * @param service must be unique (name + company + authorized handlers)
         */
        protected void putService(ServiceConf service){

            // update signature layouts
            if (!layouts.isEmpty()) {
                if (service.getService() instanceof SignService) {
                    SignService signSrv = (SignService) service.getService();
                    signSrv.setLayouts(layouts);
                } else if (service.getService() instanceof AppletSignService) {
                    AppletSignService signSrv = (AppletSignService) service.getService();
                    signSrv.setLayouts(layouts);
                }
            }
            
            if (null != appServices.put(service, handlerTypes)) {
                
                /*
                 *  FIXME KCH : uncomment when mergeApplication() will be implemented.
                 *   
                String handlers = null;
                if (service.getHandlerList() != null) {
                    for (String type : service.getHandlerList()) {
                        handlers = ((handlers == null) ? "" : handlers + ",") + type;
                    }
                }
                throw new EnvironmentException(
                        EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                        "Cannot load configuration: more than one service [name='%s', company='%s', handlers='%s'] has been found.",
                        service.getName(), service.getCompany(),handlers);
                  */
            }
        }  
    }

    public void load(InputStream is, ConfigFileLoader loader) throws Exception {
        try {
            read(is, loader);
        } catch (Exception ex) {
            synchronized (this) {
                confUp = false;
            }
            throw ex;
        }
    }
    


    abstract protected void read(InputStream is, ConfigFileLoader loader);

    /**
     * @param transport
     *            : endpointUrl, p12FilePath, p12Password, jksFilePath,
     *            jksPassword
     * @return appropriate sslConnection
     */
    protected SSLConnection createSSLConnection(String p12FilePath,
            String p12Password, String p12Type, String jksFilePath,
            String jksPassword, String jksType, String endpointUrl,
            Integer timeoutMs,
            ConfigFileLoader loader) {
        LOG.entering(
                "SSLConnection parameters [p12FilePath=\"%s\", jksFilePath=\"%s\", endpointUrl=\"%s\"]",
                p12FilePath, jksFilePath, endpointUrl);

        SSLConnection sslConnection = new SSLConnection();
        
       if (null != p12FilePath || null != p12Password || null != p12Type) {
    
            try {
                InputStream p12Stream = loader.getFileStream(p12FilePath);
                sslConnection.setClientSSLParameter(new ClientSSLParameter(
                        p12Stream, p12Type, p12Password.toCharArray()));
            } catch (Exception ex) {
                LOG.debug("unable to settup keyStrore configuration for %s",
                        p12FilePath);
                throw new EnvironmentException(ex,
                        EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                        "unable to settup keyStrore configuration");
            }  
        }
      
        try {
            InputStream jksStream = loader.getFileStream(jksFilePath);
            sslConnection.setServerSSLParameter(new ServerSSLParameter(
                    jksStream, jksType, jksPassword.toCharArray()));
        } catch (Exception ex) {
            LOG.debug("unable to settup trustStore configuration for %s",
                    jksFilePath);
            throw new EnvironmentException(ex,
                    EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "unable to settup trustStore configuration");
        }

        sslConnection.setEndpointUrl(endpointUrl);
        sslConnection.setTimeout(timeoutMs);
        LOG.exiting();
        return sslConnection;

    }
}
