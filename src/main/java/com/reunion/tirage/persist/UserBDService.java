package com.reunion.tirage.persist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import com.reunion.tirage.entity.User;
import com.reunion.tirage.persist.exception.PersistenceManagerException;

/**
 *
 * @author PME
 */
@ApplicationScoped
public class UserBDService implements Serializable {

    private static final long serialVersionUID = -5560792789637838239L;

    private static UserBDService instance = null;

    public static UserBDService getInstance() {

        if (instance == null) {
            instance = new UserBDService();
        }

        return instance;
    }

    public void persist(User user) throws PersistenceManagerException {

        PersistenceManagerImpl.getNewPersistanceManager().create(user);

    }

    public void update(User user) throws PersistenceManagerException {

        PersistenceManagerImpl.getNewPersistanceManager().update(user);

    }

    public User find(String email, String company) throws PersistenceManagerException {

        Query query = PersistenceManagerImpl.getNewPersistanceManager().createNamedQuery(User.FIND_USER_BY_EMAIL);
        query.setParameter("email", email);
        query.setParameter("compagnie", company);
        List<Object> find = PersistenceManagerImpl.getNewPersistanceManager().find(query, 5);

        return (User) find.get(0);
    }

    public void updateUser(String email, String company, boolean notificationSend) throws PersistenceManagerException {
        User user = find(email, company);
        user.setNotificationSend(notificationSend);
        update(user);
    }

    public List<User> getAllUser(String compagnie) throws PersistenceManagerException {
        List<User> result = new ArrayList<User>();

        Query query = PersistenceManagerImpl.getNewPersistanceManager().createNamedQuery(User.FIND_USER_BY_COMPAGNIE);
        query.setParameter("compagnie", compagnie);
        List<Object> find = PersistenceManagerImpl.getNewPersistanceManager().find(query, 50);
        for (Object obj : find) {
            result.add((User) obj);
        }

        return result;
    }

    public void delete(String email) throws PersistenceManagerException {

        Query query = PersistenceManagerImpl.getNewPersistanceManager().createNamedQuery(User.DELETE_USER_BY_EMAIL);
        query.setParameter("email", email);
        PersistenceManagerImpl.getNewPersistanceManager().delete(query);

    }

    /**
     * Delete all Requests.
     * 
     * @return the number of deleted elements
     */
    public long deleteAll() {
        Query query = PersistenceManagerImpl.getNewPersistanceManager().createNamedQuery(User.DELETE_ALL);
        int executeUpdate = query.executeUpdate();
        return executeUpdate;

    }

}
