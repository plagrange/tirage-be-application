package com.dictao.dtp.persistence;

import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class TreadLocalEntityManager {

    private static final Logger LOG = LoggerFactory.getLogger(TreadLocalEntityManager.class);
    private static final ThreadLocal<EntityManager> tlem =
            new ThreadLocal<EntityManager>() {

                @Override
                protected EntityManager initialValue() {
                    return null;
                }
            };
    private final static EntityManagerFactory emf ;
    static {
        Map props = new HashMap();
        String propFile = System.getProperty("jpa.persistence.extra.properties");
        if (propFile != null) {
            Properties propsff = new Properties();
            try {
                propsff.load(new FileInputStream(propFile));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            for (String name : propsff.stringPropertyNames()) {
                props.put(name, propsff.getProperty(name));
            }
        }
        emf = Persistence.createEntityManagerFactory("dtp", props);
    }

    public static void setCurrentEntityManager(EntityManager em) {
        if (tlem.get() != null) {
            // !!! DO NOT REMOVE THIS EXCEPTION !!!
            // Threads and persistence not well understood by developers so the usage pattern
            // should be as simple as possible:
            // - create and remove the thread-local EntityManager in the top-level method
            //   (use finally to remove it safely)
            // - gets it everywhere else.
            // If the code does not behave correctly (calls the create/remove where it should not),
            // the code should not fail silently. It must stop and raise an IllegalStateException.
            throw new IllegalStateException("failed to create a thread-local entity manager: another one already exists");
        }
        tlem.set(em);
    }

    public static void removeCurrentEntityManager() {
        EntityManager em = tlem.get();
        if (em != null) {
            tlem.remove();
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                LOG.debug("transaction still active while cleaning-up thread-local entity manager: abort it!");
                tx.rollback();
            }
            if (em.isOpen()) {
                LOG.debug("entity manager still open while cleaning-up thread-local entity manager: close it!");
                em.close();
            }
        } else
            LOG.warn("No entity manager found while removal.");
    }

    public static EntityManager getCurrentEntityManager() {
        if (tlem.get() == null) {
            // !!! DO NOT REMOVE THIS EXCEPTION !!!
            // The usage pattern must be as simple as possible. The caller should not be
            // able to test whether an EntityManager exists or not and allocate it.
            // Therefore, the code must throw an IllegalStateException exception if the caller
            // attempts to get a thread-local EntityManager that does not exists.
            throw new IllegalStateException("failed to get the thread-local entity manager: no entity manager has been initialized");
        }
        return tlem.get();
    }

    public static EntityManager createEntityManager() {
        EntityManager em;
        synchronized (emf) {
            em = emf.createEntityManager();
        }
        tlem.set(em);
        return em;
    }
}
