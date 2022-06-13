package com.reunion.tirage.persist;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;

import com.reunion.tirage.persist.exception.BeginTransactionException;
import com.reunion.tirage.persist.exception.CommitTransactionException;
import com.reunion.tirage.persist.exception.EntityAlreadyExistsException;
import com.reunion.tirage.persist.exception.InvalidEntityException;
import com.reunion.tirage.persist.exception.InvalidParametersException;
import com.reunion.tirage.persist.exception.PersistenceManagerException;

public class PersistenceManagerImpl implements PersistenceManager
{
    
    // ***********************************************************************
    // * CONSTRUCTEUR.
    // ***********************************************************************
    /**
     * Constructeur de la classe.
     */
    private PersistenceManagerImpl()
    {
    	initFactory("reunion");
        _persistManager = _persistFactory.createEntityManager();
    }
    
    // ***********************************************************************
    // * METHODE PUBLIC
    // ***********************************************************************
    /**
     * création de la factory 
     */
    public static synchronized void initFactory(String persistUnitName, Map<String, Object> configOverrides) {
        LOG.entering(PersistenceManagerImpl.class.getName(), "initFactory");

        if (_persistFactory != null) {
            return;
        }
        
        if (configOverrides == null) {
        	_persistFactory = Persistence.createEntityManagerFactory(persistUnitName);
        } else {
        	_persistFactory = Persistence.createEntityManagerFactory(persistUnitName, configOverrides);
        }

        LOG.exiting(PersistenceManagerImpl.class.getName(),PersistenceManagerImpl.class.getName(), "initFactory");
        assert _persistFactory != null;
    }
    
    /**
     * création de la factory 
     */
    public static synchronized void initFactory(String persistUnitName, Properties config) {
        LOG.entering(PersistenceManagerImpl.class.getName(), "initFactory");

        if (_persistFactory != null) {
            return;
        }
        
        if (config == null) {
            _persistFactory = Persistence.createEntityManagerFactory(persistUnitName);
        } else {
            _persistFactory = Persistence.createEntityManagerFactory(persistUnitName, config);
        }

        LOG.exiting(PersistenceManagerImpl.class.getName(),"initFactory");
        assert _persistFactory != null;
    }
    
    /**
     * création de la factory 
     */
    public static synchronized void initFactory(String persistUnitName) {
        LOG.entering(PersistenceManagerImpl.class.getName(),"initFactory");

        if (_persistFactory != null) {
            return;
        }
        
        _persistFactory = Persistence.createEntityManagerFactory(persistUnitName);

        LOG.exiting(PersistenceManagerImpl.class.getName(),"initFactory");
        assert _persistFactory != null;
    }
    
    /**
     * Obtenir un nouveau Gestionnaire de persistance.
     * 
     * @return instance de la classe
     */
    public static PersistenceManager getNewPersistanceManager()
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"getNewPersistanceManager");
        PersistenceManager instance = null;
        synchronized (_mutex)
        {
            instance = new PersistenceManagerImpl();
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"getNewPersistanceManager");
        return instance;
    }

    public void create(Object... entities) throws PersistenceManagerException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"create");
        if (entities != null && entities.length > 0)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                for (Object entity : entities)
                {
                    LOG.warning("Persist Entity:" + entity);
                    if (entity != null)
                    {
                        getCurrentEntityManager().persist(entity);
                    }
                    else
                    {
                        // Annulation des opération dans la transaction en cours
                        rollBack();
                        // Le paramètre de la méthode est à null ou vide
                        throw new InvalidParametersException(EXPT_INVALID_PARAM);
                    }
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (EntityExistsException ex)
            {
                // EntityExistsException - if the entity already exists. (The
                // EntityExistsException may be thrown when the persist
                // operation is invoked, or the EntityExistsException or another
                // PersistenceException may be thrown at flush or commit time.)
                // Annulation des opérations dans la transaction en cours
                rollBack();
                throw new EntityAlreadyExistsException(
                        EXPT_ENTITY_ALREADY_EXIST, ex);
            }
            catch (IllegalStateException ex)
            {
                // IllegalStateException - if this EntityManager has been closed
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if not an entity
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new InvalidEntityException(EXPT_ENTITY_NOT_VALID, ex);
            }
            catch (TransactionRequiredException ex)
            {
                // TransactionRequiredException - if invoked on a
                // container-managed entity manager of type
                // PersistenceContextType.TRANSACTION and there is no
                // transaction.
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_NO_CURRENT_TRANSACTION, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                // Annulation des opération dans la transaction en cours
                rollBack();
                // FIX BUG POSTGRES
                // throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                // ex);
                throw new EntityAlreadyExistsException(
                        EXPT_ENTITY_ALREADY_EXIST, ex);
            }
            catch (PersistenceException ex)
            {
                // Erreur de persistance (méthode persist())
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(EXPT_PERSISTENCE_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"create");
    }
    
    public void delete(Object... entities) throws PersistenceManagerException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"delete");
        if (entities != null && entities.length > 0)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                for (Object entity : entities)
                {
                    if (entity != null)
                    {
                        // Effacer l'entité de la base
                        getCurrentEntityManager().remove(entity);
                    }
                    else
                    {
                        // Annulation des opération dans la transaction en cours
                        rollBack();
                        // Le paramètre de la méthode est à null ou vide
                        throw new InvalidParametersException(EXPT_INVALID_PARAM);
                    }
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // if this EntityManager has been closed.
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if not an entity
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new InvalidEntityException(EXPT_ENTITY_NOT_VALID, ex);
            }
            catch (TransactionRequiredException ex)
            {
                // if invoked on a container-managed entity manager of type
                // PersistenceContextType.TRANSACTION and there is no
                // transaction.
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_NO_CURRENT_TRANSACTION, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (PersistenceException ex)
            {
                // Erreur de persistance (méthode remove())
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(EXPT_PERSISTENCE_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"delete");
    }
    
    
    public void delete(Query query) throws PersistenceManagerException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"delete");
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                        // Effacer l'entité de la base
                if (query != null)
                {
                    setNombreMaxResult(query, 10);
                    query.getResultList();
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // if this EntityManager has been closed.
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if not an entity
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new InvalidEntityException(EXPT_ENTITY_NOT_VALID, ex);
            }
            catch (TransactionRequiredException ex)
            {
                // if invoked on a container-managed entity manager of type
                // PersistenceContextType.TRANSACTION and there is no
                // transaction.
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_NO_CURRENT_TRANSACTION, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (PersistenceException ex)
            {
                // Erreur de persistance (méthode remove())
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(EXPT_PERSISTENCE_ERROR,
                        ex);
            }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"delete");
    }
    
    public void update(Object... entities) throws PersistenceManagerException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"update");
        if (entities != null && entities.length > 0)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                for (Object entity : entities)
                {
                    if (entity != null)
                    {
                        // Mettre � jour l'entité
                        getCurrentEntityManager().merge(entity);
                    }
                    else
                    {
                        // Annulation des opération dans la transaction en cours
                        rollBack();
                        // Le paramètre de la méthode est à null ou vide
                        throw new InvalidParametersException(EXPT_INVALID_PARAM);
                    }
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // if this EntityManager has been closed.
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if not an entity
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new InvalidEntityException(EXPT_ENTITY_NOT_VALID, ex);
            }
            catch (EntityNotFoundException ex)
            {
                // if the entity no longer exists in the database.
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(EXPT_ENTITY_NOT_FOUND, ex);
            }
            catch (TransactionRequiredException ex)
            {
                // if invoked on a container-managed entity manager of type
                // PersistenceContextType.TRANSACTION and there is no
                // transaction.
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_NO_CURRENT_TRANSACTION, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                // Annulation des opération dans la transaction en cours
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"update");
    }
    
    public int updateQuery(Query query) throws PersistenceManagerException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"updateQuery");
       int returnValue = 0;
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
//                returnValue = query.getFirstResult();
                returnValue = query.executeUpdate();
//                returnValue = query.getMaxResults();
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"findByNamedQuery");
        return returnValue;
    }
    
    
    public List<Object> find(String queryString, Integer nombreMaxResultat)
            throws PersistenceManagerException
    {
        LOG.entering(this.getClass().getName(), "find : Query=" + queryString);
        List<Object> returnValue = null;
        if (queryString != null)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                Query query = getCurrentEntityManager()
                        .createQuery(queryString);
                if (query != null)
                {
                    setNombreMaxResult(query, nombreMaxResultat);
                    returnValue = (List<Object>) query.getResultList();
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"find");
        return returnValue;
    }
    
    public List<Object> find(String queryString, Object value,
            Integer nombreMaxResultat) throws PersistenceManagerException
    {
        LOG.entering(this.getClass().getName(), " find : Query=" + queryString);
        List returnValue = null;
        if (queryString != null)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                Query query = getCurrentEntityManager()
                        .createQuery(queryString);
                if (query != null)
                {
                    query.setParameter(1, value);
                    setNombreMaxResult(query, nombreMaxResultat);
                    returnValue = query.getResultList();
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"find");
        return returnValue;
    }
    
    public List<Object> find(String queryString, String name, Object value,
            Integer nombreMaxResultat) throws PersistenceManagerException
    {
        LOG.entering(this.getClass().getName(), "find : Query=" + queryString);
        List returnValue = null;
        if (queryString != null)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                Query query = getCurrentEntityManager()
                        .createQuery(queryString);
                if (query != null)
                {
                    query.setParameter(name, value);
                    setNombreMaxResult(query, nombreMaxResultat);
                    returnValue = query.getResultList();
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"find");
        return returnValue;
    }
    
    public List<Object> findNamedParameter(String queryString,
            Integer nombreMaxResultat, NamedQueryParameter... args)
            throws PersistenceManagerException
    {
        
        LOG.entering(this.getClass().getName(), "Query=" + queryString);
        List returnValue = null;
        if (queryString != null)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                Query query = getCurrentEntityManager()
                        .createQuery(queryString);
                if (query != null)
                {
                    fillQueryWithQueryNamedParameter(query, args);
                    setNombreMaxResult(query, nombreMaxResultat);
                    returnValue = query.getResultList();
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"find");
        return returnValue;
    }
    
    public List<Object> findAll(Class type, Integer nombreMaxResultat)
            throws PersistenceManagerException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"findAll");
        List returnValue = null;
        if (type != null)
        {
            String queryString = "select o from " + type.getSimpleName() + " o";
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                Query query = getCurrentEntityManager()
                        .createQuery(queryString);
                if (query != null)
                {
                    setNombreMaxResult(query, nombreMaxResultat);
                    returnValue = query.getResultList();
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"find");
        return returnValue;
    }
    
    public Object findById(Class type, Long id)
            throws PersistenceManagerException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"findById");
        Object returnValue = null;
        if (type != null)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                returnValue = getCurrentEntityManager().find(type, id);
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"find");
        return returnValue;
    }
    
    public List<Object> findByNamedQuery(String queryName,
            Integer nombreMaxResultat) throws PersistenceManagerException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"findByNamedQuery");
        List<Object> returnValue = null;
        if (queryName != null)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                Query query = getCurrentEntityManager().createNamedQuery(
                        queryName);
                
                if (query != null)
                {
                    setNombreMaxResult(query, nombreMaxResultat);
                    returnValue = query.getResultList();
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"findByNamedQuery");
        return returnValue;
    }
    
    
    public Object findInstanceByNamedQuery(String queryName)
            throws PersistenceManagerException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"findInstanceByNamedQuery");
        Object returnValue = null;
        if (queryName != null)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
                Query query = getCurrentEntityManager().createNamedQuery(
                        queryName);
                
                if (query != null)
                {
                    returnValue = query.getSingleResult();
                }
                // Terminer l'ensemble des opérations de la transaction en
                // cours
                commit();
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"findInstanceByNamedQuery");
        return returnValue;
    }
    
    @Override
    public List<Object> find(Query query, int nombreMaxResultat)throws PersistenceManagerException {
       
        LOG.entering(PersistenceManagerImpl.class.getName(),"find");
        List<Object> returnValue = null;
        if (query != null)
        {
            try
            {
                // Débuter une transaction sur la base de donnée
                beginTransaction();
         
            setNombreMaxResult(query, nombreMaxResultat);
            returnValue = (List<Object>) query.getResultList();
            // Terminer l'ensemble des opérations de la transaction en
            // cours
            commit();
        
            }
            catch (IllegalStateException ex)
            {
                // createQuery(qlString) - if this EntityManager has been
                // closed.
                // or
                // getResultList() - if called for a Java Persistence
                // query language UPDATE or DELETE statement
                rollBack();
                throw new PersistenceManagerException(
                        EXPT_ENTITY_MANAGER_CLOSE, ex);
            }
            catch (IllegalArgumentException ex)
            {
                // IllegalArgumentException - if query string is not valid
                rollBack();
                throw new PersistenceManagerException(EXPT_INVALID_QUERY, ex);
            }
            catch (BeginTransactionException ex)
            {
                // Erreur lors de l'initialisation de la transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
            catch (CommitTransactionException ex)
            {
                // Erreur lors de la validation des actions en cours dans la
                // transaction
                rollBack();
                throw new PersistenceManagerException(EXPT_TRANSACTION_ERROR,
                        ex);
            }
        }
        else
        {
            // Le paramètre de la méthode est à null ou vide
            throw new InvalidParametersException(EXPT_INVALID_PARAM);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"find");
        return returnValue;
    }
    
    /**
     * Fermer la session sur le PersistenceManager.closeSession()
     */
    public void closeSession()
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"closeSession");
        try
        {
            flush();
            rollBack();
            if (getCurrentEntityManager() != null)
            {
                getCurrentEntityManager().close();
            }
            _persistManager = null;
        }
        catch (IllegalStateException ex)
        {
            // close() - if the EntityManager is container-managed or has been
            // already closed..
            LOG.warning(ex.getMessage() + " " + WARN_SESSION_ALREADY_CLOSE);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"closeSession");
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        closeSession();
        super.finalize();
    }
    
    // ***********************************************************************
    // * METHODE PRIVEE
    // ***********************************************************************
    /**
     * Commencer une transaction.
     * 
     * @exception BeginTransactionException
     *                l'exception BeginTransactionException est levée si une
     *                erreur intervient pendant l'ouverture de la transaction.
     */
    private void beginTransaction() throws BeginTransactionException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"beginTransaction");
        try
        {
            if (getCurrentEntityManager().getTransaction() != null
                    && getCurrentEntityManager().getTransaction().isActive())
            {
                LOG.warning(WARN_TRANSACTION_NOT_CLOSE);
                // Annulation des opérations dans la transaction en cours
                getCurrentEntityManager().getTransaction().rollback();
            }
            getCurrentEntityManager().getTransaction().begin();
        }
        catch (IllegalStateException ex)
        {
            // begin() - if isActive() is true.
            // or
            // getTransaction()- if invoked on a JTA EntityManager.
            throw new BeginTransactionException(EXPT_TRANSACTION_ALREADY_EXIST,
                    ex);
        }
        catch (PersistenceException ex)
        {
            // isActive() - if an unexpected error condition is encountered.
            throw new BeginTransactionException(EXPT_PERSISTENCE_ERROR, ex);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"beginTransaction");
    }
    
    /**
     * Valider la transaction.
     * 
     * @exception CommitTransactionException
     *                L'exception CommitTransactionException est levée si une
     *                erreur intervient pendant l'enregistrement des données en
     *                bases.
     */
    private void commit() throws CommitTransactionException
    {
        LOG.entering(PersistenceManagerImpl.class.getName(),"commit");
        try
        {
            if (getCurrentEntityManager().getTransaction() == null)
            {
                throw new CommitTransactionException(
                        EXPT_NO_CURRENT_TRANSACTION);
            }
            // Terminer l'ensemble des opérations de la transaction en
            // cours
            getCurrentEntityManager().getTransaction().commit();
            flush();
            // getCurrentEntityManager().close();
        }
        catch (IllegalStateException ex)
        {
            // COMMIT
            // - if the commit fails.
            // or
            // getTransaction()- if invoked on a JTA EntityManager.
            throw new CommitTransactionException(EXPT_COMMIT_ERROR, ex);
        }
        catch (RollbackException ex)
        {
            // COMMIT
            // - if isActive() is false.
            throw new CommitTransactionException(EXPT_COMMIT_ERROR, ex);
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"commit");
    }
    
    /**
     * Annuler la transaction en cours.
     * 
     */
    private void rollBack()
    {
        LOG.entering(PersistenceManagerImpl.class.getName(), "rollBack");
        try
        {
            if (getCurrentEntityManager().getTransaction() != null)
            {
                getCurrentEntityManager().getTransaction().rollback();
                // getCurrentEntityManager().close();
            }
        }
        catch (IllegalStateException ex)
        {
            // if transaction isActive() is false.
            // or on getTransaction()- if invoked on a JTA EntityManager.
        }
        catch (PersistenceException ex)
        {
            // if an unexpected error condition is encountered.
        }
        LOG.exiting(PersistenceManagerImpl.class.getName(),"rollBack");
    }
    
    /**
     * Flush sur la session.
     */
    private void flush()
    {
        try
        {
            getCurrentEntityManager().flush();
        }
        catch (IllegalStateException ex)
        {
            // if this EntityManager has been closed
            // or
            // getTransaction()- if invoked on a JTA EntityManager.
        }
        catch (TransactionRequiredException ex)
        {
            // if there is no transaction
        }
        catch (PersistenceException ex)
        {
            // if the flush fails
        }
    }
    
    /**
     * Obtenir l'EntityManager courant.
     * 
     * @return EntityManager courant.
     */
    private EntityManager getCurrentEntityManager()
    {
        if (_persistManager == null || !_persistManager.isOpen())
        {
            _persistManager = _persistFactory.createEntityManager();
        }
        return _persistManager;
    }
    
    
    @Override
    public Query createNamedQuery(String findRequestById) {
        return getCurrentEntityManager().createNamedQuery(findRequestById);
    }
    
    /**
     * Subsituer les paramètres se trouvant dans une query par les valeurs
     * contenues dans les paramètres nommés.
     * 
     * @param q
     *            contient la requete JPQL
     * @param args
     *            ensemble de paramètre "nommé" contennat une association entre
     *            un nom et une valeur.
     */
    private void fillQueryWithQueryNamedParameter(Query q,
            NamedQueryParameter... args)
    {
        if (args != null && args.length > 0)
        {
            for (NamedQueryParameter queryParam : args)
            {
                
                q.setParameter(queryParam.getParameterName(), queryParam
                        .getValue());
            }
        }
    }
    
    /**
     * Indiquer le nombre maximum de resultat attendu par l'execution de la
     * requete Query.
     * 
     * @param query
     *            contient la requete JPQL
     * @param nombreMaxResultat
     *            Nombre max de resultat dans la liste
     */
    private void setNombreMaxResult(Query query, Integer nombreMaxResultat)
    {
        if (query != null && nombreMaxResultat != null
                && nombreMaxResultat.intValue() >= 0)
        {
            query.setMaxResults(nombreMaxResultat.intValue());
        }
    }
    
    // ***********************************************************************
    // * CONSTANTES
    // ***********************************************************************
    
    /**
     * Constante indiquant l'identifiant de la configuration de gestionnaire de
     * persistence (cf. fichier /META-INF/persistence.xml)
     */
    private static final String TYPE_FACTORY_JPA = "hibernate";
    
    /**
     * Constante contenant un message d'erreur utilisé lorsque un paramètre null
     * ou invalide est passé à une méthode du PersistenceManager qui ne gàre pas
     * ce type de valeur.
     */
    private static final String EXPT_INVALID_PARAM = "Invalid parameters for this function";
    /**
     * Constante contenant un message d'erreur utilisé lorsque l'entité est déjà
     * persistante en base et ne peux donc pas être créé.
     */
    private static final String EXPT_ENTITY_ALREADY_EXIST = "Entity already exist in database";
    /**
     * Constante contenant un message d'erreur utilisé lorsque le manager de
     * persistence est fermé.
     */
    private static final String EXPT_ENTITY_MANAGER_CLOSE = "Entity manager is close";
    /**
     * Constante contenant un message d'erreur utilisé lorsque l'entité devant
     * être créé ou modifié ne respecte pas les règles de persistance définit
     * dans la classe de l'entité ou quand l'objet ne correspond pas à une des
     * entités définies.
     */
    private static final String EXPT_ENTITY_NOT_VALID = "Entity is not a valid entity for this database";
    
    /**
     * Constante contenant un message d'erreur utilisé lorsque l'entité devant
     * être créé ou modifié ne respecte pas les règles de persistance définit
     * dans la classe de l'entité ou quand l'objet ne correspond pas � une des
     * entités définies.
     */
    private static final String EXPT_ENTITY_NOT_FOUND = "Entity not found in this this database";
    
    /**
     * Constante contenant un message d'erreur utilisé lorsque qu'aucune
     * transaction n'a été ouverte.
     */
    private static final String EXPT_NO_CURRENT_TRANSACTION = "Can't find the current transaction";
    /**
     * Constante contenant un message d'erreur utilisé lorsqu'une transaction
     * est en cours et que l'on désire en ouvrir une autre.
     */
    private static final String EXPT_TRANSACTION_ALREADY_EXIST = "The current transaction is not close";
    
    /**
     * Constante contenant un message d'erreur utilisé pour identifier un
     * problème général de persistence.
     */
    private static final String EXPT_PERSISTENCE_ERROR = "Persistence problem: Can't do request action";
    /**
     * Constante contenant un message d'erreur utilisé lorsque la requete n'est
     * pas valide.
     */
    private static final String EXPT_INVALID_QUERY = "Invalid query for this database";
    
    /**
     * Constante contenant un message d'erreur utilisé lorsqu'une exception
     * intervient sur une transaction.
     */
    private static final String EXPT_TRANSACTION_ERROR = "Error in the current transaction";
    
    /**
     * Constante contenant un message d'erreur utilisé lorsque ...
     */
    private static final String EXPT_COMMIT_ERROR = "Error in commit transaction";
    
    /**
     * Constante contenant un message d'information utilisé pour indiquer qu'une
     * nouvelle transaction va être ouverte mais une transaction est
     * actuellement en cours.
     */
    private static final String WARN_TRANSACTION_NOT_CLOSE = "Begin a new Transaction but there is one not close";
    
    /**
     * Constante contenant un message d'information utilisé pour indiquer ...
     */
    private static final String WARN_SESSION_ALREADY_CLOSE = "Can't close session because the session is already close";
    // ***********************************************************************
    // * ATTRIBUT DE CLASSE
    // ***********************************************************************
    /** Attribut contenant le gestionnaire des logs de la classe. */
    private static Logger LOG = Logger.getLogger(PersistenceManagerImpl.class.getName());
    /**
     * Attribut contenant une reférence vers l'objet servant � la
     * synchronisation de de la création de l'instance unique.
     */
    private static Object _mutex = new Object();
    /** Attribut contenant une reférence vers la factory Java Persistance API. */
    private static EntityManagerFactory _persistFactory = null;
    //Persistence
      //      .createEntityManagerFactory(TYPE_FACTORY_JPA);
    /**
     * Attribut contenant une reférence vers le manager de persistance de la lib
     * Java Persistance API.
     */
    private EntityManager _persistManager = null;
   
}
