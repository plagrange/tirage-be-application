package com.dictao.dtp.web.config;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileMonitor;

public class ConfigFileListener implements FileListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigFileListener.class);
    private ConfigReader reader = null;
    private ConfigFileLoader loader = null;
    private String configFilePath = null;
    private DefaultFileMonitor fileMonitor = null;

    /**
     * @param reader specific configuration reader
     * @param configFilePath location of configuration file
     * @param baseURL allows computing absolute URLs
     */
    public ConfigFileListener(ConfigReader reader, String configFilePath, String baseUrl) {
        this.reader = reader;
        this.configFilePath = configFilePath;
        this.loader = new ConfigFileLoader(new File(configFilePath).getAbsoluteFile().getParent(),baseUrl);
    }

    public void start() {
        try {
            // Stop a monitor for the last restart if not stop
            if (fileMonitor != null) {
                fileMonitor.stop();
            }
            // initialize monitor
            try {
                FileSystemManager fsManager = VFS.getManager();
                FileObject listendir = fsManager.resolveFile(configFilePath);
                fileMonitor = new DefaultFileMonitor(this);
                fileMonitor.setRecursive(false);
                fileMonitor.addFile(listendir);
            } catch (Exception ex) {
                LOG.error(ex);
                throw new EnvironmentException(ex, EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                        "unable to monitor config changes for '%s'", configFilePath);
            }
            // Start watching this file
            fileMonitor.start();
            // Load the data in the file
            load();
            // Start the monitoring of the config. file
        } catch (EnvironmentException ex) {
            LOG.error(ex, "ConfigFileListener error");
            throw new EnvironmentException(ex, EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "ConfigFileListener start error");
        }
    }

    public void stop() {
        if (fileMonitor != null) {
            fileMonitor.stop();
        }
    }

    private void load() {
        FileInputStream fileInputStream  = null;
        try {
            // fileInputStream is used to close the stream, don't in-line the
            // new stream in load()
            fileInputStream = new FileInputStream(configFilePath);
            reader.load(fileInputStream, loader);
        } catch (Exception ex) {
            LOG.error(ex);
            throw new EnvironmentException(ex, EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "ConfigFileListener load error");
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception ex) {
                    LOG.error(ex);
                }
            }
        }
    }

    @Override
    public void fileChanged(FileChangeEvent event) throws Exception {
        load();
    }

    @Override
    public void fileCreated(FileChangeEvent event) throws Exception {
        load();
    }

    @Override
    public void fileDeleted(FileChangeEvent arg0) throws Exception {
        load();
    }
}
