/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.kantega.publishing.modules.forms.validate;

import java.util.ArrayList;
import java.util.List;
import no.kantega.publishing.modules.forms.model.FormValue;

public class FormElementNorwegianSsnValidator implements FormElementValidator {

    private static String id = "norwegian-ssn";
    private static String ssnRegex = "^(0[1-9]|[12]\\d|3[01])(0[1-9]|1[0-2])\\d{2}\\d{5}$";

    public String getId() {
        return id;
    }

    public List<FormError> validate(FormValue formValue, List<FormError> formErrors) {
        String value = formValue.getValues()[0];
        if (value != null && 0 < value.length()) {
            boolean valid = value.matches(ssnRegex);

            if (valid) {
                // sjekk kontrollsifre
                int[] w1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
                int[] w2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
                int[] p = new int[11];
                int k1 = 0;
                int k2 = 0;

                if (p[0] <= 3 && p[2] <= 1) {
                    for (int i = 0; i < value.length(); i++) {
                        p[i] = Integer.parseInt(value.substring(i, i + 1));
                    }

                    for (int i = 0; i < w1.length; i++) {
                        k1 += w1[i] * p[i];
                    }
                    k1 = k1 % 11 != 0 ? 11 - (k1 % 11) : 0;

                    for (int i = 0; i < w2.length; i++) {
                        k2 += w2[i] * p[i];
                    }
                    k2 = k2 % 11 != 0 ? 11 - (k2 % 11) : 0;

                    valid = (k1 == p[9] && k2 == p[10]);
                } else {
                    // D- eller H-nummer
                }
            }
            if (!valid) {
                formErrors.add(new FormError(id, "Invalid email"));
            }
        } else {
            formErrors.add(new FormError(id, "Invalid email"));
        }

        return formErrors;
    }
}
