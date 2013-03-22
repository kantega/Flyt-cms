package no.kantega.publishing.modules.forms.validate;

import no.kantega.publishing.api.forms.model.FormValue;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


public class FormElementDateValidator implements FormElementValidator {
    private static String id = "date";

    public String getId() {
        return id;
    }

    public List<FormError> validate(FormValue formValue, int currentFieldIndex, String[] args, List<FormError> formErrors) {
        String value = formValue.getValues()[0];
        if (value != null && 0 < value.length()) {
            String dateFormat = args.length > 0 ? args[0] : "dd.MM.yyyy";
            DateFormat df = new SimpleDateFormat(dateFormat);
            try {
                if(!value.equals(df.format(df.parse(value)))) {
                    formErrors.add(new FormError(formValue.getName(), currentFieldIndex, "aksess.formerror.date"));
                }
            } catch (ParseException e) {
                formErrors.add(new FormError(formValue.getName(), currentFieldIndex, "aksess.formerror.date"));
            }
        }
        return formErrors;
    }
}