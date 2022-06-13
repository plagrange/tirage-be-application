package com.dictao.dtp.persistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

public class XmlMetadataWriter {

    static public byte[] write(final Element element) throws TransformerException, IOException 
    {
        byte[] xmd = null;
        if (element != null) {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // UTF-8  to support only XML files encoding in UTF-8 
            OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
            transformer.transform(new DOMSource(element), new StreamResult(osw));
            baos.close();
            xmd = baos.toByteArray();
        }
        return xmd;
    }
}
