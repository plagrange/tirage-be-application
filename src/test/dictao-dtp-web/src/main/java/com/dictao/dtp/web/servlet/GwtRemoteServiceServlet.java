package com.dictao.dtp.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.exceptions.UserException.Code;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

/**
 * 
 * @author VRB adapted by KCH
 */
public final class GwtRemoteServiceServlet extends RemoteServiceServlet
        implements RemoteService {

    private static final long serialVersionUID = -1728943384152962138L;
    private static final Logger LOG = LoggerFactory
            .getLogger(GwtRemoteServiceServlet.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user." + GwtRemoteServiceServlet.class.getName());

    @Inject 
    TransactionFactory txFct;

    @Override
    public void init() {
        LOG.debug("WELD GWT service exporter deployed.");
    }

    @Override
    public String processCall(final String payload)
            throws SerializationException {
        try {
            Object handler = getReference(getThreadLocalRequest());
            RPCRequest rpcRequest = RPC.decodeRequest(payload,
                    handler.getClass(), this);
            onAfterRequestDeserialized(rpcRequest);
            LOG.debug("Invoking " + handler.getClass().getName() + "."
                    + rpcRequest.getMethod().getName());
            return RPC.invokeAndEncodeResponse(handler, rpcRequest.getMethod(),
                    rpcRequest.getParameters(),
                    rpcRequest.getSerializationPolicy());
        } catch (UserException ex) {
            LOGU.error(ex, "An UserException was thrown while processing this call.");
            Throwable throwable = new Throwable(ex.getMessage());
            return RPC.encodeResponseForFailure(null, throwable);
        } catch (IncompatibleRemoteServiceException ex) {
            LOG.error(ex, "An IncompatibleRemoteServiceException was thrown while processing this call.");
            return RPC.encodeResponseForFailure(null, ex);
        }
    }
    
    /* ********************* PROTECTED/PRIVATE METHODS ****************** */

    /**
     * Bug with GWT when it's behind a reverse proxy. bug report : {@link}
     * http://code.google.com/p/google-web-toolkit/issues/detail?id=4817
     * 
     * Force GWT to load serialization policy file starting from ContextName
     * instead of URL.
     */
    @Override
    protected SerializationPolicy doGetSerializationPolicy(
            HttpServletRequest request, String moduleBaseURL, String strongName) {
        LOG.entering("moduleBaseURL: %s, strongName: %s", moduleBaseURL,
                strongName);
        // The serialization policy path depends only by contraxt path
        SerializationPolicy serializationPolicy = null;
        String contextRelativePath = "/ui/dtp/";
        String serializationPolicyFilePath = SerializationPolicyLoader
                .getSerializationPolicyFileName(contextRelativePath
                        + strongName);
        // Open the RPC resource file and read its contents.
        InputStream is = getServletContext().getResourceAsStream(
                serializationPolicyFilePath);
        try {
            if (is != null) {
                try {
                    serializationPolicy = SerializationPolicyLoader
                            .loadFromStream(is, null);
                } catch (ParseException e) {
                    log("ERROR: Failed to parse the policy file '"
                            + serializationPolicyFilePath + "'", e);
                } catch (IOException e) {
                    log("ERROR: Could not read the policy file '"
                            + serializationPolicyFilePath + "'", e);
                }
            } else {
                String message = "ERROR: The serialization policy file '"
                        + serializationPolicyFilePath
                        + "' was not found; did you forget to include it in this deployment?";
                log(message);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore this error
                }
            }
        }
        LOG.exiting();
        return serializationPolicy;
    }

    private RemoteService getReference(final HttpServletRequest request) {
        final String url = request.getRequestURI();
        final String beanName = url.substring(url.lastIndexOf("/") + 1);
        LOG.debug("Bean for service '%s'", beanName) ;
        final RemoteService bean = (RemoteService) getReference(this.getServletContext(),beanName);
        if (!(bean instanceof RemoteService)) {
            throw new UserException(Code.DTP_USER_INVALID_CONTEXT, "Bean is not a GWT RemoteService [service='%s']", beanName);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getReference(ServletContext context, String beanName) {
        BeanManager bmgr = (BeanManager) context
                .getAttribute("org.jboss.weld.environment.servlet."
                        + BeanManager.class.getName());
        Bean<T> bean = (Bean<T>) bmgr.getBeans(beanName).iterator().next();
        CreationalContext<T> ctx = bmgr.createCreationalContext(bean);
        return bean.create(ctx);
    }
}
