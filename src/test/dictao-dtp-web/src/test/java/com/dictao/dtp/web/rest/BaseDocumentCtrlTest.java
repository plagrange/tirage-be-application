package com.dictao.dtp.web.rest;

import com.dictao.dtp.core.exceptions.UserException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Assert;
import org.junit.Test;

public class BaseDocumentCtrlTest extends XMLTestCase {

    /**
     * Test of transform method, of class BaseDocumentCtrl.
     */
    @Test
    public void testTransform() throws Exception {
        System.out.println("transform");
        InputStream input = new ByteArrayInputStream("<data>myData</data>".getBytes("UTF-8"));
        String expectedOutput = "<transformed-data>myData</transformed-data>";

        InputStream result = BaseDocumentCtrl.transform(input, "test-transform", "text/html");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = result.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        assertXMLEqual(expectedOutput, new String(baos.toByteArray(), "UTF-8"));
    }

    public void testTransformWithInvalidXslt() throws Exception {
        try {
            System.out.println("testTransformWithInvalidXslt");
            InputStream is = new ByteArrayInputStream("data".getBytes());
            BaseDocumentCtrl.transform(is, "INVALID_XSLT", "text/html");
        } catch (UserException ex) {
            if (ex.getCode().equals(UserException.Code.DTP_USER_INVALID_PARAMETER)) {
                // OK
                return;
            }
        }

        Assert.fail("Expected DTP_USER_INVALID_PARAMETER User exception");
    }

    public void testTransformWithInvalidTranformChars() throws Exception {

        try {
            System.out.println("testTransformWithInvalidTranformChars");
            InputStream is = new ByteArrayInputStream("data".getBytes());
            BaseDocumentCtrl.transform(is, "~#-.", "text/html");
        } catch (UserException ex) {
            if (ex.getCode().equals(UserException.Code.DTP_USER_INVALID_PARAMETER)) {
                // OK
                return;
            }
        }

        Assert.fail("Expected DTP_USER_INVALID_PARAMETER User exception");
    }
}
