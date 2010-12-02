package no.kantega.publishing.admin.taglib;

import no.kantega.commons.client.util.ValidationError;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.InputScreenRenderer;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.exception.InvalidTemplateException;

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

        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            Content content = (Content)request.getSession(true).getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

            try {
                if (attribute == null) {
                    if (type == null || type.length() < 2) {
                        type = "text";
                    }
                    type = type.substring(0, 1).toUpperCase() + type.substring(1, type.length()).toLowerCase();

                    attribute = (Attribute) Class.forName(Aksess.ATTRIBUTE_CLASS_PATH + type + "Attribute").newInstance();
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

                Map<String, List<ValidationError>> fieldErrors = new HashMap<String, List<ValidationError>>();
                ValidationErrors errors = (ValidationErrors)request.getAttribute("errors");
                if (errors != null) {
                    for (ValidationError error : errors.getErrors()) {
                        if (error.getField() != null && error.getField().length() > 0) {
                            List<ValidationError> errorsForField = fieldErrors.get(error.getField());
                            if (errorsForField == null) {
                                errorsForField = new ArrayList<ValidationError>();
                                fieldErrors.put(error.getField(), errorsForField);
                            }
                            errorsForField.add(error);
                        }
                    }
                }

                InputScreenRenderer renderer = new InputScreenRenderer(pageContext, content, AttributeDataType.CONTENT_DATA);
                renderer.renderAttribute(pageContext.getOut(), request, fieldErrors, attribute);

            } catch (IOException e) {
                throw new JspException(e);
            } catch (SystemException e) {
                throw new JspException(e);
            } catch (ClassNotFoundException e) {
                throw new JspException(e);
            } catch (InstantiationException e) {
                throw new JspException(e);
            } catch (IllegalAccessException e) {
                throw new JspException(e);
            } catch (InvalidFileException e) {
                throw new JspException(e);
            } catch (InvalidTemplateException e) {
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

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }


}
