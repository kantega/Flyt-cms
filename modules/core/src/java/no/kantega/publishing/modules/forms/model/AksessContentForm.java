package no.kantega.publishing.modules.forms.model;

import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.api.forms.model.DefaultForm;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.EditableformAttribute;
import no.kantega.publishing.common.data.attributes.EmailAttribute;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;
import no.kantega.publishing.modules.forms.filter.GetFormFieldsFilter;

import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AksessContentForm extends DefaultForm {

    public AksessContentForm(Form form) {
        setId(form.getId());
        setTitle(form.getTitle());
        setEmail(form.getEmail());
        setFormDefinition(form.getFormDefinition());
        setFieldNames(getFieldNamesFromDefinition(form.getFormDefinition()));
        setUrl(form.getUrl());
    }

    public AksessContentForm(Content content) {
        List<Attribute> attributes = content.getAttributes(AttributeDataType.CONTENT_DATA);
        for (Attribute attr : attributes) {
            if (isBlank(getEmail())) {
                String email = "";
                if (attr instanceof RepeaterAttribute) {
                    email = getEmailFromRepeater((RepeaterAttribute) attr, email);
                }

                if (isBlank(email) && attr instanceof EmailAttribute) {
                    email = attr.getValue();
                }

                if (!isBlank(email)) {
                    setEmail(email);
                }
            }

            if (attr instanceof EditableformAttribute) {
                setFormDefinition(attr.getValue());
                setFieldNames(getFieldNamesFromDefinition(attr.getValue()));
            }
        }
        setId(content.getId());
        setTitle(content.getTitle());
        setUrl(content.getUrl());
    }

    private String getEmailFromRepeater(RepeaterAttribute repeaterAttribute, String email) {
        StringBuilder emailBuilder = new StringBuilder();
        Iterator<List<Attribute>> it =  repeaterAttribute.getIterator();
        while(it.hasNext()) {
            for (Attribute a : it.next()) {
                if (a instanceof EmailAttribute) {
                    if (!isBlank(email)) {
                        emailBuilder.append(",");
                    }
                    emailBuilder.append(a.getValue());
                }
            }
        }
        return emailBuilder.toString();
    }

    private List<String> getFieldNamesFromDefinition(String formDefinition) {
        FilterPipeline pipeline = new FilterPipeline();

        GetFormFieldsFilter filter = new GetFormFieldsFilter();

        pipeline.addFilter(filter);

        pipeline.filter(formDefinition);

        return filter.getFieldNames();
    }
}
