package no.kantega.publishing.admin.taglib;

import no.kantega.commons.client.util.ValidationError;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.InputScreenRenderer;
import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.factory.AttributeFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderAttributeTag extends TagSupport {
    private String type;
    private String value;
    private String name;
    private String title;
    private String titlekey;
    private String helpText;
    private int maxLength = -1;
    private Attribute attribute;
    private AttributeFactory attributeFactory;

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitlekey(String titlekey) {
        this.titlekey = titlekey;
    }

    public void setHelptext(String helpText) {
        this.helpText = helpText;
    }

    public void setMaxlength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public int doStartTag() throws JspException {
        setAttributeFactoryIfNull();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        Content content = (Content)request.getSession(true).getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

        try {
            if (attribute == null) {
                attribute = attributeFactory.newAttribute(type);
            }
            if (value != null && value.length() > 0) {
                attribute.setValue(value);
            }
            attribute.setName(name);
            if (titlekey != null) {
                title = LocaleLabels.getLabel(titlekey, Aksess.getDefaultAdminLocale());
            }
            if (title != null) {
                attribute.setTitle(title);
            }
            if (helpText != null) {
                attribute.setHelpText(helpText);
            }
            if (maxLength != -1) {
                attribute.setMaxLength(maxLength);
            }

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

            InputScreenRenderer renderer = new InputScreenRenderer(pageContext, content, AttributeDataType.CONTENT_DATA);
            renderer.renderNormalAttribute(pageContext.getOut(), request, fieldErrors, attribute);

        } catch (IOException | SystemException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new JspException(e);
        } finally {
            value = null;
            helpText = null;
            title = null;
            name = null;
            type = null;
            titlekey = null;
            maxLength = -1;
            attribute = null;
        }


        return SKIP_BODY;
    }

    private void setAttributeFactoryIfNull() {
        if (attributeFactory == null){
            attributeFactory = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean("aksessAttributeFactory", AttributeFactory.class);
        }
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }


}
