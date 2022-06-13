package com.dictao.dtp.persistence;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Query;

import com.dictao.dtp.persistence.entity.UserAccess;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

/**
 * 
 * @author msauvee
 */
@RequestScoped
public class UserAccessService extends DbService implements Serializable {

    private static final long serialVersionUID = 3808331633166092605L;

    private static final int MAX_RESULT = 10000;

    @Transactional
    public void create(UserAccess access) {
        access.setUpdateTimestamp(new Date());
        getEntityManager().persist(access);
    }


    @Transactional
    public UserAccess update(UserAccess ua) {
        EntityManager em = getEntityManager();
        ua.setUpdateTimestamp(new Date());
        if (ua.getId() == null) {
            em.persist(ua);
            return ua;
        }
        ua = em.merge(ua);
        return ua;
    }

    @Transactional
    public UserAccess find(String accessID) {
        Query query = getEntityManager().createNamedQuery(UserAccess.FIND_BY_ACCESSID);
        query.setParameter("accessID", accessID);
        List list = query.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return (UserAccess) list.get(0);
    }

    @Transactional
    public UserAccess findFromExternalId(String applicationID, String externalId) {
        Query query = getEntityManager().createNamedQuery(UserAccess.FIND_BY_APP_AND_EXTID);
        query.setParameter("applicationID", applicationID);
        query.setParameter("externalID", externalId);
        List list = query.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return (UserAccess) list.get(0);
    }

    @Transactional
    public List<UserAccess> findFromTransactionId(String transactionID) {
        Query query = getEntityManager().createNamedQuery(UserAccess.FIND_BY_TRANSACTIONID);
        query.setParameter("transactionID", transactionID);
        query.setMaxResults(MAX_RESULT);
        return query.getResultList();
    }
}
