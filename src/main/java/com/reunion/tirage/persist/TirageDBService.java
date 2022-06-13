package com.reunion.tirage.persist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import com.reunion.tirage.data.UserTirageResponse;
import com.reunion.tirage.entity.Tirage;
import com.reunion.tirage.entity.User;
import com.reunion.tirage.persist.exception.PersistenceManagerException;

/**
 *
 * @author PME
 */
@ApplicationScoped
public class TirageDBService implements Serializable {

	private static final Logger LOG = Logger.getLogger(TirageDBService.class
			.getName());

	private static final long serialVersionUID = -5560792789637838239L;

	private static TirageDBService instance = null;

	public static TirageDBService getInstance() {

		if (instance == null) {
			instance = new TirageDBService();
		}

		return instance;
	}

	public void persist(Tirage request) throws PersistenceManagerException {
		LOG.entering(TirageDBService.class.getName(), "persist");

		PersistenceManagerImpl.getNewPersistanceManager().create(request);

		LOG.exiting(TirageDBService.class.getName(), "persist");
	}

	public void update(Tirage request) throws PersistenceManagerException {

		LOG.entering(TirageDBService.class.getName(), "update");

		PersistenceManagerImpl.getNewPersistanceManager().update(request);

		LOG.exiting(TirageDBService.class.getName(), "update");
	}

	public Tirage findByEmailAndConpany(String email, String company)
			throws PersistenceManagerException {
		LOG.entering(TirageDBService.class.getName(), "findByEmailAndConpany");
		
		Tirage result = null;
		Query query = PersistenceManagerImpl.getNewPersistanceManager()
				.createNamedQuery(Tirage.FIND_TIRAGE_BY_EMAIL_AND_COMPANY);
		query.setParameter("email", email);
		query.setParameter("compagnie", company);
		List<Object> find = PersistenceManagerImpl.getNewPersistanceManager()
				.find(query, 5);

		if (find != null && find.size() != 0) {
			result = (Tirage) find.get(0);
		}
		
		LOG.exiting(TirageDBService.class.getName(), "findByEmailAndConpany");
		return result;
	}

	public Tirage delete(String tirageId) throws PersistenceManagerException {
		LOG.entering(TirageDBService.class.getName(), "delete");

		Tirage tirage = new Tirage("test", 1, null);
		PersistenceManagerImpl.getNewPersistanceManager().delete(tirage);

		LOG.exiting(TirageDBService.class.getName(), "delete");

		return tirage;
	}

	/**
	 * Delete all Requests.
	 * 
	 * @return the number of deleted elements
	 */
	public long deleteAllForCompany(String company) {
		LOG.entering(TirageDBService.class.getName(), "deleteAll");
		
		Query query = PersistenceManagerImpl.getNewPersistanceManager()
				.createNamedQuery(Tirage.DELETE_ALL);
		query.setParameter("compagnie", company);
		int executeUpdate = query.executeUpdate();
		
		LOG.exiting(TirageDBService.class.getName(), "deleteAll");
		return executeUpdate;

	}

	public long deleteByTId(String tId) {
		LOG.entering(TirageDBService.class.getName(), "deleteByTId");
		
		Query query = PersistenceManagerImpl.getNewPersistanceManager()
				.createNamedQuery(Tirage.DELETE_BY_TID);
		query.setParameter("tID", tId);
		
		LOG.exiting(TirageDBService.class.getName(), "deleteByTId");
		return query.executeUpdate();
	}

	public List<UserTirageResponse> getTirageResult(String company)
			throws PersistenceManagerException {
		LOG.entering(TirageDBService.class.getName(), "getTirageResult");
		
		Query query = PersistenceManagerImpl.getNewPersistanceManager()
				.createNamedQuery(Tirage.SELECT_ALL);
		query.setParameter("compagnie", company);
		List<Object> resultList = PersistenceManagerImpl
				.getNewPersistanceManager().find(query, 50);

		List<UserTirageResponse> results = new ArrayList<UserTirageResponse>();
		for (Object objet : resultList) {
			Tirage tirage = (Tirage) objet;
			UserTirageResponse resultDto = new UserTirageResponse(tirage.getEmail(),
					tirage.getNumeroTire(), company);
			results.add(resultDto);
		}
		
		LOG.exiting(TirageDBService.class.getName(), "getTirageResult");
		return results;
	}

	public boolean authenticateUser(String email, String criteria,
			String company) throws PersistenceManagerException {
		LOG.entering(TirageDBService.class.getName(), "authenticateUser");
		
		boolean autenticate = false;
		Query query = PersistenceManagerImpl.getNewPersistanceManager()
				.createNamedQuery(User.FIND_USER_BY_EMAIL);
		query.setParameter("email", email);
		query.setParameter("compagnie", company);

		List<Object> userList = PersistenceManagerImpl
				.getNewPersistanceManager().find(query, 1);
		User user = null;
		if (userList != null && userList.size() != 0) {
			user = (User) userList.get(0);
			if (user.getCodeSecurite().equals(criteria)) {
				autenticate = true;
			}
		}
		
		LOG.exiting(TirageDBService.class.getName(), "authenticateUser");
		return autenticate;
	}
}
