package no.kantega.publishing.modules.forms.filter;

import no.kantega.commons.xmlfilter.Filter;
import no.kantega.publishing.modules.forms.validate.FormError;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

/**
 *  Prefills HTML form with values from hashmap
 *
 *  NOTE! This filter cannot be run twice in a row
 */
public class FormFillFilter implements Filter {
    private Map<String, String[]> params;
    private List<FormError> errors;
    private int currentFieldIndex;

    public FormFillFilter(Map<String, String[]> params, List<FormError> errors) {
        this.params = params;
        this.errors = errors;
        this.currentFieldIndex = 0;
    }


    @Override
    public Document runFilter(Document document) {
        for (Element div : document.getElementsByTag("div")) {
            if (div.attr("class").contains("formElement")) {
                currentFieldIndex++;
                boolean hasError = false;
                for (FormError error : errors) {
                    if (error.getIndex() == currentFieldIndex) {
                        hasError = true;
                        break;
                    }
                }
                if (hasError) {
                    div.addClass("error");
                }
            }
        }

        for (Element input : document.getElementsByTag("input")) {
            String inputType = input.attr("type");
            String inputName = input.attr("name");
            if ("text".equals(inputType) || "hidden".equals(inputType)) {
                String[] values = params.get(inputName);
                String value = null;
                if (values != null && values.length > 0) {
                    value = values[0];
                }
                if (value != null) {
                    value = HtmlUtils.htmlEscape(value);
                    input.attr("value", value);
                }

            } else if ("radio".equals(inputType) || "checkbox".equals(inputType)) {
                String inputValue = input.attr("value");
                String[] values = params.get(inputName);
                String value = "";
                if (inputValue != null && values != null) {
                    for (String v : values) {
                        if (inputValue.equals(v)) {
                            value = v;
                            break;
                        }
                    }
                }
                input.attr("type", inputType);
                input.attr("name", inputName);
                input.attr("value", inputValue);
                if (value.equals(inputValue)) {
                    input.attr("checked", "checked");
                } else {
                    input.removeAttr("checked");
                }
            }
        }

        for (Element select : document.getElementsByTag("select")) {
            String currentSelectList = select.attr("name");
            for (Element option : select.getElementsByTag("option")) {
                String inputValue = option.attr("value");
                String[] values = params.get(currentSelectList);
                String value = null;
                if (values != null && values.length > 0) {
                    value = values[0];
                }
                if (value != null) {
                    option.attr("value", inputValue);
                    if (value.equals(inputValue)) {
                        option.attr("selected", "selected");
                    } else {
                        option.removeAttr("selected");
                    }
                }
            }

        }
        for (Element textarea : document.getElementsByTag("textarea")) {
            String inputName = textarea.attr("name");
            String[] values = params.get(inputName);
            String value = null;
            if (values != null && values.length > 0) {
                value = values[0];
            }
            if (value != null) {
                value = HtmlUtils.htmlEscape(value);
                textarea.text(value);
            }
        }
        return document;
    }
}
