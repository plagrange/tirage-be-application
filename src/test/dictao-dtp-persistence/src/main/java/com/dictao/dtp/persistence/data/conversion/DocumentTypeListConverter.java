package com.dictao.dtp.persistence.data.conversion;

import com.dictao.dtp.persistence.types.v1.DocumentTypeList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;

public class DocumentTypeListConverter {

    public static List<String> loadDocumentTypeList(byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            
            final Unmarshaller u = JAXBCache.getContextV1().createUnmarshaller();
            u.setSchema(JAXBCache.getSchemaV1());

            DocumentTypeList docTypes = (DocumentTypeList) u.unmarshal(new ByteArrayInputStream(data));
            return docTypes.getDocumentType();
            
        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal signatures. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal signatures. Data validation failled");
        }
    }

    public static byte[] saveDocumentTypeList(List<String> docTypes) {
        if (docTypes == null) {
            return null;
        }

        try {
            
            DocumentTypeList docTypeList = new DocumentTypeList();
            docTypeList.getDocumentType().addAll(docTypes);

            final Marshaller m = JAXBCache.getContextV1().createMarshaller();
            m.setSchema(JAXBCache.getSchemaV1());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            m.marshal(docTypeList, baos);
            return baos.toByteArray();
            
        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal document types. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal document types. Data validation failled");
        }
    }
}
