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

import no.kantega.commons.client.util.ValidationError;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.content.util.AttributeHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.cache.MetadataTemplateCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.AttributeHandler;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;
import no.kantega.publishing.common.data.attributes.SeparatorAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class InputScreenRenderer {
    private static final Logger log = LoggerFactory.getLogger(InputScreenRenderer.class);

    private PageContext pageContext = null;
    private Content content = null;
    private int attributeType = -1;
    private boolean hiddenAttributes = false;

    public InputScreenRenderer(PageContext pageContext, Content content, int attributeType) {
        this.pageContext = pageContext;
        this.content  = content;
        this.attributeType = attributeType;
    }


    /**
     * Lager inputskjermbilde ved å gå gjennom alle attributter
     */
    public void generateInputScreen() throws IOException, SystemException, ServletException {
        JspWriter out = pageContext.getOut();
        ServletRequest request = pageContext.getRequest();

        Map<String, List<ValidationError>> fieldErrors = new HashMap<>();
        ValidationErrors errors = (ValidationErrors)request.getAttribute("errors");
        if (errors != null) {
            for (ValidationError error : errors.getErrors()) {
                if (error.getField() != null && error.getField().length() > 0) {
                    List<ValidationError> errorsForField = fieldErrors.get(error.getField());
                    if (errorsForField == null) {
                        errorsForField = new ArrayList<>();
                        fieldErrors.put(error.getField(), errorsForField);
                    }
                    errorsForField.add(error);
                }
            }
        }

        ContentTemplate template = null;
        if (attributeType == AttributeDataType.CONTENT_DATA) {
            template = ContentTemplateCache.getTemplateById(content.getContentTemplateId(), true);
        } else if (attributeType == AttributeDataType.META_DATA && content.getMetaDataTemplateId() > 0) {
            template = MetadataTemplateCache.getTemplateById(content.getContentTemplateId(), true);
        }

        String globalHelpText = null;
        if (template != null) {
            globalHelpText = template.getHelptext();
        }

        if (globalHelpText != null && globalHelpText.length() > 0) {
            out.print("<div id=\"TemplateGlobalHelpText\" class=\"ui-state-highlight\">" + globalHelpText + "</div>");
        }

        request.setAttribute("content", content);

        int tabIndex = 100; // Tab index for attribute
        List<Attribute> attributes = content.getAttributes(attributeType);
        for (Attribute attribute : attributes) {
            tabIndex = renderAttribute(out, request, fieldErrors, attribute, tabIndex);
        }
    }

    private int renderAttribute(JspWriter out, ServletRequest request, Map<String, List<ValidationError>> fieldErrors, Attribute attribute, int tabIndex) throws IOException {
        if (attribute.isEditable() && !attribute.isHidden(content) && roleCanEdit(attribute, request)) {
            String value = attribute.getValue();
            if (value == null || value.length() == 0) {
                attribute.setValue("");
            }

            if (attribute instanceof RepeaterAttribute) {
                renderRepeaterAttribute(out, request, fieldErrors, (RepeaterAttribute) attribute, tabIndex);
            } else if(attribute instanceof SeparatorAttribute){
                renderSeparatorAttribute(out, (SeparatorAttribute) attribute);
            } else {
                tabIndex += 10;
                attribute.setTabIndex(tabIndex);
                renderNormalAttribute(out, request, fieldErrors, attribute);
            }
        }
        return tabIndex;
    }

    private int renderRepeaterAttribute(JspWriter out, ServletRequest request, Map<String, List<ValidationError>> fieldErrors, RepeaterAttribute repeaterAttribute, Integer tabIndex) throws IOException {

        try {
            out.print("\n<div class=\"contentAttributeRepeater");
            if (attributeIsHiddenEmpty(repeaterAttribute)) {
                this.hiddenAttributes = true;
                out.print(" attributeHiddenEmpty");
            }
            out.print("\" id=\"" + AttributeHelper.getInputContainerName(repeaterAttribute.getNameIncludingPath()) + "\" data-title=\"" + repeaterAttribute.getTitle() + "\"\n>\n");
            request.setAttribute("repeater", repeaterAttribute);
            request.setAttribute("repeaterFieldName", AttributeHelper.getInputFieldName(repeaterAttribute.getNameIncludingPath()));

            pageContext.include("/admin/publish/attributes/repeater_start.jsp");

            int numberOfRows = repeaterAttribute.getNumberOfRows();

            for (int rowNo = 0; rowNo < numberOfRows; rowNo++) {
                out.print("<div class=\"contentAttributeRepeaterRow\">\n");
                request.setAttribute("repeaterRowNo", rowNo);
                pageContext.include("/admin/publish/attributes/repeater_row_start.jsp");
                List<Attribute> attributes = repeaterAttribute.getRow(rowNo);
                for (Attribute attribute : attributes) {
                    tabIndex = renderAttribute(out, request, fieldErrors, attribute, tabIndex);
                }
                pageContext.include("/admin/publish/attributes/repeater_row_end.jsp");
                out.print("</div>\n");
            }
            pageContext.include("/admin/publish/attributes/repeater_end.jsp");
            out.print("</div>");
        } catch (Exception e) {
            log.error("", e);
            String errorMessage = LocaleLabels.getLabel("aksess.editcontent.exception", Aksess.getDefaultAdminLocale());
            out.print("<div class=\"errorText\">" + errorMessage + ":" + repeaterAttribute.getTitle() + "</div>\n");
        }

        return tabIndex;
    }

    private void renderSeparatorAttribute(JspWriter out, SeparatorAttribute separatorAttribute) throws IOException {


        StringBuilder output = new StringBuilder();
        output.append("\n<div class=\"separator\">");
        output.append("\n<h2 class=\"separator_heading\">").append(separatorAttribute.getName()).append("</h2>\n");
        if (separatorAttribute.getHelpText() != null && separatorAttribute.getHelpText().length() > 0){
            output.append("<div class=\"separator_description\">")
                    .append(separatorAttribute.getHelpText())
                    .append("</div>");
        }
        output.append("</div>");

        out.print(output);
    }


    public void renderNormalAttribute(JspWriter out, ServletRequest request, Map<String, List<ValidationError>> fieldErrors, Attribute attr) throws IOException {
        request.setAttribute("attribute", attr);
        request.setAttribute("fieldName", AttributeHelper.getInputFieldName(attr.getNameIncludingPath()));

        try {
            out.print("\n<div class=\"contentAttribute");

            if (fieldErrors.get(attr.getName()) != null) {
                out.print(" error");
            }

            if (attributeIsHiddenEmpty(attr)) {
                this.hiddenAttributes = true;
                out.print(" attributeHiddenEmpty");
            }

            out.print("\" id=\"" + AttributeHelper.getInputContainerName(attr.getNameIncludingPath()) + "\" data-title=\"" + attr.getTitle() + "\">\n");

            out.print("<div class=\"heading\">" + attr.getTitle());
            if (attr.isMandatory()) {
                out.print("<span class=\"mandatory\">*</span>");
            }
            out.print("</div>");
            String helptext = attr.getHelpText();
            if (helptext != null && helptext.length() > 0) {
                out.print("<div class=\"ui-state-highlight\">" + helptext + "</div>\n");
            }
            String script = attr.getScript();
            if (script != null && script.length() > 0) {
                out.print("<script type=\"text/javascript\">\n" + script+ "\n</script>\n");
            }
            if (attr.inheritsFromAncestors()) {
                String inheritText = LocaleLabels.getLabel("aksess.editcontent.inheritsfromancestors", Aksess.getDefaultAdminLocale());
                out.print("<div class=\"ui-state-highlight\">" + inheritText + "</div>\n");
            }
            pageContext.include("/admin/publish/attributes/" + attr.getRenderer() +".jsp");
            out.print("\n");
            out.print("</div>\n");
        } catch (Exception e) {
            out.print("</div>\n");
            log.error("", e);
            String errorMessage = LocaleLabels.getLabel("aksess.editcontent.exception", Aksess.getDefaultAdminLocale());
            out.print("<div class=\"errorText\">" + errorMessage + ":" + attr.getTitle() + "</div>\n");
        }
    }

    private boolean roleCanEdit(Attribute attr, ServletRequest request) {
        String[] roles = attr.getEditableByRoles();
        return !(roles != null && roles.length > 0)
                || SecuritySession.getInstance((HttpServletRequest) request).isUserInRole(roles);

    }

    private boolean attributeIsHiddenEmpty(Attribute attribute) {
        AttributeIsEmptyHandler attributeHandler = new AttributeIsEmptyHandler();
        content.doForAttribute(attributeHandler, attribute);

        return attributeHandler.hasOnlyEmptyValues && attribute.isHideIfEmpty();
    }

    public boolean hasHiddenAttributes() {
        return hiddenAttributes;
    }

    private static class AttributeIsEmptyHandler implements AttributeHandler{
        private boolean hasOnlyEmptyValues = true;


        @Override
        public void handleAttribute(Attribute attribute) {
            if (!isBlank(attribute.getValue())) {
                hasOnlyEmptyValues = false;
            }
        }

        boolean isHasOnlyEmptyValues() {
            return hasOnlyEmptyValues;
        }
    }
}
