package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
* A RepeaterAttribute is a composite Attribute, used to make repeatable rows with attributes
*/
public class RepeaterAttribute extends Attribute implements Iterable<List<Attribute>> {

    List<List<Attribute>> rows = new ArrayList<>();

    int minOccurs = 0;
    int maxOccurs = Integer.MAX_VALUE;

    public RepeaterAttribute() {
        super();
    }

    public RepeaterAttribute(String name, List<List<Attribute>> values) {
        setName(name);
        rows = values;
        for (List<Attribute> attributeRow : rows) {
            setParent(attributeRow);
        }
    }

    /**
     *
     * @deprecated use {@code iterator} or for(List&lt;Attribute&gt; attribues : repeater)
     */
    @Deprecated
    public Iterator<List<Attribute>> getIterator() {
        return iterator();
    }

    public void addRow(List<Attribute> attributes) {
        rows.add(attributes);
        setParent(attributes);
    }

    public void addRow(int i, List<Attribute> attributes) {
        rows.add(i, attributes);
        setParent(attributes);
    }

    private void setParent(List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            attribute.setParent(this);
        }
    }

    public void setRows(List<List<Attribute>> attributes){
        rows = attributes;
        for (List<Attribute> attributeRow : attributes) {
            setParent(attributeRow);
        }
    }

    public List<Attribute> getRow(int i) {
        return rows.get(i);
    }

    public void removeRow(int i) {
        rows.remove(i);
    }

    public int getNumberOfRows() {
        return rows.size();
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);
        if (config != null) {
            String strMinOccurs = config.getAttribute("minoccurs");
            if (strMinOccurs != null && strMinOccurs.length() > 0) {
                minOccurs = Integer.parseInt(strMinOccurs);
            }
            String strMaxOccurs = config.getAttribute("maxoccurs");
            if (strMaxOccurs != null && strMaxOccurs.length() > 0) {
                maxOccurs = Integer.parseInt(strMaxOccurs);
                if (maxOccurs < minOccurs) {
                    maxOccurs = minOccurs;
                }
            }
        }
    }

    public int getOffset(Attribute a) {
        for (int i = 0; i < rows.size(); i++) {
            List<Attribute> row = rows.get(i);
            for (Attribute o : row) {
                if (o == a) {
                    return i;
                }
            }
        }

        return 0;
    }

    public String getRenderer() {
        return "repeater";
    }

    /**
     * A repeaterattribute is supposed to be iterated over, either with RepeatAttributesTag or through .getIterator,
     * this method does return the string representation of its rows.
     * The reason for this overrided method is that for instance the aksess:exists-tag fetches the attribute and checks
     * whether the attribute string value has a non-blank value.
     * @return either Repeaterattribute with {n} rows, or super.getValue() which most likely is null.
     */
    @Override
    public String getValue() {
        int size = rows.size();
        if(size > 0){
            return "Repeaterattribute with " + size + " rows";
        }
        return super.getValue();
    }

    @Override
    public Iterator<List<Attribute>> iterator() {
        return rows.iterator();
    }
}
