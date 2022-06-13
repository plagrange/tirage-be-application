package com.dictao.dtp.persistence.data.conversion;

import com.dictao.dtp.persistence.data.AuthenticationInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;


public class AuthenticationInfoConverter {
    
    public static AuthenticationInfo loadAuthenticationInfo(byte[] data) {
        if (data == null) {
            return null;
        }

        try {

            final Unmarshaller u = JAXBCache.getContextV1().createUnmarshaller();
            u.setSchema(JAXBCache.getSchemaV1());

            com.dictao.dtp.persistence.types.v1.AuthenticationInfo persistenceAuthentInfo = (com.dictao.dtp.persistence.types.v1.AuthenticationInfo) u.unmarshal(new ByteArrayInputStream(data));
            AuthenticationInfo result = PersistenceAuthenticationInfoV1ToAuthenticationInfo(persistenceAuthentInfo);

            return result;

        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal Authentication Info. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal Authentication Info. Data validation failled");
        }
    }

    public static byte[] saveAuthenticationInfo(AuthenticationInfo AuthenticationInfo) {

        if (AuthenticationInfo == null) {
            return null;
        }

        try {

            com.dictao.dtp.persistence.types.v1.AuthenticationInfo persistanceAuthenticationInfo = AuthenticationInfoToPersistenceAuthenticationInfoV1(AuthenticationInfo);

            final Marshaller m = JAXBCache.getContextV1().createMarshaller();
            m.setSchema(JAXBCache.getSchemaV1());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            m.marshal(persistanceAuthenticationInfo, baos);
            return baos.toByteArray();

        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal Authentication Info. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal Authentication Info. Data validation failled");
        }
    }

    private static AuthenticationInfo PersistenceAuthenticationInfoV1ToAuthenticationInfo(com.dictao.dtp.persistence.types.v1.AuthenticationInfo persistenceAuthenticationInfo) {

        AuthenticationInfo AuthenticationInfo = new AuthenticationInfo(
                persistenceAuthenticationInfo.getUserId(),
                persistenceAuthenticationInfo.getPhoneNumber(),
                persistenceAuthenticationInfo.getSecurityLevel());

        return AuthenticationInfo;
    }

    private static com.dictao.dtp.persistence.types.v1.AuthenticationInfo AuthenticationInfoToPersistenceAuthenticationInfoV1(AuthenticationInfo AuthenticationInfo) {

        com.dictao.dtp.persistence.types.v1.AuthenticationInfo resAuthenticationInfo = new com.dictao.dtp.persistence.types.v1.AuthenticationInfo();
        resAuthenticationInfo.setUserId(AuthenticationInfo.getUserId());
        resAuthenticationInfo.setPhoneNumber(AuthenticationInfo.getPhoneNumber());
        resAuthenticationInfo.setSecurityLevel(AuthenticationInfo.getSecurityLevel());

        return resAuthenticationInfo;
    }

}
