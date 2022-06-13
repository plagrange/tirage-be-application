package com.dictao.dtp.web.cdi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.jboss.weld.environment.servlet.deployment.URLScanner;

import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

/**
 * <p>
 * To fix: https://issues.jboss.org/browse/WELD-834
 * </p>
 * 
 * @author JHG
 */
public class URLScannerEx extends URLScanner {

    private transient static final Logger LOG = LoggerFactory.getLogger(URLScannerEx.class);

    public URLScannerEx(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public void scanResources(String[] resources, Set<String> classes, Set<URL> urls) {
        Set<String> paths = new HashSet<String>();

        for (String resourceName : resources) {
            try {
                Enumeration<URL> urlEnum = getClassLoader().getResources(resourceName);

                while (urlEnum.hasMoreElements()) {
                    String urlPath = urlEnum.nextElement().getFile();

                    //Fix is here
                    urlPath = URLDecoder.decode(urlPath, Charset.defaultCharset().name());

                    if (urlPath.startsWith("file:")) {
                        urlPath = urlPath.substring(5);
                    }

                    if (urlPath.indexOf('!') > 0) {
                        urlPath = urlPath.substring(0, urlPath.indexOf('!'));
                    } else {
                        File dirOrArchive = new File(urlPath);

                        if ((resourceName != null) && (resourceName.lastIndexOf('/') > 0)) {
                            // for META-INF/beans.xml
                            dirOrArchive = dirOrArchive.getParentFile();
                        }

                        urlPath = dirOrArchive.getParent();
                    }
                    paths.add(urlPath);
                }
            } catch (IOException ioe) {
                LOG.warn(ioe, "Resource cannot be read: %s", resourceName);
            }
        }
        handle(paths, classes, urls);
    }
}
