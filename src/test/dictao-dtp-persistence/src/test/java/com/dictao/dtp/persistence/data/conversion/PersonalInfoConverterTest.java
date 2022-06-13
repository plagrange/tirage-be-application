package com.dictao.dtp.persistence.data.conversion;

import com.dictao.dtp.persistence.data.PersonalInfo;
import com.dictao.dtp.persistence.data.UserDN;
import java.sql.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import static org.unitils.reflectionassert.ReflectionAssert.*;

public class PersonalInfoConverterTest {

    public PersonalInfoConverterTest() {
    }

    /**
     * Test round trip PersonalInfo marshalling/unmarshalling
     */
    @Test
    public void testPersonalInfoConversion() throws Exception {
        System.out.println("personalInfoConversion");

        assertNull(PersonalInfoConverter.savePersonalInfo(null));
        assertNull(PersonalInfoConverter.loadPersonalInfo(null));

        UserDN userDN = new UserDN("cn", "org", "myOU", "myEmail@dictao.com", "myCN", "mySubjAltName");

        PersonalInfo personalInfo = new PersonalInfo("user1", "title", "fn", "ln", Date.valueOf("2012-04-05"), userDN, true);

        assertEquals(personalInfo.getBirthDate(), Date.valueOf("2012-04-05"));
        assertEquals(personalInfo.getFirstName(), "fn");
        assertEquals(personalInfo.getLastName(), "ln");
        assertEquals(personalInfo.getTitle(), "title");
        assertEquals(personalInfo.getUser(), "user1");
        assertEquals(personalInfo.isMainContractor(), true);

        assertEquals(personalInfo.getUserDN().getCountryName(), "cn");
        assertEquals(personalInfo.getUserDN().getOrganizationName(), "org");
        assertEquals(personalInfo.getUserDN().getOrganizationalUnitName(), "myOU");
        assertEquals(personalInfo.getUserDN().getEmailAddress(), "myEmail@dictao.com");
        assertEquals(personalInfo.getUserDN().getCommonName(), "myCN");
        assertEquals(personalInfo.getUserDN().getSubjectAltName(), "mySubjAltName");

        byte[] rawPersonalInfo = PersonalInfoConverter.savePersonalInfo(personalInfo);
        PersonalInfo personalInfo2 = PersonalInfoConverter.loadPersonalInfo(rawPersonalInfo);

        assertReflectionEquals(personalInfo, personalInfo2);
    }
}
