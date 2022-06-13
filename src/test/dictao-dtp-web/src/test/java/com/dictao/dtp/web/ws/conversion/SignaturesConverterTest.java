package com.dictao.dtp.web.ws.conversion;

import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.persistence.data.EntitySignature;
import com.dictao.dtp.persistence.data.PersonalSignature;
import com.dictao.dtp.persistence.data.Signatures;
import com.dictao.dtp.persistence.data.VisibleSignatureField;
import com.dictao.xsd.dtp.common.v2012_03.Signature;
import com.dictao.xsd.dtp.common.v2012_03.VisibleSignature;
import org.junit.*;
import static org.junit.Assert.*;

public class SignaturesConverterTest {
    
    /**
     * Test of SignatureToWSSignatures method, of class SignaturesConverter.
     */
    @Test
    public void testSignatureToWSSignatures() {
        System.out.println("SignatureToWSSignatures");
        
        assertNull(SignaturesConverter.SignatureToWSSignatures(null));
        
        Signatures signatures = new Signatures();
        
        com.dictao.xsd.dtp.common.v2012_03.Signatures result = SignaturesConverter.SignatureToWSSignatures(signatures);
        assertNull(result.getEntity());
        assertTrue(result.getPersonal().isEmpty());

        signatures.setEntitySignature(new EntitySignature());
        
        result = SignaturesConverter.SignatureToWSSignatures(signatures);
        assertNotNull(result.getEntity());
        assertNull(result.getEntity().getVisibleSignature());
        
        signatures.getEntitySignature().setVisibleSignatureField(new VisibleSignatureField(1, 2, 3, 4, 5, "layout"));
        
        result = SignaturesConverter.SignatureToWSSignatures(signatures);
        assertNotNull(result.getEntity().getVisibleSignature());
        assertEquals(result.getEntity().getVisibleSignature().getX(), 1);
        assertEquals(result.getEntity().getVisibleSignature().getY(), 2);
        assertEquals(result.getEntity().getVisibleSignature().getWidth(), 3);
        assertEquals(result.getEntity().getVisibleSignature().getHeight(), 4);
        assertEquals(result.getEntity().getVisibleSignature().getPage(), 5);
        assertEquals(result.getEntity().getVisibleSignature().getLayout(), "layout");
        
        signatures.getPersonalSignatures().add(
                new PersonalSignature(
                new VisibleSignatureField(11, 12, 13, 14, 15, "layout2"), "user0", "label"));
        
        result = SignaturesConverter.SignatureToWSSignatures(signatures);
        assertEquals(result.getPersonal().size(), 1);
        
        assertEquals(result.getPersonal().get(0).getUser(), "user0");
        assertEquals(result.getPersonal().get(0).getSignatureLabel(), "label");
        assertEquals(result.getPersonal().get(0).getVisibleSignature().getX(), 11);
        assertEquals(result.getPersonal().get(0).getVisibleSignature().getY(), 12);
        assertEquals(result.getPersonal().get(0).getVisibleSignature().getWidth(), 13);
        assertEquals(result.getPersonal().get(0).getVisibleSignature().getHeight(), 14);
        assertEquals(result.getPersonal().get(0).getVisibleSignature().getPage(), 15);
        assertEquals(result.getPersonal().get(0).getVisibleSignature().getLayout(), "layout2");
        
        result = SignaturesConverter.SignatureToWSSignatures(signatures);
        assertEquals(result.getPersonal().size(), 1);
        
        signatures.getPersonalSignatures().get(0).setVisibleSignatureField(null);
        try{
            SignaturesConverter.SignatureToWSSignatures(signatures);
        }   catch(UserException ex){
            if(ex.getCode().equals(UserException.Code.DTP_USER_INVALID_CONTEXT)) {
                // Test OK
                return;
            }
        }
        fail("Expected UserException with DTP_USER_INVALID_CONTEXT");
    }

    /**
     * Test of WSSignaturesToSignature method, of class SignaturesConverter.
     */
    @Test
    public void testWSSignaturesToSignature() {
        System.out.println("WSSignaturesToSignature");
        
        assertNull(SignaturesConverter.WSSignaturesToSignature(null));
        
        com.dictao.xsd.dtp.common.v2012_03.Signatures signatures =  new com.dictao.xsd.dtp.common.v2012_03.Signatures();
        
        Signatures result = SignaturesConverter.WSSignaturesToSignature(signatures);
        assertNull(result.getEntitySignature());
        assertTrue(result.getPersonalSignatures().isEmpty());
        
        signatures.setEntity(new Signature());
        result = SignaturesConverter.WSSignaturesToSignature(signatures);
        assertNotNull(result.getEntitySignature());
        assertNull(result.getEntitySignature().getVisibleSignatureField());
        
        com.dictao.xsd.dtp.common.v2012_03.VisibleSignature visEntitySignature = new com.dictao.xsd.dtp.common.v2012_03.VisibleSignature();
        visEntitySignature.setX(1);
        visEntitySignature.setY(2);
        visEntitySignature.setWidth(3);
        visEntitySignature.setHeight(4);
        visEntitySignature.setPage(5);
        visEntitySignature.setLayout("le");
        signatures.getEntity().setVisibleSignature(visEntitySignature);
        result = SignaturesConverter.WSSignaturesToSignature(signatures);
        assertNotNull(result.getEntitySignature().getVisibleSignatureField());
        assertEquals(result.getEntitySignature().getVisibleSignatureField().getX(), 1);
        assertEquals(result.getEntitySignature().getVisibleSignatureField().getY(), 2);
        assertEquals(result.getEntitySignature().getVisibleSignatureField().getWidth(), 3);
        assertEquals(result.getEntitySignature().getVisibleSignatureField().getHeight(), 4);
        assertEquals(result.getEntitySignature().getVisibleSignatureField().getPage(), 5);
        assertEquals(result.getEntitySignature().getVisibleSignatureField().getLayout(), "le");
        
        
        com.dictao.xsd.dtp.common.v2012_03.PersonalSignature ps = new com.dictao.xsd.dtp.common.v2012_03.PersonalSignature();
        ps.setUser("user0");
        ps.setSignatureLabel("label");
        com.dictao.xsd.dtp.common.v2012_03.VisibleSignature visPS = new com.dictao.xsd.dtp.common.v2012_03.VisibleSignature();
        visPS.setX(11);
        visPS.setY(12);
        visPS.setWidth(13);
        visPS.setHeight(14);
        visPS.setPage(15);
        visPS.setLayout("lp");
        ps.setVisibleSignature(visPS);
        signatures.getPersonal().add(ps);
        result = SignaturesConverter.WSSignaturesToSignature(signatures);
        
        assertEquals(result.getPersonalSignatures().get(0).getUser(), "user0");
        assertEquals(result.getPersonalSignatures().get(0).getSignatureLabel(), "label");
        assertEquals(result.getPersonalSignatures().get(0).getVisibleSignatureField().getX(), 11);
        assertEquals(result.getPersonalSignatures().get(0).getVisibleSignatureField().getY(), 12);
        assertEquals(result.getPersonalSignatures().get(0).getVisibleSignatureField().getWidth(), 13);
        assertEquals(result.getPersonalSignatures().get(0).getVisibleSignatureField().getHeight(), 14);
        assertEquals(result.getPersonalSignatures().get(0).getVisibleSignatureField().getPage(), 15);
        assertEquals(result.getPersonalSignatures().get(0).getVisibleSignatureField().getLayout(), "lp");
        
        signatures.getPersonal().get(0).setVisibleSignature(null);
        
        try{
            SignaturesConverter.WSSignaturesToSignature(signatures);
        }   catch(UserException ex){
            if(ex.getCode().equals(UserException.Code.DTP_USER_INVALID_PARAMETER)) {
                // Test OK
                return;
            }
        }
        fail("Expected UserException with DTP_USER_INVALID_PARAMETER");
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
