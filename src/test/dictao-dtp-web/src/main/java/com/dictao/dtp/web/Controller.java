package com.dictao.dtp.web;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.log4j.MDC;

import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.core.transactions.IUAHandler;
import com.dictao.dtp.core.transactions.IUASignPersoHandler;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;

/**
 * 
 * @author msauvee
 */
public class Controller<TH extends TransactionHandler> {

    @Inject
    private TransactionFactory txService;
    @Inject
    private INavHandler navHandler;
    @Inject
    private ApplicationListConf appsConf;

    public Controller() {
    }

    /*
     * Constructor only for tests. Should not be used !!!
     */
    protected Controller(TransactionFactory txService, INavHandler navHandler) {
        this.txService = txService;
        this.navHandler = navHandler;
    } 

    protected <U extends IUAHandler, T extends TransactionHandler> U findUASignPersoHandler(String tenant,
            String applicationName, String externalId, Class<U> clazz) {
        IUAHandler handler = (IUASignPersoHandler) txService.findUA(tenant, applicationName, externalId, clazz);
        return (U) handler;
    }

    @Deprecated
    protected <U extends IUAHandler, T extends TransactionHandler> U findUASignPersoHandler(String applicationId,
            String externalId, Class<U> clazz) {
        IUAHandler handler = (IUASignPersoHandler) txService.findUA(applicationId, externalId, clazz);
        return (U) handler;
    }

    protected TH findTransaction(String accessId) {

        MDC.put("AID", accessId);
        TH handler = (TH) txService.find(accessId);
        navHandler.setTransactionHandler(handler);
        if ((handler == null) || !handler.isValidUserAccess())
            throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                    "Access unauthorized");

        if(handler.getTransactionId() != null)
            MDC.put("TID", handler.getTransactionId());

        return handler;
    }

    protected void redirectWithStatus(String statusName) throws IOException {
        navHandler.redirectWithStatus(statusName);
    }

    /**
     * @param url
     *            FULL URL for redirection
     * @throws IOException
     */
    protected void redirect(String url) throws IOException {
        navHandler.redirect(url);
    }
}
