package no.kantega.publishing.modules.forms.model;

import org.springframework.web.util.HtmlUtils;

/**
 *
 */
public class FormValue {
    private String name;
    private String[] values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValuesAsEscapedString() {
        return HtmlUtils.htmlEscape(getValuesAsString());
    }
    
    public String getValuesAsString() {
        StringBuffer sb = new StringBuffer();
        if (values != null) {
            for (String v : values) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(v);
            }
        }
        return sb.toString();
    }

    public void setValue(String value) {
        values = new String[] {value};
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}
