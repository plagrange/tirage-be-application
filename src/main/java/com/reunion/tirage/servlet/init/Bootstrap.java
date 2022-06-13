package com.reunion.tirage.servlet.init;

import java.net.URL;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Bootstrap implements ServletContextListener {

	/**
	 * Instance used for logging purpose.
	 */
	private static Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

//	@Inject
	private Application application;
	
	public Bootstrap() {

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		try {
			String basePath = sce.getServletContext().getRealPath(this.getClass().getName());
			basePath = basePath.substring(0, basePath.lastIndexOf("\\"));
			application = new Application();
			application.start(basePath);
			
		} catch (Exception ex) {
			LOG.error( "There's an error in the Cosi Portal Server start", ex);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		try {
		    if(application!=null){
		        application.stop();
		    }
		} catch (Exception ex) {
			LOG.error("There's an error in the cosi Portal Server start", ex);
		}
	}

}
