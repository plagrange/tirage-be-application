package com.dictao.dtp.persistence.data.conversion;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class DocumentTypeListConverterTest {
    
    public DocumentTypeListConverterTest() {
    }

    @Test
    public void testDocumentTypeListConversion() throws Exception {
        System.out.println("documentTypeListConversion");
        
        assertNull(DocumentTypeListConverter.loadDocumentTypeList(null));
        assertNull(DocumentTypeListConverter.saveDocumentTypeList(null));
        
        List<String> documentTypes = new ArrayList<String>();
        testRoundTripConvert(documentTypes);
        
        documentTypes.add("type1");
        documentTypes.add("type2");
        documentTypes.add("type3");
        
        testRoundTripConvert(documentTypes);
    }
    
    private void testRoundTripConvert(List<String> documentTypes) throws InvalidPersistenceDataException {
        
        byte[] raw = DocumentTypeListConverter.saveDocumentTypeList(documentTypes);
        List<String> documentTypes2 = DocumentTypeListConverter.loadDocumentTypeList(raw);
        
        assertArrayEquals(documentTypes.toArray(), documentTypes2.toArray());
    }
}
