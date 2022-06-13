package com.dictao.dtp.web;

import java.io.IOException;

import com.dictao.dtp.core.data.RequestInfo;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.web.data.RedirectStatus;

/**
 *
 * @author msauvee
 */
public interface INavHandler {

    String getBasePath();
    /**
     * redirect on given URL 
     * @throws IOException
     */
    void redirect(String url);

    /**
     * redirect on error with concatenation of the status code
     * @param url
     * @param status
     * @throws IOException
     */
    void redirectOnError(RedirectStatus status);

    /**
     * redirect with the specified status
     * @param statusName A url-friendly status (OK, CLICK_ON_CANCEL, ...)
     * @throws IOException
     */
    void redirectWithStatus(String statusName);

    /**
     * to use systematically on each find or update of TransactionHandler
     * @param handler
     */
    void setTransactionHandler(TransactionHandler handler);

    RequestInfo getRequestInfo();
}
