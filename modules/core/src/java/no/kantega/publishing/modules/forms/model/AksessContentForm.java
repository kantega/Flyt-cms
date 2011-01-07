package no.kantega.publishing.modules.forms.model;

import no.kantega.publishing.api.forms.model.DefaultForm;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.EditableformAttribute;
import no.kantega.publishing.common.data.attributes.EmailAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;

import java.util.List;

public class AksessContentForm extends DefaultForm {

    public AksessContentForm(Form form) {
        setId(form.getId());
        setTitle(form.getTitle());
        setEmail(form.getEmail());
        setFormDefinition(form.getFormDefinition());
    }

    public AksessContentForm(Content content) {
        List<Attribute> attributes = content.getAttributes(AttributeDataType.CONTENT_DATA);
        for (Attribute attr : attributes) {
            if (attr instanceof EmailAttribute) {
                setEmail(attr.getValue());
            }
            if (attr instanceof EditableformAttribute) {
                setFormDefinition(attr.getValue());
            }
        }
        setId(content.getId());
        setTitle(content.getTitle());
    }
}
