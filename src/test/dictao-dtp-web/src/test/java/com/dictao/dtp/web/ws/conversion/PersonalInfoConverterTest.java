package com.dictao.dtp.web.ws.conversion;

import com.dictao.xsd.dtp.common.v2012_03.PersonalInfo;
import com.dictao.xsd.dtp.common.v2012_03.UserDN;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.*;
import static org.junit.Assert.*;

public class PersonalInfoConverterTest {
    
    /**
     * Test of WSPersonalInfoToPersonalInfo method, of class PersonalInfoConverter.
     */
    @Test
    public void testWSPersonalInfoToPersonalInfo() throws ParseException, DatatypeConfigurationException {
        System.out.println("WSPersonalInfoToPersonalInfo");
        
            assertNull(PersonalInfoConverter.WSPersonalInfoToPersonalInfo(null));
        
        PersonalInfo wsPersonalInfo = new PersonalInfo();
        
        wsPersonalInfo.setFirstName("fn");
        wsPersonalInfo.setLastName("ln");
        wsPersonalInfo.setMainContractor(Boolean.TRUE);
        wsPersonalInfo.setTitle("title");
        wsPersonalInfo.setUser("USER0");

        GregorianCalendar birthdate = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        Date birthdateDate = sdf.parse("12-07-2010");
        birthdate.setTime(birthdateDate);
        wsPersonalInfo.setBirthdate(DatatypeFactory.newInstance().newXMLGregorianCalendar(birthdate));
        
        com.dictao.dtp.persistence.data.PersonalInfo result = PersonalInfoConverter.WSPersonalInfoToPersonalInfo(wsPersonalInfo);
        
        assertEquals(result.getBirthDate(), birthdateDate);
        assertEquals(result.getFirstName(), "fn");
        assertEquals(result.getLastName(), "ln");
        assertEquals(result.getTitle(), "title");
        assertEquals(result.getUser(), "USER0");
        assertEquals(result.isMainContractor(), true);
        assertEquals(result.getUserDN(), null);
        
        UserDN userDN = new UserDN();
        userDN.setCommonName("cn");
        userDN.setCountryName("country");
        userDN.setEmailAddress("email@dictao.com");
        userDN.setOrganizationName("o");
        userDN.setOrganizationalUnitName("ou");
        userDN.setSubjectAltName("alt");
        wsPersonalInfo.setUserDN(userDN);
        
        result = PersonalInfoConverter.WSPersonalInfoToPersonalInfo(wsPersonalInfo);
            
        assertEquals(result.getUserDN().getCommonName(), "cn");
        assertEquals(result.getUserDN().getCountryName(), "country");
        assertEquals(result.getUserDN().getEmailAddress(), "email@dictao.com");
        assertEquals(result.getUserDN().getOrganizationName(), "o");
        assertEquals(result.getUserDN().getOrganizationalUnitName(), "ou");
        assertEquals(result.getUserDN().getSubjectAltName(), "alt");
    }
}
