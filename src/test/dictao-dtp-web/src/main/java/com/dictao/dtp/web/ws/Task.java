// Copyright (c) 2008 Dictao. All rights reserved.
// Confidential and proprietary.
// @file:ResultTask.java
package com.dictao.dtp.web.ws;

import com.dictao.dtp.core.transactions.TransactionContext;
import com.dictao.dtp.web.servlet.Monitoring;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.apache.log4j.MDC;

/**
 * Provides the base implementation of methods that procudes a result. Mostly used
 * for building anonymous class executed via a RunnableContainer class.
 */
public abstract class Task<V, UFE extends Exception, EFE extends Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(Task.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."+Task.class.getName());
    private final ExceptionConverter<UFE, EFE> ec;
    private final X509Certificate certificate;
    
    public Task(ExceptionConverter ec, X509Certificate certificate) {
        this.ec = ec;
        this.certificate = certificate;
    }

    public V execute() throws UFE, EFE {
        Logger logu = LOGU;
        Date start = new Date();
        long duration = 0;
        try {
            Monitoring.start();
            Logger tmp = getUserLogger();
            if (tmp != null)
                logu = tmp;
            logu.info("%s", this);
            TransactionContext.set(certificate);
            V result = onRun();
            Monitoring.stop();
            duration = new Date().getTime() - start.getTime();
            logu.info("%s executed in %d ms", this, duration);
            MDC.clear();
            return result;
        } catch (com.dictao.dtp.core.exceptions.UserException e) {
            Monitoring.stop();
            logu.error(e, "Failed to %s in %d ms. Cause: %s", this, duration, e.getMessage());
            MDC.clear();
            throw ec.toWSUserException(e);
        } catch (com.dictao.dtp.core.exceptions.EnvironmentException e) {
            Monitoring.stop();
            LOG.error(e, "Failed to %s in %d ms. Cause: %s", this,duration,  e.getMessage());
            MDC.clear();
            throw ec.toWSEnvironmentException(e);
        } catch (Exception e) {
            Monitoring.stop();
            LOG.error(e, "Failed to %s in %d ms. Cause: %s", this, duration, e.getMessage());
            MDC.clear();
            throw ec.toInternalError();
        }finally{
            TransactionContext.unset();
        }
    }

    public abstract V run() throws Exception;
    
    protected V onRun() throws Exception {
        return run();
    }
    
    protected Logger getUserLogger() {
        return LOGU;
    }
}
