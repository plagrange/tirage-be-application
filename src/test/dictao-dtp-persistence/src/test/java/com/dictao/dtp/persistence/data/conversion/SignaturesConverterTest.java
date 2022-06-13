package com.dictao.dtp.persistence.data.conversion;

import com.dictao.dtp.persistence.data.EntitySignature;
import com.dictao.dtp.persistence.data.PersonalSignature;
import com.dictao.dtp.persistence.data.Signatures;
import com.dictao.dtp.persistence.data.VisibleSignatureField;
import java.io.IOException;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.unitils.reflectionassert.ReflectionAssert.*;

public class SignaturesConverterTest extends XMLTestCase {

    public SignaturesConverterTest() {
    }

    /**
     * Test round trip signature marshalling/unmarshalling
     */
    @Test
    public void testSignatureConversion() throws Exception {
        System.out.println("signatureConversion");

        assertNull(SignaturesConverter.saveSignatures(null));
        assertNull(SignaturesConverter.loadSignatures(null));

        Signatures signatures = new Signatures();

        EntitySignature entitySignature = new EntitySignature();
        signatures.setEntitySignature(entitySignature);

        testRoundTripConvert(signatures);

        VisibleSignatureField entityVisibleSignatureField =
                new VisibleSignatureField(1, 2, 3, 4, 5, "layout");

        entitySignature.setVisibleSignatureField(entityVisibleSignatureField);
        testRoundTripConvert(signatures);

        VisibleSignatureField personnalVisibleSignatureField = new VisibleSignatureField(11, 12, 13, 14, 15, "layout2");

        PersonalSignature personalSignature = new PersonalSignature(personnalVisibleSignatureField, "userN", "label");

        signatures.getPersonalSignatures().add(personalSignature);
        testRoundTripConvert(signatures);
    }

    private void testRoundTripConvert(Signatures signatures) throws SAXException, IOException, InvalidPersistenceDataException {
        byte[] rawSignatures = SignaturesConverter.saveSignatures(signatures);
        Signatures signatures2 = SignaturesConverter.loadSignatures(rawSignatures);
        byte[] rawSignatures2 = SignaturesConverter.saveSignatures(signatures2);

        assertXMLEqual(new String(rawSignatures, "UTF-8"),
                new String(rawSignatures2, "UTF-8"));

        assertReflectionEquals(signatures, signatures2);
    }
}
