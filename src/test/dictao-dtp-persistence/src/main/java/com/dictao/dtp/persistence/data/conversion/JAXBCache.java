package com.dictao.dtp.persistence.data.conversion;

import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

public class JAXBCache {
    
    private static final String PERSISTENCE_V1_XSD_FILE = "resources/xsd/dtp-persistence-v1.xsd";
    private static JAXBContext s_jaxbContextV1 = null;
    private static Schema s_schemaV1 = null;

    static synchronized Schema getSchemaV1() throws SAXException {

        if (s_schemaV1 == null) {
            final URL schemaURL = JAXBCache.class.getClassLoader().getResource(PERSISTENCE_V1_XSD_FILE);
            final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            s_schemaV1 = sf.newSchema(schemaURL);
        }
        return s_schemaV1;
    }

    static synchronized JAXBContext getContextV1() throws JAXBException {

        if (s_jaxbContextV1 == null) {
            s_jaxbContextV1 = JAXBContext.newInstance(com.dictao.dtp.persistence.types.v1.ObjectFactory.class);
        }

        return s_jaxbContextV1;
    }
}
