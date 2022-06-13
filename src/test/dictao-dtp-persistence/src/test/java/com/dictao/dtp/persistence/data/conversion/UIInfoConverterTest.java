package com.dictao.dtp.persistence.data.conversion;

import com.dictao.dtp.persistence.data.UIInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import static org.unitils.reflectionassert.ReflectionAssert.*;

public class UIInfoConverterTest {
    
    public UIInfoConverterTest() {
    }

    
    /**
     * Test round trip UIInfo marshalling/unmarshalling
     */
    @Test
    public void testUIInfoConversion() throws Exception {
        System.out.println("uiInfoConversion");
        
        assertNull(UIInfoConverter.saveUIInfo(null));
        assertNull(UIInfoConverter.loadUIInfo(null));
        
        UIInfo uiInfo = new UIInfo("ui", "label", "type", "consent", "termAndConditionsUrl", "http://backUrl/");
        
        assertEquals(uiInfo.getBackUrl(), "http://backUrl/");
        assertEquals(uiInfo.getConsent(), "consent");
        assertEquals(uiInfo.getLabel(), "label");
        assertEquals(uiInfo.getTermAndConditionsUrl(), "termAndConditionsUrl");
        assertEquals(uiInfo.getType(), "type");
        assertEquals(uiInfo.getUi(), "ui");
        
        byte[] rawUIInfo = UIInfoConverter.saveUIInfo(uiInfo);
        UIInfo uiInfo2 = UIInfoConverter.loadUIInfo(rawUIInfo);
        
        assertReflectionEquals(uiInfo, uiInfo2);
    }
}
