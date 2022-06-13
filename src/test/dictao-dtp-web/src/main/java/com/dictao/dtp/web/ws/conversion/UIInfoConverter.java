package com.dictao.dtp.web.ws.conversion;

import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.persistence.data.UIInfo;

public class UIInfoConverter {

    public static final String REGEXP_VALID_URL = "^(http(s)?:/)?/.*";

    public static UIInfo WSUIInfoToUIInfo(com.dictao.xsd.dtp.common.v2012_03.UIInfo wsUIInfo) {

        if (wsUIInfo == null)
            return null;

        if (null != wsUIInfo.getBackUrl() && !wsUIInfo.getBackUrl().matches(REGEXP_VALID_URL))
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid url format provided into UIInfo [backUrl='%s']", wsUIInfo.getBackUrl());
        
        UIInfo uiInfo = new UIInfo(
                wsUIInfo.getUi(),
                wsUIInfo.getLabel(),
                wsUIInfo.getType(),
                wsUIInfo.getConsent(),
                wsUIInfo.getTermAndConditionsUrl(),
                wsUIInfo.getBackUrl(),
                wsUIInfo.getDocumentTypes());

        return uiInfo;
    }
}
