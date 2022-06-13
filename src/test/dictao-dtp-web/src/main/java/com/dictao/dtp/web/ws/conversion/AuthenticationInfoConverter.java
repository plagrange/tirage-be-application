package com.dictao.dtp.web.ws.conversion;

import com.dictao.dtp.persistence.data.AuthenticationInfo;

public class AuthenticationInfoConverter {

    public static AuthenticationInfo WSAuthenticationInfoToAuthenticationInfo(com.dictao.xsd.dtp.common.v2012_03.AuthenticationInfo wsAuthenticationInfo) {
        
        if(wsAuthenticationInfo == null)
            return null;
    
        AuthenticationInfo authenticationInfo = new AuthenticationInfo(
                wsAuthenticationInfo.getUserId(),
                wsAuthenticationInfo.getPhoneNumber(),
                wsAuthenticationInfo.getSecurityLevel());

        return authenticationInfo;
    }    
}
