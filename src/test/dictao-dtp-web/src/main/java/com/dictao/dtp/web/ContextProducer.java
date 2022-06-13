package com.dictao.dtp.web;

import java.util.Locale;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.dictao.dtp.core.ResourceBundleHandler;
import com.dictao.dtp.core.data.IAgent;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.web.webagent.WebAgentSSO;

public class ContextProducer {

    @Inject
    TransactionFactory txFct;

    @Produces
    @RequestScoped
    public FacesContext getFacesContext() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null) {
            throw new ContextNotActiveException("FacesContext is not active");
        }
        return ctx;
    }

    @Produces
    @RequestScoped
    public INavHandler getNavigationHandler() {
        if (FacesContext.getCurrentInstance() != null) {
            return new com.dictao.dtp.web.faces.NavHandler(getFacesContext(),
                    txFct);
        }
        return new com.dictao.dtp.web.gwt.NavHandler(HttpContext.getRequest(),
                HttpContext.getResponse(), txFct);
    }

    @Produces
    @RequestScoped
    @Any
    public ResourceBundleHandler getResourceBundleHandler() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null)
            // HttpContext.getRequest().getLocale()
            return new ResourceBundleHandler(Locale.FRENCH);

        if (FacesContext.getCurrentInstance().getViewRoot() != null) {
            return new ResourceBundleHandler(ctx.getViewRoot().getLocale());
        } else {
            return new ResourceBundleHandler(ctx.getApplication()
                    .getViewHandler().calculateLocale(ctx));
        }
    }

    @Produces
    @RequestScoped
    public IAgent getWebAgent() {
    	FacesContext ctx = FacesContext.getCurrentInstance();
        Locale locale = null;
        if (ctx != null){
        	if (FacesContext.getCurrentInstance().getViewRoot() != null){
        		locale = ctx.getViewRoot().getLocale();
        	} else {
        		ctx.getApplication()
                .getViewHandler().calculateLocale(ctx);
        	}
            return new WebAgentSSO((HttpServletRequest) ctx
                    .getExternalContext().getRequest(), locale);
        }
        return new WebAgentSSO(HttpContext.getRequest(), locale);
    }
}
