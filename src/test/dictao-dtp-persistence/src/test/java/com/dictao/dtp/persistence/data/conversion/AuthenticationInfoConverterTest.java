package com.dictao.dtp.persistence.data.conversion;

import com.dictao.dtp.persistence.data.AuthenticationInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import static org.unitils.reflectionassert.ReflectionAssert.*;

public class AuthenticationInfoConverterTest {
    
    public AuthenticationInfoConverterTest() {
    }

    
    /**
     * Test round trip AuthenticationInfo marshalling/unmarshalling
     */
    @Test
    public void testAuthenticationInfoConversion() throws Exception {
        System.out.println("AuthenticationInfoConversion");
        
        assertNull(AuthenticationInfoConverter.saveAuthenticationInfo(null));
        assertNull(AuthenticationInfoConverter.loadAuthenticationInfo(null));
        
        AuthenticationInfo AuthenticationInfo = new AuthenticationInfo("uid", "0000000000", 5);
                
        assertEquals(AuthenticationInfo.getUserId(), "uid");
        assertEquals(AuthenticationInfo.getPhoneNumber(), "0000000000");
        assertEquals(AuthenticationInfo.getSecurityLevel().intValue(), 5);
        
        byte[] rawAuthenticationInfo = AuthenticationInfoConverter.saveAuthenticationInfo(AuthenticationInfo);
        AuthenticationInfo AuthenticationInfo2 = AuthenticationInfoConverter.loadAuthenticationInfo(rawAuthenticationInfo);
        
        assertReflectionEquals(AuthenticationInfo, AuthenticationInfo2);
        
    }
}
