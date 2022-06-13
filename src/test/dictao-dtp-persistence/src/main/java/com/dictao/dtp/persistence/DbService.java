package com.dictao.dtp.persistence;


import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import javax.persistence.EntityManager;

/**
 *
 * @author msauvee, kchakali
 */
abstract class DbService {
    protected EntityManager getEntityManager() {
        return TreadLocalEntityManager.getCurrentEntityManager();
    }
    
    private transient static final Logger LOG = LoggerFactory.getLogger(DbService.class);

    public static final String PARAM = "param";
    public static final String ESCAPE_CHAR = "\\";

    public DbService() {
        // For POJO
    }
}
