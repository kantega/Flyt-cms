package no.kantega.publishing.modules.forms.validate;

import no.kantega.publishing.api.forms.model.DefaultFormValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FormElementNorwPhoneNumValidatorTest {


    @Test
    public void shouldAcceptNorwegianPhoneNumbers() throws Exception {
        String[] args = null;
        List<FormError> formErrors = new ArrayList<>();
        FormElementNorwPhoneNumValidator validator = new FormElementNorwPhoneNumValidator();

        DefaultFormValue formValue0 = new DefaultFormValue();
        formValue0.setName("Telephone");
        formValue0.setValue("+4712345678");
        validator.validate(formValue0, 0, args, formErrors);
        assertEquals(0, formErrors.size());

        DefaultFormValue formValue1 = new DefaultFormValue();
        formValue1.setName("Telephone");
        formValue1.setValue("004712345678");
        validator.validate(formValue1, 1, args, formErrors);
        assertEquals(0, formErrors.size());

        DefaultFormValue formValue2 = new DefaultFormValue();
        formValue2.setName("Telephone");
        formValue2.setValue("4712345678");
        validator.validate(formValue2, 2, args, formErrors);
        assertEquals(0, formErrors.size());

        DefaultFormValue formValue3 = new DefaultFormValue();
        formValue3.setName("Telephone");
        formValue3.setValue("12345678");
        validator.validate(formValue3, 3, args, formErrors);
        assertEquals(0, formErrors.size());

        DefaultFormValue formValue4 = new DefaultFormValue();
        formValue4.setName("Telephone");
        formValue4.setValue("12 34 56 78");
        validator.validate(formValue4, 4, args, formErrors);
        assertEquals(0, formErrors.size());

        DefaultFormValue formValue5 = new DefaultFormValue();
        formValue5.setName("Telephone");
        formValue5.setValue("123 45 678");
        validator.validate(formValue5, 5, args, formErrors);
        assertEquals(0, formErrors.size());

        DefaultFormValue formValue6 = new DefaultFormValue();
        formValue6.setName("Telephone");
        formValue6.setValue("123-45-678"); // Accepted at the moment
        validator.validate(formValue6, 6, args, formErrors);
        assertEquals(0, formErrors.size());

    }

    @Test
    public void shouldNotAcceptAmericanPhoneNumbers() throws Exception {
        String[] args = null;
        List<FormError> formErrors = new ArrayList<>();
        FormElementNorwPhoneNumValidator validator = new FormElementNorwPhoneNumValidator();

        DefaultFormValue formValue0 = new DefaultFormValue();
        formValue0.setName("Telephone");
        formValue0.setValue("+1 202 555 0196");
        validator.validate(formValue0, 0, args, formErrors);
        assertEquals(1, formErrors.size());

        DefaultFormValue formValue1 = new DefaultFormValue();
        formValue1.setName("Telephone");
        formValue1.setValue("+1-202-555-0196");
        validator.validate(formValue1, 0, args, formErrors);
        assertEquals(2, formErrors.size());
    }

    @Test
    public void shouldNotAcceptPhoneNumbersWithNonNumerals() throws Exception {
        String[] args = null;
        List<FormError> formErrors = new ArrayList<>();
        FormElementNorwPhoneNumValidator validator = new FormElementNorwPhoneNumValidator();

        DefaultFormValue formValue0 = new DefaultFormValue();
        formValue0.setName("Telephone");
        formValue0.setValue("+1 (202) 555-0196");
        validator.validate(formValue0, 0, args, formErrors);
        assertEquals(1, formErrors.size());

        DefaultFormValue formValue1 = new DefaultFormValue();
        formValue1.setName("Telephone");
        formValue1.setValue("+1-abc-555-0196");
        validator.validate(formValue1, 1, args, formErrors);
        assertEquals(2, formErrors.size());
    }
}