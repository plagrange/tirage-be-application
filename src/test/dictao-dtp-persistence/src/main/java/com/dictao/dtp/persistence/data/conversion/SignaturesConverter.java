package com.dictao.dtp.persistence.data.conversion;

import com.dictao.dtp.persistence.data.EntitySignature;
import com.dictao.dtp.persistence.data.PersonalSignature;
import com.dictao.dtp.persistence.data.Signatures;
import com.dictao.dtp.persistence.data.VisibleSignatureField;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;

public class SignaturesConverter {
    
    public static Signatures loadSignatures(byte[] data) {

        if (data == null) {
            return null;
        }

        try {

            final Unmarshaller u = JAXBCache.getContextV1().createUnmarshaller();
            u.setSchema(JAXBCache.getSchemaV1());
            
            com.dictao.dtp.persistence.types.v1.Signatures persistenceSignatures = (com.dictao.dtp.persistence.types.v1.Signatures) u.unmarshal(new ByteArrayInputStream(data));
            Signatures result = PersistenceSignaturesV1ToSignatures(persistenceSignatures);
            
            return result;

        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal signatures. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to unmarshal signatures. Data validation failled");
        }
    }

    public static byte[] saveSignatures(Signatures signatures) {

        if (signatures == null) {
            return null;
        }

        try {

            com.dictao.dtp.persistence.types.v1.Signatures persistenceSignatures = SignaturesToPersistenceSignaturesV1(signatures);

            final Marshaller m = JAXBCache.getContextV1().createMarshaller();
            m.setSchema(JAXBCache.getSchemaV1());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            m.marshal(persistenceSignatures, baos);
            return baos.toByteArray();

        } catch (JAXBException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal signatures. Invalid data format");
        } catch (SAXException ex) {
            throw new InvalidPersistenceDataException(ex, "Failed to marshal signatures. Data validation failled");
        }
    }

    private static Signatures PersistenceSignaturesV1ToSignatures(com.dictao.dtp.persistence.types.v1.Signatures signatures) {

        Signatures resSignatures = new Signatures();

        if (signatures.getEntity() != null) {
            EntitySignature resEntitySignature = new EntitySignature();

            if (signatures.getEntity().getVisibleSignature() != null) {

                VisibleSignatureField resEntityVisibleSignatureField =
                        new VisibleSignatureField(signatures.getEntity().getVisibleSignature().getX(),
                        signatures.getEntity().getVisibleSignature().getY(),
                        signatures.getEntity().getVisibleSignature().getWidth(),
                        signatures.getEntity().getVisibleSignature().getHeight(),
                        signatures.getEntity().getVisibleSignature().getPage(),
                        signatures.getEntity().getVisibleSignature().getLayout());

                resEntitySignature.setVisibleSignatureField(resEntityVisibleSignatureField);

            }

            resSignatures.setEntitySignature(resEntitySignature);
        }

        for (com.dictao.dtp.persistence.types.v1.PersonalSignature personalSignature : signatures.getPersonal()) {


            VisibleSignatureField resVisiblePersonalSignature = new VisibleSignatureField(
                    personalSignature.getVisibleSignature().getX(),
                    personalSignature.getVisibleSignature().getY(),
                    personalSignature.getVisibleSignature().getWidth(),
                    personalSignature.getVisibleSignature().getHeight(),
                    personalSignature.getVisibleSignature().getPage(),
                    personalSignature.getVisibleSignature().getLayout());

            PersonalSignature resPersonalSignature = new PersonalSignature(
                    resVisiblePersonalSignature, personalSignature.getUser(),
                    personalSignature.getSignatureLabel());

            resSignatures.getPersonalSignatures().add(resPersonalSignature);

        }

        return resSignatures;
    }

    private static com.dictao.dtp.persistence.types.v1.Signatures SignaturesToPersistenceSignaturesV1(Signatures signatures) {

        com.dictao.dtp.persistence.types.v1.Signatures resSignatures = new com.dictao.dtp.persistence.types.v1.Signatures();

        if (signatures.getEntitySignature() != null) {
            com.dictao.dtp.persistence.types.v1.Signature resEntitySignature = new com.dictao.dtp.persistence.types.v1.Signature();

            if (signatures.getEntitySignature().getVisibleSignatureField() != null) {

                com.dictao.dtp.persistence.types.v1.VisibleSignature resEntityVisibleSignature = new com.dictao.dtp.persistence.types.v1.VisibleSignature();

                resEntityVisibleSignature.setX(signatures.getEntitySignature().getVisibleSignatureField().getX());
                resEntityVisibleSignature.setY(signatures.getEntitySignature().getVisibleSignatureField().getY());
                resEntityVisibleSignature.setWidth(signatures.getEntitySignature().getVisibleSignatureField().getWidth());
                resEntityVisibleSignature.setHeight(signatures.getEntitySignature().getVisibleSignatureField().getHeight());
                resEntityVisibleSignature.setPage(signatures.getEntitySignature().getVisibleSignatureField().getPage());
                resEntityVisibleSignature.setLayout(signatures.getEntitySignature().getVisibleSignatureField().getLayout());

                resEntitySignature.setVisibleSignature(resEntityVisibleSignature);

            }

            resSignatures.setEntity(resEntitySignature);
        }

        for (PersonalSignature personalSignature : signatures.getPersonalSignatures()) {
            com.dictao.dtp.persistence.types.v1.PersonalSignature resPersonalSignature = new com.dictao.dtp.persistence.types.v1.PersonalSignature();
            resPersonalSignature.setUser(personalSignature.getUser());
            resPersonalSignature.setSignatureLabel(personalSignature.getSignatureLabel());

            if (personalSignature.getVisibleSignatureField() == null) {
                throw new InvalidPersistenceDataException("Failed to marshal signatures. No visible signature field set for personal signature.");
            }

            com.dictao.dtp.persistence.types.v1.VisibleSignature resPersonalVisibleSignature = new com.dictao.dtp.persistence.types.v1.VisibleSignature();

            resPersonalVisibleSignature.setX(personalSignature.getVisibleSignatureField().getX());
            resPersonalVisibleSignature.setY(personalSignature.getVisibleSignatureField().getY());
            resPersonalVisibleSignature.setWidth(personalSignature.getVisibleSignatureField().getWidth());
            resPersonalVisibleSignature.setHeight(personalSignature.getVisibleSignatureField().getHeight());
            resPersonalVisibleSignature.setPage(personalSignature.getVisibleSignatureField().getPage());
            resPersonalVisibleSignature.setLayout(personalSignature.getVisibleSignatureField().getLayout());

            resPersonalSignature.setVisibleSignature(resPersonalVisibleSignature);
            resSignatures.getPersonal().add(resPersonalSignature);
        }

        return resSignatures;
    }
}
