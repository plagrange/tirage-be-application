package com.dictao.dtp.web.faces;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.web.INavHandler;
import com.dictao.dtp.web.data.RedirectStatus;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.util.Iterator;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.ServletContext;

public class FaultHandler extends ExceptionHandlerWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(FaultHandler.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user." + FaultHandler.class.getName());
    /**
     * depth allowed for retrieving exception instance 
     * and it's intended for preventing infinite loop.
     */
    private static final int DEPTH_MAX = 20;

    private ExceptionHandler wrapped;

    public FaultHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        boolean doRedirect = false;
        RedirectStatus status = null ;
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
            Throwable root_t = context.getException();

            if (null != root_t) {
                
                doRedirect = true;
                Throwable t = root_t;
                int depth = 0;
                
                while (null != t) {
                    if (t instanceof ViewExpiredException) {
                        status = RedirectStatus.SESSION_EXPIRED;
                        LOGU.error(root_t, "Session expired: %s", t.getMessage());
                        break;
                    }
                    if (t instanceof UserException) {

                        UserException userException = (UserException)t;

                        switch(userException.getCode()){

                            case DTP_USER_INVALID_CONTEXT:
                                status = RedirectStatus.CONTEXT_ERROR;
                                break;

                            case DTP_USER_ILLEGAL_STATE:
                                status = RedirectStatus.TRANSACTION_ERROR;
                                break;

                            default:
                                status = RedirectStatus.USER_ERROR;
                        }

                        LOGU.error(root_t);
                        break;
                    }
                    if (t instanceof EnvironmentException) {
                        status = RedirectStatus.ENVIRONMENT_ERROR;
                        LOG.error(root_t);
                        break;
                    }
                    if (t == t.getCause() || depth > DEPTH_MAX) { // prevent infinite loop
                        break;
                    }
                    t = t.getCause();
                    depth++;
                }
                if (status == null) {
                    status = RedirectStatus.INTERNAL_ERROR;
                    LOG.error(root_t);
                }
            }
            i.remove();
        }
        if (doRedirect) {
            ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            BeanManager bmgr = (BeanManager)sc.getAttribute("org.jboss.weld.environment.servlet." + BeanManager.class.getName());
            Bean<NavHandler> bean = (Bean<NavHandler>) bmgr.getBeans(INavHandler.class).iterator().next();
            CreationalContext<NavHandler> ctx = bmgr.createCreationalContext(bean);
            INavHandler navHandler = (INavHandler) bmgr.getReference(bean, INavHandler.class, ctx);
            
            navHandler.redirectOnError(status);
        }
        // At this point, the queue will not contain any exception events.
        // Therefore, let the parent handle them.
        getWrapped().handle();
    }
}
