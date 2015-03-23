package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.dom4j.dom.DOMElement;
import org.junit.Test;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;

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
        assertEquals("Validation should not contain errors", 0, errors.getLength());


        // should not validate
        attribute.setValue(notANumber);
        errors = new ValidationErrors();
        attribute.validate(errors);
        assertEquals("False validation occured!", 1, errors.getLength());

    }

    @Test
    public void testCustomRegex() throws InvalidTemplateException {
        NumberAttribute attribute = new NumberAttribute();
        attribute.setName("testName");
        attribute.setValue(decimalvalue);
        attribute.setConfig(generateCustomRegexpConfig(), null);

        // should validate ok
        ValidationErrors errors = new ValidationErrors();
        attribute.validate(errors);
        assertEquals("Validation should not contain errors", 0, errors.getLength());


        // should not validate
        attribute.setValue(notANumber);
        errors = new ValidationErrors();
        attribute.validate(errors);

        assertEquals("False validation occured!", 1, errors.getLength());

    }

    private Element generateCustomRegexpConfig(){

        DOMElement element = new DOMElement("config");
        element.setAttribute("title", "config");
        element.setAttribute("regexp", decimalPattern);

        return element;

    }


}
