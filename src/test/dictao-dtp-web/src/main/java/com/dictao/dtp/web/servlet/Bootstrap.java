package com.dictao.dtp.web.servlet;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.persistence.ArchiveRepository;
import com.dictao.dtp.persistence.DocumentService;
import com.dictao.dtp.persistence.TreadLocalEntityManager;
import com.dictao.dtp.web.config.ConfigFileListener;
import com.dictao.dtp.web.config.DtpConfigReader;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

/**
 * Bootstrap
 * 
 * @author GLA
 */
public class Bootstrap implements ServletContextListener {
    /* **************************** CONSTANTS *************************** */
    private static Logger LOG = LoggerFactory.getLogger(Bootstrap.class.getName());
    public static final String BOOTSTRAP_FILE_PROPERTY = "dtp.fo.config.path";
    public static final String CHECK_TR_HANDLER_PARAMETER = "com.dictao.dtp.web.servlet.bootstrap.checkTransactionHandlerClass";
    public static final String CONTEXT_NAME = "context_name";

    /**
     * Option jvm pour renseigner le chemin du fichier de configuration log4j.
     */
    public static final String DTP_PORTAL_LOG4J_CONFIG_PATH_KEY = "dtp.webfo.log4j.config.path";

    private static final int MIN_THREAD_POOL_SIZE = 2;
    private static final int MAX_THREAD_POOL_SIZE = 20;

    /* **************************** ATTRIBUTES ************************** */
    private ConfigFileListener contextHandler = null;
    private BeanManager bmgr = null;
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(MIN_THREAD_POOL_SIZE,
            MAX_THREAD_POOL_SIZE,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(MAX_THREAD_POOL_SIZE));

    /* ************************** PUBLIC METHODS ************************ */

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Recherche de la configuration log4j
        String logPath = System
                .getProperty(DTP_PORTAL_LOG4J_CONFIG_PATH_KEY);
        File logFile = null;
        if (logPath != null) {
            logFile = new File(logPath);
            if (logFile.exists()) {
                // configuration de log4j
                PropertyConfigurator.configureAndWatch(logPath);
            }
        }
        LOG.info("Server start !");

        try {
            bmgr = (BeanManager) sce.getServletContext()
                    .getAttribute("org.jboss.weld.environment.servlet." + BeanManager.class.getName());
            LOG.info("Server context path : " + sce.getServletContext().getRealPath(""));
            if (bmgr != null) {
                // read DTP version from MANIFEST
                ServletContext ctx = sce.getServletContext();
                InputStream inputStream = ctx.getResourceAsStream("/META-INF/MANIFEST.MF");
                Manifest mf = new Manifest(inputStream);
                String dtpVersion = mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                String baseURL = ctx.getContextPath();
                // Get the right file which contains the configuration data
                String boostrapFileProperty = System.getProperty(BOOTSTRAP_FILE_PROPERTY);
                if (boostrapFileProperty != null) {
                    // recherche avec option jvm
                    File boostrapFile = new File(boostrapFileProperty);
                    if (boostrapFile != null && boostrapFile.exists()) {
                        LOG.info("Config file found = " + boostrapFile.getAbsolutePath());

                        boolean check = true;
                        if (sce.getServletContext().getInitParameter(CHECK_TR_HANDLER_PARAMETER) != null) {
                            check = Boolean.parseBoolean(sce.getServletContext().getInitParameter(
                                    CHECK_TR_HANDLER_PARAMETER));
                        }

                        final String contextName = baseURL.substring(baseURL.lastIndexOf("/") + 1);
                        LOG.info("%s configuration is being loaded...", contextName) ;;
                        // configuration of the core
                        ApplicationListConf appsConf = getReference(ApplicationListConf.class);
                        DocumentService documentRepository = getReference(DocumentService.class);
                        ArchiveRepository archiveRepository = getReference(ArchiveRepository.class);
                        DtpConfigReader contextReader = new DtpConfigReader(
                                dtpVersion, appsConf,
                                documentRepository,
                                archiveRepository, 
                                check,
                                contextName,
                                threadPoolExecutor);

                        contextHandler = new ConfigFileListener(contextReader,
                                System.getProperty(BOOTSTRAP_FILE_PROPERTY), baseURL);

                        contextHandler.start();

                    } else {
                        LOG.error("Boostrap file not found: %s", boostrapFileProperty);
                    }
                } else {
                    LOG.error("DTP config not found, whit key: "
                            + BOOTSTRAP_FILE_PROPERTY);
                    throw new EnvironmentException(
                            EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                            "DTP config not found, whit key: "
                                    + BOOTSTRAP_FILE_PROPERTY);
                }
            }
            // Create an unused EntityManager to force DB schema integrity check via SessionCustomizerImpl
            TreadLocalEntityManager.createEntityManager();
            LOG.info("Database schema integrity checked");
        } catch (EnvironmentException ex) {
            LOG.error(ex, "Internal error to start server");
        } catch (UserException ex) {
            LOG.error(ex, "Internal error to start server");
        } catch (Exception ex) {
            LOG.error(ex, "Internal error to start server");
        } finally {
            // release em
            TreadLocalEntityManager.removeCurrentEntityManager();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            LOG.info("Server stop !");
            contextHandler.stop();
            threadPoolExecutor.shutdown();
            LOG.info("Server stopped.");
        } catch (Exception ex) {
            LOG.error(ex, "Internal error to stop server");
        }
    }

    /* ********************* PROTECTED/PRIVATE METHODS ****************** */
    protected <T> T getReference(Class<?> clazz) {
        Bean<T> bean = (Bean<T>) bmgr.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = bmgr.createCreationalContext(bean);
        return (T) bmgr.getReference(bean, clazz, ctx);
    }


}