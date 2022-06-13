package com.dictao.dtp.web.ws.conversion;

import com.dictao.xsd.dtp.common.v2012_03.UIInfo;
import org.junit.*;
import static org.junit.Assert.*;

public class UIInfoConverterTest {
    
    /**
     * Test of WSUIInfoToUIInfo method, of class UIInfoConverter.
     */
    @Test
    public void testWSUIInfoToUIInfo() {
        System.out.println("WSUIInfoToUIInfo");
        
        assertNull(UIInfoConverter.WSUIInfoToUIInfo(null));
        
        UIInfo wsUIInfo = new UIInfo();
        
        wsUIInfo.setBackUrl("https://www.url.com/");
        wsUIInfo.setConsent("c");
        wsUIInfo.setLabel("l");
        wsUIInfo.setTermAndConditionsUrl("tc");
        wsUIInfo.setType("t");
        wsUIInfo.setUi("ui");
        
        com.dictao.dtp.persistence.data.UIInfo result = UIInfoConverter.WSUIInfoToUIInfo(wsUIInfo);
        
        assertEquals(result.getBackUrl(), "https://www.url.com/");
        assertEquals(result.getConsent(), "c");
        assertEquals(result.getLabel(), "l");
        assertEquals(result.getTermAndConditionsUrl(), "tc");
        assertEquals(result.getType(), "t");
        assertEquals(result.getUi(), "ui");
    }
}
