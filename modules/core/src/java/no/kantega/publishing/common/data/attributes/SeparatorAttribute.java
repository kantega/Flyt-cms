package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.content.behaviours.attributes.PersistAttributeBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.SkipPersistAttributeBehaviour;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
* A RepeaterAttribute is a composite Attribute, used to make repeatable rows with attributes
*/
public class SeparatorAttribute extends Attribute {



    public SeparatorAttribute() {
        super();
    }


    private void setParent(List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            attribute.setParent(this);
        }
    }

    public String getRenderer() {
        return "separator";
    }

    @Override
    public PersistAttributeBehaviour getSaveBehaviour() {
        return new SkipPersistAttributeBehaviour();
    }
}
