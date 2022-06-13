/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dictao.dtp.web.html;

import com.dictao.dtp.types.metadata.v2011_06.PlaceHolderTextType.PlaceHolderContent;
import com.dictao.dtp.types.metadata.v2011_06.PlaceHolderTextType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author msauvee
 */
public class TextPlaceHolderTest {

    public TextPlaceHolderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWithoutPlaceHolder() {
        PlaceHolderTextType pht = new PlaceHolderTextType();
        pht.setText("test");
        assertEquals("test", TextPlaceHolderRenderer.render(pht));
    }

    @Test
    public void testHTMLEscapingInMainText() {
        PlaceHolderTextType pht = new PlaceHolderTextType();
        pht.setText("<table>");
        assertEquals("&lt;table&gt;", TextPlaceHolderRenderer.render(pht));
    }

    @Test
    public void testHTMLEscapingInContentText() {
        PlaceHolderTextType pht = new PlaceHolderTextType();
        pht.setText("1 %s 2");
        PlaceHolderContent phc = new PlaceHolderContent();
        phc.setText("<first>");
        pht.getPlaceHolderContent().add(phc);
        assertEquals("1 <span>&lt;first&gt;</span> 2", TextPlaceHolderRenderer.render(pht));
    }

    @Test
    public void testWithSpanPlaceHolder() {
        PlaceHolderTextType pht = new PlaceHolderTextType();
        pht.setText("1 %s 2 %s 3");
        PlaceHolderContent phc = new PlaceHolderContent();
        phc.setText("first");
        pht.getPlaceHolderContent().add(phc);
        phc = new PlaceHolderContent();
        phc.setText("second");
        phc.setCssClass("class2");
        pht.getPlaceHolderContent().add(phc);
        assertEquals("1 <span>first</span> 2 <span class=\"class2\">second</span> 3", TextPlaceHolderRenderer.render(pht));
    }

    @Test
    public void testWithLinkPlaceHolder() {
        PlaceHolderTextType pht = new PlaceHolderTextType();
        pht.setText("1 %s 2 %s 3");
        PlaceHolderContent phc = new PlaceHolderContent();
        phc.setText("first");
        phc.setLinkUrl("myurl");
        pht.getPlaceHolderContent().add(phc);
        phc = new PlaceHolderContent();
        phc.setText("second");
        phc.setCssClass("class2");
        phc.setLinkUrl("myurl2");
        pht.getPlaceHolderContent().add(phc);
        assertEquals("1 <a href=\"myurl\">first</a> 2 <a href=\"myurl2\" class=\"class2\">second</a> 3", TextPlaceHolderRenderer.render(pht));
    }
}