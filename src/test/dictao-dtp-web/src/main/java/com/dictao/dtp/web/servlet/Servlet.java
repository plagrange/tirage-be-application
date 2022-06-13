package com.dictao.dtp.web.servlet;

import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.web.INavHandler;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

/**
 * 
 * @author msauvee
 */
public class Servlet extends HttpServlet {
    private static final long serialVersionUID = -6255035642151890301L;
    private static final String TYPE_X509 = "javax.servlet.request.X509Certificate";
    private static final Logger LOG = LoggerFactory.getLogger(Servlet.class);

    private BeanManager bmgr;
    private TransactionFactory txSvc;
    private INavHandler navHandler;
    private ApplicationListConf appConfList;

    @Override
    public void init() throws ServletException {
        bmgr = (BeanManager) getServletContext().getAttribute(
                "org.jboss.weld.environment.servlet."
                + BeanManager.class.getName());
        txSvc = getReference(TransactionFactory.class);
        navHandler = getReference(INavHandler.class);
        appConfList = getReference(ApplicationListConf.class);
    }

    protected TransactionFactory getTransactionFactory() {
        return txSvc;
    }
    
    protected ApplicationListConf getApplicationListConf() {
        return appConfList;
    }
 
    /*protected ApplicationConf getApplicationConf(String tenant, String applicationID,
            HttpServletRequest servletRequest) {
        return appConfList.getConfigurationID(tenant, applicationID, getSSLCertificate(servletRequest));
    }*/
    
    protected INavHandler getNavHandler() {
        return navHandler;
    }

    protected <T> T getReference(Class<?> clazz) {
        Bean<T> bean = (Bean<T>) bmgr.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = bmgr.createCreationalContext(bean);
        return (T) bmgr.getReference(bean, clazz, ctx);
    }

    protected X509Certificate getSSLCertificate(HttpServletRequest servletRequest) {

        if (servletRequest != null) {
            X509Certificate[] sslCerts = (X509Certificate[]) servletRequest.getAttribute((TYPE_X509));
            if ((sslCerts != null) && (sslCerts.length > 0)) {
                return sslCerts[0];
            }
        }
        return null;
    }
    

    /**
     * Common method for sending an HTTP error code of current HTTP Response
     * @param response
     * @param errorStatusCode
     * @throws IOException
     */
    protected void sendError(HttpServletResponse response, int errorStatusCode) throws IOException {
        if (!response.isCommitted()) {
            response.reset();
            response.sendError(errorStatusCode);
        } else {
            LOG.warn("the response has already been committed,"
                    + " After using sendError method, the response should be considered to be committed and should not be written to.");
        }
    }
}