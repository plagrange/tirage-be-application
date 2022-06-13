package com.dictao.dtp.persistence.data.conversion;

import com.dictao.dtp.persistence.data.PersonalInfo;
import com.dictao.dtp.persistence.data.UserDN;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.xml.sax.SAXException;

public class PersonalInfoConverter {

    public static PersonalInfo loadPersonalInfo(byte[] data) {
        if (data == null) {
            return null;
        }

        try {

            final Unmarshaller u = JAXBCache.getContextV1().createUnmarshaller();
            u.setSchema(JAXBCache.getSchemaV1());

            com.dictao.dtp.persistence.types.v1.PersonalInfo persistencePersonalInfo = (com.dictao.dtp.persistence.types.v1.PersonalInfo) u.unmarshal(new ByteArrayInputStream(data));
            PersonalInfo result = PersistencePersonalInfoV1ToPersonalInfo(persistencePersonalInfo);

            return result;

        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal Personal Info. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal Personal Info. Data validation failled");
        }
    }

    public static byte[] savePersonalInfo(PersonalInfo personalInfo) {
        if (personalInfo == null) {
            return null;
        }

        try {

            com.dictao.dtp.persistence.types.v1.PersonalInfo persistancePersonalInfo = PersonalInfoToPersistencePersonalInfoV1(personalInfo);

            final Marshaller m = JAXBCache.getContextV1().createMarshaller();
            m.setSchema(JAXBCache.getSchemaV1());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            m.marshal(persistancePersonalInfo, baos);
            return baos.toByteArray();

        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal Personal Info. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal Personal Info. Data validation failled");
        }
    }

    private static PersonalInfo PersistencePersonalInfoV1ToPersonalInfo(com.dictao.dtp.persistence.types.v1.PersonalInfo persistencePersonalInfo) {
        
        if(persistencePersonalInfo == null)
            return null;

        PersonalInfo personalInfo = new PersonalInfo(
                persistencePersonalInfo.getUser(),
                persistencePersonalInfo.getTitle(),
                persistencePersonalInfo.getFirstName(),
                persistencePersonalInfo.getLastName(),
                persistencePersonalInfo.getBirthdate() == null ? null : persistencePersonalInfo.getBirthdate().toGregorianCalendar().getTime(),
                PersistenceUserDNToUserDN(persistencePersonalInfo.getUserDN()),
                persistencePersonalInfo.isMainContractor());

        return personalInfo;

    }
    
    private static UserDN PersistenceUserDNToUserDN(com.dictao.dtp.persistence.types.v1.UserDN persistenceUserDN){
        
        if(persistenceUserDN == null)
            return null;

        return new UserDN(
                persistenceUserDN.getCountryName(),
                persistenceUserDN.getOrganizationName(),
                persistenceUserDN.getOrganizationalUnitName(),
                persistenceUserDN.getEmailAddress(),
                persistenceUserDN.getCommonName(),
                persistenceUserDN.getSubjectAltName());
    }

    private static com.dictao.dtp.persistence.types.v1.PersonalInfo PersonalInfoToPersistencePersonalInfoV1(PersonalInfo personalInfo) {

        com.dictao.dtp.persistence.types.v1.PersonalInfo resPersonalInfo = new com.dictao.dtp.persistence.types.v1.PersonalInfo();

        resPersonalInfo.setFirstName(personalInfo.getFirstName());
        resPersonalInfo.setLastName(personalInfo.getLastName());
        resPersonalInfo.setTitle(personalInfo.getTitle());
        resPersonalInfo.setUser(personalInfo.getUser());
        resPersonalInfo.setMainContractor(personalInfo.isMainContractor());
        resPersonalInfo.setUserDN(UserDNToPersistenceUserDN(personalInfo.getUserDN()));

        try {
            
            if(personalInfo.getBirthDate() != null) {
                GregorianCalendar birthdate = new GregorianCalendar();
                birthdate.setTime(personalInfo.getBirthDate());
                resPersonalInfo.setBirthdate(DatatypeFactory.newInstance().newXMLGregorianCalendar(birthdate));
            }
        } catch (DatatypeConfigurationException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal Personal Info.");
        }

        return resPersonalInfo;
    }
    
    private static com.dictao.dtp.persistence.types.v1.UserDN UserDNToPersistenceUserDN(UserDN userDN){
        
        if(userDN == null)
            return null;
        
        com.dictao.dtp.persistence.types.v1.UserDN resUserDN = new com.dictao.dtp.persistence.types.v1.UserDN();
        
        resUserDN.setCountryName(userDN.getCountryName());
        resUserDN.setOrganizationName(userDN.getOrganizationName());
        resUserDN.setOrganizationalUnitName(userDN.getOrganizationalUnitName());
        resUserDN.setEmailAddress(userDN.getEmailAddress());
        resUserDN.setCommonName(userDN.getCommonName());
        resUserDN.setSubjectAltName(userDN.getSubjectAltName());
        
        return resUserDN;
    }
}
