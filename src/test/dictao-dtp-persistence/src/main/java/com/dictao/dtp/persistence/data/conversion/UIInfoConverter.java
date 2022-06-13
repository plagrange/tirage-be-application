package com.dictao.dtp.persistence.data.conversion;

import com.dictao.dtp.persistence.data.UIInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;

public class UIInfoConverter {

    public static UIInfo loadUIInfo(byte[] data) {
        if (data == null) {
            return null;
        }

        try {

            final Unmarshaller u = JAXBCache.getContextV1().createUnmarshaller();
            u.setSchema(JAXBCache.getSchemaV1());

            com.dictao.dtp.persistence.types.v1.UiInfo persistenceUIInfo = (com.dictao.dtp.persistence.types.v1.UiInfo) u.unmarshal(new ByteArrayInputStream(data));
            UIInfo result = PersistenceUIInfoV1ToUIInfo(persistenceUIInfo);

            return result;

        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal UI Info. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal UI Info. Data validation failled");
        }
    }

    public static byte[] saveUIInfo(UIInfo uiInfo) {

        if (uiInfo == null) {
            return null;
        }

        try {

            com.dictao.dtp.persistence.types.v1.UiInfo persistanceUIInfo = UIInfoToPersistenceUIInfoV1(uiInfo);

            final Marshaller m = JAXBCache.getContextV1().createMarshaller();
            m.setSchema(JAXBCache.getSchemaV1());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            m.marshal(persistanceUIInfo, baos);
            return baos.toByteArray();

        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal UI Info. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal UI Info. Data validation failled");
        }
    }

    private static UIInfo PersistenceUIInfoV1ToUIInfo(com.dictao.dtp.persistence.types.v1.UiInfo persistenceUIInfo) {

        UIInfo uiInfo = new UIInfo(
                persistenceUIInfo.getUi(),
                persistenceUIInfo.getLabel(),
                persistenceUIInfo.getType(),
                persistenceUIInfo.getConsent(),
                persistenceUIInfo.getTermAndConditionsUrl(),
                persistenceUIInfo.getBackUrl(),
                persistenceUIInfo.getDocumentTypes().size()==0?null:persistenceUIInfo.getDocumentTypes());

        return uiInfo;
    }

    private static com.dictao.dtp.persistence.types.v1.UiInfo UIInfoToPersistenceUIInfoV1(UIInfo uiInfo) {

        com.dictao.dtp.persistence.types.v1.UiInfo resUIInfo = new com.dictao.dtp.persistence.types.v1.UiInfo();
        resUIInfo.setBackUrl(uiInfo.getBackUrl());
        resUIInfo.setConsent(uiInfo.getConsent());
        resUIInfo.setLabel(uiInfo.getLabel());
        resUIInfo.setTermAndConditionsUrl(uiInfo.getTermAndConditionsUrl());
        resUIInfo.setType(uiInfo.getType());
        resUIInfo.setUi(uiInfo.getUi());
        if(uiInfo.getDocumentTypes()!=null && uiInfo.getDocumentTypes().size()!=0)
      	resUIInfo.getDocumentTypes().addAll(uiInfo.getDocumentTypes());
        		
        return resUIInfo;
    }
}
