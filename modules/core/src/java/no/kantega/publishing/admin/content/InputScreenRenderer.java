/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content;

import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.content.util.AttributeHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.HtmltextAttribute;
import no.kantega.publishing.common.exception.InvalidTemplateException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class InputScreenRenderer {
    private static final String SOURCE = "aksess.admin.InputScreenRenderer";

    private PageContext pageContext = null;
    private Content content = null;
    private int attributeType = -1;

    public InputScreenRenderer(PageContext pageContext, Content content, int attributeType) throws SystemException, InvalidFileException, InvalidTemplateException {
        this.pageContext = pageContext;
        this.content  = content;
        this.attributeType = attributeType;
    }


    /**
     * Genererer Javascript som kalles før redigering starter (ved lasting av siden)
     */
    public void generatePreJavascript() throws IOException {
        JspWriter out = pageContext.getOut();
        List attrlist  = content.getAttributes(attributeType);

        for (int i = 0; i < attrlist.size(); i++) {
            Attribute attr = (Attribute)attrlist.get(i);
            if (attr.isEditable() &&  !attr.isHidden(content)) {
                if (attr instanceof HtmltextAttribute) {
                    HtmltextAttribute htmlAttr = (HtmltextAttribute)attr;

                    // Initialiser kode for editor
                    String inputField = AttributeHelper.getInputFieldName(attr.getName());
                    String editor = "editor_" + inputField;
                    String hidden  = "document.myform." + inputField;

                    String cssPath = "";
                    String cssfile = htmlAttr.getCss();
                    try {
                        Site site = SiteCache.getSiteById(content.getAssociation().getSiteId());
                        cssPath = "/css" + site.getAlias() + htmlAttr.getCss();
                        InputStream is = pageContext.getServletContext().getResourceAsStream(cssPath);
                        if (is == null) {
                            cssPath = site.getAlias() + "css/" + htmlAttr.getCss();
                        }
                    } catch (SystemException e) {
                        cssPath = "/css/" + cssfile;
                    }

                    out.write("rtInitEditor('" + editor + "'," + hidden + ", '" + cssPath + "');\n");
                }
            }
        }
    }


    /**
     * Genererer Javascript som kalles etter redigering er ferdig (før submit)
     */
    public void generatePostJavascript() throws IOException {
        JspWriter out = pageContext.getOut();
        List attrlist  = content.getAttributes(attributeType);

        for (int i = 0; i < attrlist.size(); i++) {
            Attribute attr = (Attribute)attrlist.get(i);
            if (attr.isEditable() && !attr.isHidden(content)) {
                if (attr instanceof HtmltextAttribute) {
                    String inputField = AttributeHelper.getInputFieldName(attr.getName());
                    String editor = "editor_" + inputField;
                    String hidden  = "document.myform." + inputField;
                    out.write("if (!rtCopyValue('" + editor + "'," + hidden + ")) return;");
                }
            }
        }
    }


    /**
     * Lager inputskjermbilde ved å gå gjennom alle attributter
     */
    public void generateInputScreen() throws IOException, SystemException, ServletException {
        JspWriter out = pageContext.getOut();
        ServletRequest request = pageContext.getRequest();

        int tabIndex = 100; // Angir tabindex for å få cursor til å hoppe til rette felter
        List attrlist = content.getAttributes(attributeType);
        for (int i = 0; i < attrlist.size(); i++) {
            Attribute attr = (Attribute)attrlist.get(i);
            if (attr.isEditable() && !attr.isHidden(content)) {
                String value = attr.getValue();
                if (value == null || value.length() == 0) {
                    attr.setValue("");
                }

                // Skriver ut felt ved å inkludere JSP for hver attributt
                attr.setTabIndex(tabIndex);
                tabIndex += 10;

                request.setAttribute("content", content);
                request.setAttribute("attribute", attr);
                request.setAttribute("fieldName", AttributeHelper.getInputFieldName(attr.getName()));

                try {
                    out.print("\n<div class=\"contentAttribute\">\n");
                    pageContext.include("../../../../admin/publish/attributes/" + attr.getRenderer() +".jsp");
                    out.print("\n");
                    String helptext = attr.getHelpText();
                    if (helptext != null && helptext.length() > 0) {
                        out.print("<div class=\"helpText\">" + helptext + "</div>\n");
                    }
                    if (attr.inheritsFromAncestors()) {
                        String inheritText = LocaleLabels.getLabel("aksess.editcontent.inheritsfromancestors", Aksess.getDefaultAdminLocale());
                        out.print("<div class=\"helpText\">" + inheritText + "</div>\n");
                    }
                    out.print("</div>\n");
                } catch (Exception e) {
                    out.print("</div>\n");
                    Log.error(SOURCE, e, null, null);
                    String errorMessage = LocaleLabels.getLabel("aksess.editcontent.exception", Aksess.getDefaultAdminLocale());
                    out.print("<div class=\"errorText\">" + errorMessage + ":" + attr.getTitle() + "</div>\n");
                }
            }
        }
    }
}
