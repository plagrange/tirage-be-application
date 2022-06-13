package com.dictao.dtp.web.ws.conversion;

import com.dictao.xsd.dtp.common.v2012_03.AuthenticationInfo;
import org.junit.*;
import static org.junit.Assert.*;

public class AuthenticationInfoConverterTest {
    
    /**
     * Test of WSAuthenticationInfoToAuthenticationInfo method, of class AuthenticationInfoConverter.
     */
    @Test
    public void testWSAuthenticationInfoToAuthenticationInfo() {
        System.out.println("WSAuthenticationInfoToAuthenticationInfo");
        
        assertNull(AuthenticationInfoConverter.WSAuthenticationInfoToAuthenticationInfo(null));
        
        AuthenticationInfo wsAuthenticationInfo = new AuthenticationInfo();
        wsAuthenticationInfo.setUserId("uid");
        wsAuthenticationInfo.setSecurityLevel(5);
        wsAuthenticationInfo.setPhoneNumber("0605050505");
        
        com.dictao.dtp.persistence.data.AuthenticationInfo result = AuthenticationInfoConverter.WSAuthenticationInfoToAuthenticationInfo(wsAuthenticationInfo);
        
        assertEquals(result.getUserId(), "uid");
        assertEquals(result.getPhoneNumber(), "0605050505");
        assertEquals(result.getSecurityLevel().intValue(), 5);
        
    }
}
