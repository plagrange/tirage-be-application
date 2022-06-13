/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dictao.dtp.web.ws;

import com.dictao.dtp.persistence.EntityTransactionInterceptor;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.interceptor.InvocationContext;

/**
 *
 * @author msauvee
 */
public abstract class TransactionalTask<V, UFE extends Exception, EFE extends Exception> extends Task { 

    public TransactionalTask(ExceptionConverter ec, X509Certificate certificate) {
        super(ec, certificate);
    }

    @Override
    public abstract V run() throws Exception;
    
    @Override
    protected V onRun() throws Exception {
        EntityTransactionInterceptor eti = new EntityTransactionInterceptor();
        InvocationContext ic = new InvocationContext() {
            @Override
            public Object getTarget() {
                return this;
            }

            @Override
            public Object getTimer() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Method getMethod() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Object[] getParameters() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setParameters(Object[] params) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Map<String, Object> getContextData() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public V proceed() throws Exception {
                return run();
            }
        };
        return (V)eti.aroundInvoke(ic);
    }    
}
