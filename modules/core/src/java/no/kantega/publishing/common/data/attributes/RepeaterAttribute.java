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
public class RepeaterAttribute extends Attribute {
    Attribute parent = null;

    List<List<Attribute>> rows = new ArrayList();

    int minOccurs = 1;
    int maxOccurs = -1;


    public Iterator getIterator() {
        return rows.iterator();
    }

    public void addRow(List<Attribute> attributes) {
        rows.add(attributes);
    }

    public void addRow(int i, List<Attribute> attributes) {
        rows.add(i, attributes);
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
}
