package com.dictao.dtp.web.ws.conversion;

import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.persistence.data.EntitySignature;
import com.dictao.dtp.persistence.data.PersonalSignature;
import com.dictao.dtp.persistence.data.Signatures;
import com.dictao.dtp.persistence.data.VisibleSignatureField;

public class SignaturesConverter {
    
    public static com.dictao.xsd.dtp.common.v2012_03.Signatures SignatureToWSSignatures(com.dictao.dtp.persistence.data.Signatures signatures) {
        
        if(signatures == null)
            return null;
        
        com.dictao.xsd.dtp.common.v2012_03.Signatures resSignatures = new com.dictao.xsd.dtp.common.v2012_03.Signatures();

        if (signatures.getEntitySignature() != null) {
            com.dictao.xsd.dtp.common.v2012_03.Signature resEntitySignature = new com.dictao.xsd.dtp.common.v2012_03.Signature();

            if (signatures.getEntitySignature().getVisibleSignatureField() != null) {

                com.dictao.xsd.dtp.common.v2012_03.VisibleSignature resEntityVisibleSignature = new com.dictao.xsd.dtp.common.v2012_03.VisibleSignature();

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
            com.dictao.xsd.dtp.common.v2012_03.PersonalSignature resPersonalSignature = new com.dictao.xsd.dtp.common.v2012_03.PersonalSignature();
            resPersonalSignature.setUser(personalSignature.getUser());
            resPersonalSignature.setSignatureLabel(personalSignature.getSignatureLabel());

            if (personalSignature.getVisibleSignatureField() == null) {
                throw new UserException(UserException.Code.DTP_USER_INVALID_CONTEXT,  "Failed to retrieve signatures. No visible signature field set for personal signature.");
            }

            com.dictao.xsd.dtp.common.v2012_03.VisibleSignature resPersonalVisibleSignature = new com.dictao.xsd.dtp.common.v2012_03.VisibleSignature();

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

    public static Signatures WSSignaturesToSignature(com.dictao.xsd.dtp.common.v2012_03.Signatures signatures) {
        
        if(signatures == null)
            return null;
        
        Signatures resSignatures = new Signatures();
        
        if(signatures.getEntity() != null) {
            
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
        
        for (com.dictao.xsd.dtp.common.v2012_03.PersonalSignature personalSignature : signatures.getPersonal()) {
            
            if(personalSignature.getVisibleSignature() == null)
                throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER, "Invalid signature fields. Personal signatures must have a non null visual signature field.");
            
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
}
