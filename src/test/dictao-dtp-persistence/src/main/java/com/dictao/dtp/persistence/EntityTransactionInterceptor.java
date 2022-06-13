package com.dictao.dtp.persistence;

import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.Serializable;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Declarative JPA EntityTransactions
 *
 * @author Gavin King
 */
@Transactional
@Interceptor
public class EntityTransactionInterceptor implements Serializable {

    private static final long serialVersionUID = -7108928947904757279L;
    private static final Logger LOG = LoggerFactory.getLogger(EntityTransactionInterceptor.class);

    private static EntityManager getEntityManager() {
        EntityManager entityManager = null;
        try {
            entityManager = TreadLocalEntityManager.getCurrentEntityManager();
        } catch (IllegalStateException ex) {
            // Do nothing, no entity manager, we will create a new one
        }
        if (entityManager == null) {
            entityManager = TreadLocalEntityManager.createEntityManager();
        }
        return entityManager;
    }

    @AroundInvoke
    public Object aroundInvoke(InvocationContext ic) throws Exception {
        final EntityManager em = getEntityManager();
        boolean needToClose = true;
        try {
            final EntityTransaction transaction = em.getTransaction();
            boolean isOwnTransaction = !em.getTransaction().isActive();
            needToClose = isOwnTransaction;
            if (isOwnTransaction) {
                transaction.begin();
            }
            try {
                Object result = ic.proceed();
                if (isOwnTransaction) {
                    if (transaction.getRollbackOnly()) {
                        LOG.info("Rolling back transaction, as flag has been set");
                        transaction.rollback();
                    } else {
                        transaction.commit();
                    }
                }
                return result;
            } catch (Exception e) {
                if (isOwnTransaction) {
                    LOG.info("Rolled back transaction due to exception", e);
                    transaction.rollback();
                } else {
                    LOG.info("Setting rollback-only flag due to exception", e);
                    transaction.setRollbackOnly();
                }
                throw e;
            }
        } finally {
            if (needToClose && em.isOpen()) {
                em.close();
                TreadLocalEntityManager.removeCurrentEntityManager();
            }
        }
    }
}
