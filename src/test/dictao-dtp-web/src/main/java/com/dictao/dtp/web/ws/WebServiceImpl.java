package com.dictao.dtp.web.ws;

import com.dictao.dtp.core.ResourceBundleHandler;
import java.io.StringWriter;
import java.security.cert.X509Certificate;

import javax.annotation.Resource;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.dictao.dtp.core.data.RequestInfo;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.IService;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.xsd.dtp.common.v2012_03.Metadata;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author msauvee
 */
public abstract class WebServiceImpl {

    private static final String TYPE_X509 = "javax.servlet.request.X509Certificate";
    private static final int METADATA_TOSTRING_TRUNCATE_THRESHOLD = 3421;
    private TransactionFactory txf = null;
    @Resource
    WebServiceContext context;

    private <T> T getReference(Class<?> clazz) {
        ServletContext sc = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        BeanManager bmgr = (BeanManager) sc.getAttribute("org.jboss.weld.environment.servlet." + BeanManager.class.getName());
        Bean<T> bean = (Bean<T>) bmgr.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = bmgr.createCreationalContext(bean);
        return (T) bmgr.getReference(bean, clazz, ctx);
    }

    protected ResourceBundleHandler getResourceBundleHandlerReference() {
        //return new ResourceBundleHandler(getLocale());
        return getReference(ResourceBundleHandler.class);
    }

    protected ApplicationListConf getConfigurationReference() {
        //return new ResourceBundleHandler(getLocale());
        return getReference(ApplicationListConf.class);
    }

    protected Locale getLocale() {
        MessageContext msgContext = context.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) msgContext.get(MessageContext.SERVLET_REQUEST);
        return request.getLocale();
    }

    protected TransactionFactory getTransactionFactory() {
        if (txf == null) {
            txf = getReference(TransactionFactory.class);
        }
        return txf;
    }

    protected X509Certificate getSSLCertificate() {
        HttpServletRequest servletRequest = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        // Si la requete existe
        if (servletRequest != null) {
            // On recupère un ensemble de certificat associé à la
            // connexion
            X509Certificate[] sslCerts = (X509Certificate[]) servletRequest.getAttribute(TYPE_X509);
            // Si on a aucun certificat
            if ((sslCerts != null) && (sslCerts.length > 0)) {
                return sslCerts[0];
            }
        }
        return null;
    }

    /**
     * ensure an authenticated access to ECMService with check SSL certificate
     * @param transactionId
     * @param service
     * @return
     */
    protected IService getService(
            final String transactionId,
            final String service) {
        Object osvc = getTransactionFactory().getService(transactionId, service, getSSLCertificate());
        if (osvc == null || !(osvc instanceof IService)) {
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Unable to access to service '%s' for transactionId '%s'", service, transactionId);
        }
        return (IService) osvc;
    }

    protected String metadataToString(Metadata mds) {
        String xml = "null";
        if (mds != null) {
            try {
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();
                StringWriter buffer = new StringWriter();
                transformer.transform(new DOMSource(mds.getAny()), new StreamResult(buffer));
                xml = buffer.toString().replaceAll("\\r\\n", "\n").replaceAll("\\n", "\t").replaceAll("\\t", "");

//                final Document doc = xmlAny.getAny().getOwnerDocument();
//                final DOMImplementation impl = doc.getImplementation();
//                DOMImplementationLS implLS = (org.apache.xerces.dom.CoreDOMImplementationImpl) impl.getFeature("LS", "3.0");
//                LSSerializer writer = implLS.createLSSerializer();
//                xml = writer.writeToString(xmlAny.getAny());
            } catch (Exception e) {
                throw new UserException(e, UserException.Code.DTP_USER_INVALID_CONTEXT, "Unable to convert org.w3c.dom.Element to String");
            }
        }
        return xml;
    }
    
    protected RequestInfo collectRequestInfo(){
        HttpServletRequest request = (HttpServletRequest)context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        RequestInfo requestInfo;
        requestInfo = new RequestInfo(request.getRemoteAddr(), request.getRemoteHost(), request.getRemotePort());
        return requestInfo;
    }
}
