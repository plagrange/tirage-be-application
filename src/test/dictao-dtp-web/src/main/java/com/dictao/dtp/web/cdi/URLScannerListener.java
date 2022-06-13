package com.dictao.dtp.web.cdi;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.weld.environment.servlet.deployment.URLScanner;
import org.jboss.weld.environment.servlet.util.Reflections;

/**
 * <p>To fix: https://issues.jboss.org/browse/WELD-834</p>
 * @author JHG
 *
 */
public class URLScannerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final URLScanner urlScanner = new URLScannerEx(Reflections.getClassLoader());
        sce.getServletContext().setAttribute(URLScanner.class.getName(), urlScanner);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //No need to remove instance,
        //here Weld is doing it for us
    }
}
