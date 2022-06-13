/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reunion.tirage.servlet.init;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reunion.tirage.persist.PersistenceManagerImpl;

/**
 *
 */
@Named
@ApplicationScoped
public class Application implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4209093585499931386L;

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    protected static final String EXTRA_PERSISTENCE_FILE_PATH = "jpa.persistence.extra.properties";
    protected static final String TIRAGE_PERSISTENCE_UNIT_NAME = "reunion";

    protected Application() {

        // TODO exception tester si le fichier de conf existe
    }

    // State
    private boolean started;

    public void start(String basePath) {

        try {
            LOG.info("Starting up Tirage Portal server");

            PersistenceManagerImpl.initFactory(TIRAGE_PERSISTENCE_UNIT_NAME, getBddProperties(basePath));

            LOG.info("Database schema integrity checked");

            started = true;
            LOG.info("Tirage portal server started");
        } catch (Exception ex) {

        }
    }

    public void stop() {
        LOG.info("Shutting down Cosi Portal server");
        started = false;
        LOG.info("Cosi portal server shutdown");
    }

    public boolean isStarted() {
        return started;
    }

    private Map<String, Object> getBddProperties(String basePath) {
        Map<String, Object> props = new HashMap<String, Object>();
        basePath = basePath.replace("\\", "/");
        props.put("eclipselink.logging.level", "FINEST");
        props.put("eclipselink.ddl-generation", "create-tables");
        props.put("eclipselink.ddl-generation.output-mode", "database");
        props.put("eclipselink.cache.shared.default", "false");
        props.put("eclipselink.session.customizer", "com.reunion.tirage.entity.SessionCustomizerImpl");

        return props;
    }
}