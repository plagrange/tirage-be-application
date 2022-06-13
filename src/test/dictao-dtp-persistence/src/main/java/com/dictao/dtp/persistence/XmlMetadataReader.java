package com.dictao.dtp.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class XmlMetadataReader {
	
    public static Element read(byte[] xmlMetadata) throws TransformerException, IOException, ParserConfigurationException, SAXException  {
        Element element = null;
        if (xmlMetadata == null) {
            element = null;
        } else {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// validation was disabled with jira DTPJAVA-2162 (another jira will be opened to handle validation properly)
            
            // Force XXE attack protection
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            
            DocumentBuilder builder = dbf.newDocumentBuilder();
            final InputStream is = new ByteArrayInputStream(xmlMetadata);
            final Document doc = builder.parse(is);
            element = doc.getDocumentElement();
            is.close();
        }
        return element;
    }
}
