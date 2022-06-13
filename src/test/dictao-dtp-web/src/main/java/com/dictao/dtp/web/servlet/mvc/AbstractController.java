package com.dictao.dtp.web.servlet.mvc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import com.dictao.dtp.core.ResourceBundleHandler;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.EnvironmentException.Code;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.UserAccess;

public abstract class AbstractController<T extends TransactionHandler> implements Controller<T> {
    
    protected T txHandler;

    @Override
    public void setTransactionHandler(T txHandler) {
        this.txHandler = txHandler;
    }

    protected String addParameterToUrl(String url, String name, String value) {
        int qpos = url.indexOf('?');
        int hpos = url.indexOf('#');
        char sep = qpos == -1 ? '?' : '&';
        try {
            name = URLEncoder.encode(name, "UTF-8");
            value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new EnvironmentException(e, Code.DTP_ENV_INTERNAL_ERROR, "Enable to encode url parameter name [%s], value [%s]", name, value);
        }
        String seg = sep + name + '=' + value;
        String res = hpos == -1 ? url + seg : url.substring(0, hpos) + seg
                + url.substring(hpos);
        return res;
    }

    protected String getDocUrl(String ecm, String tid, String aid, String filename) {
        return "ui/document/" + ecm + "/" + tid + "/" + filename + "?aid=" + aid;
    }

    protected String getDefaultJspName(String path) {
        String fileName;
        if (path.length() > 1) {
            fileName = path.replace("/", "-");
        } else {
            fileName = "";
        }
        String workflow = txHandler.getDatabaseUserAccess().getUIInfo().getUi();
        String type = txHandler.getClass().getSimpleName().toLowerCase();
        return type + "-" + workflow + fileName;
    }

    protected String getBackUrlOK() {
        String backUrl = null;
        UserAccess ua = txHandler.getDatabaseUserAccess();
        if(ua != null && ua.getUIInfo() !=null && ua.getUIInfo().getBackUrl() != null) {
            backUrl = txHandler.getDatabaseUserAccess().getUIInfo().getBackUrl();
        } else {
            throw new UserException(UserException.Code.DTP_USER_ILLEGAL_STATE,
                    "Unable to redirect to back url from UserAccess/UIInfo");
        }
        return addParameterToUrl(backUrl, "status", "OK");
    }
    
    protected String getBackUrl(String status) {
        String backUrl = null;
        UserAccess ua = txHandler.getDatabaseUserAccess();
        if(ua != null && ua.getUIInfo() !=null && ua.getUIInfo().getBackUrl() != null) {
            backUrl = txHandler.getDatabaseUserAccess().getUIInfo().getBackUrl();
        } else {
            throw new UserException(UserException.Code.DTP_USER_ILLEGAL_STATE,
                    "Unable to redirect to back url from UserAccess/UIInfo");
        }
        return addParameterToUrl(backUrl, "status", status);
    }
    
    protected String getBackUrl() {
        String backUrl = null;
        UserAccess ua = txHandler.getDatabaseUserAccess();
        if(ua != null && ua.getUIInfo() !=null && ua.getUIInfo().getBackUrl() != null) {
            backUrl = txHandler.getDatabaseUserAccess().getUIInfo().getBackUrl();
        } else {
            throw new UserException(UserException.Code.DTP_USER_ILLEGAL_STATE,
                    "Unable to redirect to back url from UserAccess/UIInfo");
        }
        return backUrl;
    }

    protected void doExternalResourceBundle(HttpServletRequest req, ResourceBundleHandler rbh){
        ResourceBundle rb = txHandler.getResourceBundle(rbh, req.getLocale());
         LocalizationContext lc = new LocalizationContext(rb);
         Config.set(req, Config.FMT_LOCALE, req.getLocale());
         Config.set(req, Config.FMT_LOCALIZATION_CONTEXT, lc);
    }

    protected String getExternalRessources(String tenantName, String applicationID, String company) {
        if(company==null || company.length()==0){
            company = "default";
        }
        String applicationName = txHandler.getApplicationName();
        String url = "ui/" + "static/" + tenantName + "/" + applicationName + "/" + company;
        return url;
    }
}
