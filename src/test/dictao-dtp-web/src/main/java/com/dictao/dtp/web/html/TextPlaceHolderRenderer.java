package com.dictao.dtp.web.html;

import com.dictao.dtp.core.ContentType;
import com.dictao.dtp.core.api.converting.converter.ConvertToText;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.types.metadata.v2011_06.PlaceHolderTextType;
import com.dictao.dtp.types.metadata.v2011_06.PlaceHolderTextType.PlaceHolderContent;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 *
 * @author msauvee
 */
public class TextPlaceHolderRenderer {

    private static String escapeTextForHtml(String text) {
        ConvertToText conv = null;
        try {
            conv = new ConvertToText(new MimeType(ContentType.MIMETYPE_XML));
        } catch (MimeTypeParseException ex) {
            throw new EnvironmentException(ex, EnvironmentException.Code.DTP_ENV_INTERNAL_ERROR,
                    "Unable to convert text");
        }
        conv.setSourceEncoding("UTF-8");
        return conv.convert(text, ContentType.MIMETYPE_HTML);
    }

    public static String render(PlaceHolderTextType pht) {
        String text = escapeTextForHtml(pht.getText());
        String[] args = new String[pht.getPlaceHolderContent().size()];
        int i = 0;
        for (PlaceHolderContent phc : pht.getPlaceHolderContent()) {
            String s = escapeTextForHtml(phc.getText());
            String cssClass = "";
            if (phc.getCssClass() != null) {
                cssClass = " class=\"" + phc.getCssClass() + "\"";
            }
            if (phc.getLinkUrl() != null) {
                s = "<a href=\"" + phc.getLinkUrl() + "\"" + cssClass + ">" + s + "</a>";
            } else {
                s = "<span" + cssClass + ">" + s + "</span>";
            }
            args[i] = s;
            i++;
        }
        text = text.replace("&#037;s", "%s");
        text = String.format(text, (Object[])args);
        return text;
    }
}
