package com.dictao.dtp.web.ws.conversion;

import com.dictao.dtp.persistence.data.PersonalInfo;
import com.dictao.dtp.persistence.data.UserDN;

public class PersonalInfoConverter {

    public static PersonalInfo WSPersonalInfoToPersonalInfo(com.dictao.xsd.dtp.common.v2012_03.PersonalInfo wsPersonalInfo) {
        
        if(wsPersonalInfo == null)
            return null;

        PersonalInfo personalInfo = new PersonalInfo(
                wsPersonalInfo.getUser(),
                wsPersonalInfo.getTitle(),
                wsPersonalInfo.getFirstName(),
                wsPersonalInfo.getLastName(),
                wsPersonalInfo.getBirthdate() != null ? wsPersonalInfo.getBirthdate().toGregorianCalendar().getTime() : null,
                WSUserDNToUserDN(wsPersonalInfo.getUserDN()),
                wsPersonalInfo.isMainContractor());

        return personalInfo;

    }

    public static UserDN WSUserDNToUserDN(com.dictao.xsd.dtp.common.v2012_03.UserDN wsUserDN) {
        if (wsUserDN == null) {
            return null;
        }

        return new UserDN(
                wsUserDN.getCountryName(),
                wsUserDN.getOrganizationName(),
                wsUserDN.getOrganizationalUnitName(),
                wsUserDN.getEmailAddress(),
                wsUserDN.getCommonName(),
                wsUserDN.getSubjectAltName());
    }
}
