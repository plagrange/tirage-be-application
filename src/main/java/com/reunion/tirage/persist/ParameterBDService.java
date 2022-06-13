package com.reunion.tirage.persist;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import com.reunion.tirage.entity.TirageParameter;
import com.reunion.tirage.persist.exception.PersistenceManagerException;

/**
 *
 * @author PME
 */
@ApplicationScoped
public class ParameterBDService implements Serializable {

    private static final long serialVersionUID = -5560792789637838239L;
    private static ParameterBDService instance = null;
    public static ParameterBDService getInstance(){
    	
    	if(instance==null){
    		instance = new ParameterBDService();
    	}
    	
    	return instance;
    }
    public void persist(TirageParameter parameter) throws PersistenceManagerException {
        
        PersistenceManagerImpl.getNewPersistanceManager().create(parameter);
            
    }
    
    public void update(TirageParameter parameter) throws PersistenceManagerException {
        
        PersistenceManagerImpl.getNewPersistanceManager().update(parameter);
        
    }
    
    
    public TirageParameter find(String compagnie) throws PersistenceManagerException {
        
        Query query =  PersistenceManagerImpl.getNewPersistanceManager().createNamedQuery(TirageParameter.FIND_PARAM_BY_COMPAGNIE);
        query.setParameter("compagnie", compagnie);
        List<Object> find = PersistenceManagerImpl.getNewPersistanceManager().find(query, 5);
        
        return find.size()!=0? (TirageParameter) find.get(0) : null;
    }

     
    public void delete(String compagnie) throws PersistenceManagerException {
        
    	 Query query =  PersistenceManagerImpl.getNewPersistanceManager().createNamedQuery(TirageParameter.DELETE_PARAM_BY_COMPAGNIE);
         query.setParameter("compagnie", compagnie);
         PersistenceManagerImpl.getNewPersistanceManager().delete(query);
        
    }


   /**
    * Delete all Requests.
    * @return the number of deleted elements
    */
	public long deleteAll() {
	    Query query = PersistenceManagerImpl.getNewPersistanceManager().createNamedQuery(TirageParameter.DELETE_ALL);
		int executeUpdate = query.executeUpdate();
        return executeUpdate;
		
	}

}
