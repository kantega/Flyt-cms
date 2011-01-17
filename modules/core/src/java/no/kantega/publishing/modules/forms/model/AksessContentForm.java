package no.kantega.publishing.modules.forms.model;

import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.api.forms.model.DefaultForm;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.EditableformAttribute;
import no.kantega.publishing.common.data.attributes.EmailAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.modules.forms.filter.GetFormFieldsFilter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class AksessContentForm extends DefaultForm {

    public AksessContentForm(Form form) {
        setId(form.getId());
        setTitle(form.getTitle());
        setEmail(form.getEmail());
        setFormDefinition(form.getFormDefinition());
        setFieldNames(getFieldNamesFromDefinition(form.getFormDefinition()));

    }

    public AksessContentForm(Content content) {
        List<Attribute> attributes = content.getAttributes(AttributeDataType.CONTENT_DATA);
        for (Attribute attr : attributes) {
            if (attr instanceof EmailAttribute) {
                setEmail(attr.getValue());
            }
            if (attr instanceof EditableformAttribute) {
                setFormDefinition(attr.getValue());
                setFieldNames(getFieldNamesFromDefinition(attr.getValue()));
            }
        }
        setId(content.getId());
        setTitle(content.getTitle());
    }

    private List<String> getFieldNamesFromDefinition(String formDefinition) {
        FilterPipeline pipeline = new FilterPipeline();

        GetFormFieldsFilter filter = new GetFormFieldsFilter();

        pipeline.addFilter(filter);

        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(formDefinition), sw);

        return filter.getFieldNames();
    }


}
