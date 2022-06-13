package com.dictao.dtp.persistence;

import com.dictao.dtp.persistence.entity.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import javax.enterprise.context.RequestScoped;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.eclipse.persistence.config.QueryHints;




@RequestScoped
public class TransactionService extends DbService implements Serializable {

    private static final long serialVersionUID = 5487792771951122300L;
    
    private static final int MAX_RESULT = 1000;
    
    @Transactional
    public void create(Step step) {
        getEntityManager().persist(step);
    }

    @Transactional
    public void create(Transaction transaction) {
        transaction.setUpdateTimestamp(new Date());
        getEntityManager().persist(transaction);
    }

    @Transactional
    public Transaction update(Transaction transaction) {
        EntityManager em = getEntityManager();
        transaction.setUpdateTimestamp(new Date());
        if (transaction.getId() == null) {
            em.persist(transaction);
            return transaction;
        }
        transaction = em.merge(transaction);
        return transaction;
    }

    @Transactional
    public Transaction find(String transactionID) {
        Query query = getEntityManager().createNamedQuery(Transaction.FIND_BY_TXID);
        query.setParameter("transactionID", transactionID);
        query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
        List<Transaction> transactionList = query.getResultList();
        Transaction res = null;
        if (transactionList.size() > 0) {
            res = transactionList.get(0);
        }
        return res;
    }

    @Transactional
    public long getTransactionsNumber() {
        long transactions = 0;
        Query query = getEntityManager().createNamedQuery(Transaction.COUNT_TX);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date date = calendar.getTime();
        query.setParameter("updateTimestamp", date);
        transactions = (Long) query.getSingleResult();
        return transactions;
    }

    @Transactional
    public void updateExpired(final String applicationId, final Date expirationTimestamp) {
        Query query = getEntityManager().createNamedQuery(Transaction.UPDATE_EXPIRED);
        query.setParameter("statusToSet", TransactionStatusEnum.Closed);
        query.setParameter("subStatusToSet", TransactionSubStatusEnum.Expired);
        query.setParameter("applicationID", applicationId);
        query.setParameter("expirationTimestamp", expirationTimestamp);
        query.setParameter("endTimestamp", new Date());
        query.setParameter("status", TransactionStatusEnum.Closed);
        query.executeUpdate();
    }
    
    @Transactional
    public Long countOpenedTransactions(String applicationId, 
            Date updadDateRangeStart, Date updatDateRangeEnd) {
        Query query = getEntityManager().createNamedQuery(TransactionBase.COUNT_OPEN_TX_BY_UPDATETIMERANGE);
        query.setParameter("updateTimestampBegin", new Timestamp(updadDateRangeStart.getTime()));
        // Substract 1 millisecond to ignore 
        query.setParameter("updateTimestampEnd", new Timestamp(updatDateRangeEnd.getTime() - 1L));
        query.setParameter("applicationID", applicationId);
        query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
        return (Long)query.getSingleResult();
    }
    
    @Transactional
    public List<String> findOpenedTransactions(String applicationId, 
            Date updadDateRangeStart, Date updatDateRangeEnd) {
        Query query = getEntityManager().createNamedQuery(TransactionBase.FIND_OPEN_TX_BY_UPDATETIMERANGE);
        query.setParameter("updateTimestampBegin", new Timestamp(updadDateRangeStart.getTime()));
        query.setParameter("updateTimestampEnd", new Timestamp(updatDateRangeEnd.getTime() - 1L));
        query.setParameter("applicationID", applicationId);
        query.setMaxResults(MAX_RESULT);
        query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
        return query.getResultList();
    }
    
    @Transactional
    public Long countModifiedTransactions(String applicationId, 
            Date updadDateRangeStart, Date updatDateRangeEnd) {
        Query query = getEntityManager().createNamedQuery(TransactionBase.COUNT_MODIFIED_TX_BY_UPDATETIMERANGE);
        query.setParameter("updateTimestampBegin", new Timestamp(updadDateRangeStart.getTime()));
        // Substract 1 millisecond to ignore 
        query.setParameter("updateTimestampEnd", new Timestamp(updatDateRangeEnd.getTime() - 1L));
        query.setParameter("applicationID", applicationId);
        query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
        return (Long)query.getSingleResult();
    }
    
    @Transactional
    public List<String> findModifiedTransactions(String applicationId, 
            Date updadDateRangeStart, Date updatDateRangeEnd) {
        Query query = getEntityManager().createNamedQuery(TransactionBase.FIND_MODIFIED_TX_BY_UPDATETIMERANGE);
        query.setParameter("updateTimestampBegin", new Timestamp(updadDateRangeStart.getTime()));
        query.setParameter("updateTimestampEnd", new Timestamp(updatDateRangeEnd.getTime() - 1L));
        query.setParameter("applicationID", applicationId);
        query.setMaxResults(MAX_RESULT);
        query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
        return query.getResultList();
    }
    
    @Transactional
    public Long countClosedTransactions(String applicationId, 
            Date endDateRangeStart, Date endDateRangeEnd) {
        Query query = getEntityManager().createNamedQuery(TransactionBase.COUNT_CLOSED_TX_BY_ENDTIMERANGE);
        query.setParameter("endTimestampBegin", new Timestamp(endDateRangeStart.getTime()));
        query.setParameter("endTimestampEnd", new Timestamp(endDateRangeEnd.getTime()));
        query.setParameter("applicationID", applicationId);
        query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
        return (Long)query.getSingleResult();
    }    
    
    @Transactional
    public List<String> findClosedTransactions(String applicationId, 
            Date endDateRangeStart, Date endDateRangeEnd) {
        Query query = getEntityManager().createNamedQuery(TransactionBase.FIND_CLOSED_TX_BY_ENDTIMERANGE);
        query.setParameter("endTimestampBegin", new Timestamp(endDateRangeStart.getTime()));
        query.setParameter("endTimestampEnd", new Timestamp(endDateRangeEnd.getTime()));
        query.setParameter("applicationID", applicationId);
        query.setMaxResults(MAX_RESULT);
        query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
        return query.getResultList();
    }    
  
}

