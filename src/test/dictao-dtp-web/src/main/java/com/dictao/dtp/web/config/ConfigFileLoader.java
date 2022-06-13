package com.dictao.dtp.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author msauvee
 */
public class ConfigFileLoader {
    
    private static final String REGEXP_URL = "^((https?://(www\\.)?)|www\\.)[.[^:]]+(:\\d+)?(/.*)?";

    private final File basePath;
    private final String baseURL;

    /**
     * @param path location of configuration file
     * @param baseURL allows computing absolute URLs
     */
    public ConfigFileLoader(String path, String baseURL) {
        this.baseURL = baseURL;
        basePath = new File(path);
        
    }

    /**
     * Return a FileInputStream pointing to a file knowing its relative path
     * @return
     */
    protected FileInputStream getFileStream(String path) throws FileNotFoundException {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (file.isAbsolute()) {
            return new FileInputStream(file);
        } 
		return new FileInputStream(new File(basePath, path));
    }
    
    /**
     * Return a FileInputStream pointing to a file knowing its relative path
     * @return
     */
    protected String getFilePath(String path) throws FileNotFoundException {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (file.isAbsolute()) {
            return file.getAbsolutePath();
        } 
        return (new File(basePath, path)).getAbsolutePath();
    }
    
    /**
     * computes the absolute URL from the given URL according to context path
     * @param configURL
     * @return
     */
    protected String computeFullURL (String configURL){

        if (! configURL.matches(REGEXP_URL)){
            return this.baseURL + configURL;
        }
        return configURL ;
        
    }

	public String getBaseURL() {
		
		return this.baseURL;
	}
}
