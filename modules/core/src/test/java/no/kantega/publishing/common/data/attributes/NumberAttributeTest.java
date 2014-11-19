package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.dom4j.dom.DOMElement;
import org.junit.Test;
import org.w3c.dom.*;

/**
 * Created by esphoe on 19/11/14.
 */
public class NumberAttributeTest {


    private String integerValue = "123";
    private String decimalvalue = "123,456";
    private String notANumber = "not a number";
    private String decimalPattern =  "^[\\-\\+]?(?:[0-9]+(?:[,][0-9]*)?)";

    @Test
    public void testDefaultIntegerValidation(){
        NumberAttribute attribute = new NumberAttribute();
        attribute.setName("testName");
        attribute.setValue(integerValue);

        // should validate ok
        ValidationErrors errors = new ValidationErrors();
        attribute.validate(errors);
        assert errors.getLength() == 0;

        // should not validate
        attribute.setValue(notANumber);
        errors = new ValidationErrors();
        attribute.validate(errors);
        assert errors.getLength() > 0;

    }

    @Test
    public void testCustomRegex() throws InvalidTemplateException {
        NumberAttribute attribute = new NumberAttribute();
        attribute.setName("testName");
        attribute.setValue(decimalvalue);
        attribute.setConfig(generateCustomRegexpConfig(), null);

        ValidationErrors errors = new ValidationErrors();
        attribute.validate(errors);

        // should validate ok
        assert errors.getLength() == 0;

        attribute.setValue(notANumber);
        errors = new ValidationErrors();
        attribute.validate(errors);

        assert errors.getLength() > 0;

    }

    private Element generateCustomRegexpConfig(){

        DOMElement element = new DOMElement("config");
        element.setAttribute("title", "config");
        element.setAttribute("regexp", decimalPattern);

        return element;

    }


}
