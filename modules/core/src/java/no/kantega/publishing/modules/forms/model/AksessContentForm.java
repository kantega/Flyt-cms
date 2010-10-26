package no.kantega.publishing.modules.forms.model;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.EditableformAttribute;
import no.kantega.publishing.common.data.attributes.EmailAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;

import java.util.List;

public class AksessContentForm implements Form {

    private Content content;

    public AksessContentForm(Content content) {
        this.content = content;
    }

    public int getId() {
        if (content != null) {
            return content.getId();
        } else {
            return -1;
        }
    }

    public String getTitle() {
        if (content != null) {
            return content.getTitle();
        } else {
            return null;
        }
    }

    public String getFormDefinition() {
        if (content != null) {
            List attributes = content.getAttributes(AttributeDataType.CONTENT_DATA);
            for (int i = 0; i < attributes.size(); i++) {
                Attribute attr = (Attribute)attributes.get(i);
                if (attr instanceof EditableformAttribute) {
                    return attr.getValue();
                }
            }
        }
        return null;
    }

    public String getEmail() {
        if (content != null) {
            List attributes = content.getAttributes(AttributeDataType.CONTENT_DATA);
            for (int i = 0; i < attributes.size(); i++) {
                Attribute attr = (Attribute)attributes.get(i);
                if (attr instanceof EmailAttribute) {
                    return attr.getValue();
                }
            }
        }
        return null;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
