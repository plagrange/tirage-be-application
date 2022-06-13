/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dictao.dtp.web.proofexplorer;

import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.Serializable;
import javax.inject.Singleton;

/**
 * This class serves as a storage base for ecm zip configuration
 */
@Singleton
public class SessionEcmZipConfiguration implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEcmZipConfiguration.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + SessionEcmZipConfiguration.class.getName());

    int maxZipSize;
    int maxZipEntrySize;
    int maxZipTotalContentSize;
    String tempStorageFolder;

    public SessionEcmZipConfiguration() {
    }

    public int getMaxZipSize() {
        return maxZipSize;
    }

    public void setMaxZipSize(int value) {
        maxZipSize = value;
    }

    public int getMaxZipEntrySize() {
        return maxZipEntrySize;
    }

    public void setMaxZipEntrySize(int value) {
        maxZipEntrySize = value;
    }

      public int getMaxZipTotalContentSize() {
        return maxZipTotalContentSize;
    }

    public void setMaxZipTotalContentSize(int value) {
        maxZipTotalContentSize = value;
    }

    public String getTempStorageFolder() {
        return tempStorageFolder;
    }

    public void setTempStorageFolder(String value) {
        tempStorageFolder = value;
    }
}
