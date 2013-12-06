package no.kantega.commons.password;

import no.kantega.commons.client.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

public class PasswordValidator {
    private int minLength = 6;
    private int minDigits = 0;
    private int minLowerCase = 0;
    private int minUpperCase = 0;
    private int minNonAlphaNumeric = 0;

    public ValidationErrors isValidPassword(String password, String password2) {
        ValidationErrors errors = new ValidationErrors();

        Map<String, Object> params = new HashMap<>();

        if (password == null || !password.equals(password2)) {
            errors.add("password", "password.mismatch", params);
            return errors;
        }

        int digits = 0;
        int lowerCase = 0;
        int upperCase = 0;
        int nonAlphaNumeric = 0;

        int length = password.length();

        for (int i = 0; i < length; i++) {
            char c = password.charAt(i);

            if (Character.isDigit(c)) {
                digits++;
            } else if (Character.isLowerCase(c)) {
                lowerCase++;
            } else if (Character.isUpperCase(c)) {
                upperCase++;
            } else {
                nonAlphaNumeric++;
            }
        }

        if (length < minLength) {
            params.put("minlength", minLength);
            errors.add("password", "password.minlength", params);
        }

        if (digits < minDigits) {
            params.put("mindigits", minDigits);
            errors.add("password", "password.mindigits", params);
        }

        if (lowerCase < minLowerCase) {
            params.put("minlowercase", minLowerCase);
            errors.add("password", "password.minlowercase", params);
        }

        if (upperCase < minUpperCase) {
            params.put("minuppercase", minUpperCase);
            errors.add("password", "password.minuppercase", params);
        }

        if (nonAlphaNumeric < minNonAlphaNumeric) {
            params.put("minnonalpha", minNonAlphaNumeric);
            errors.add("password", "password.minnonalpha", params);
        }

        return errors;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMinDigits(int minDigits) {
        this.minDigits = minDigits;
    }

    public void setMinLowerCase(int minLowerCase) {
        this.minLowerCase = minLowerCase;
    }

    public void setMinUpperCase(int minUpperCase) {
        this.minUpperCase = minUpperCase;
    }

    public void setMinNonAlphaNumeric(int minNonAlphaNumeric) {
        this.minNonAlphaNumeric = minNonAlphaNumeric;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMinDigits() {
        return minDigits;
    }

    public int getMinLowerCase() {
        return minLowerCase;
    }

    public int getMinUpperCase() {
        return minUpperCase;
    }

    public int getMinNonAlphaNumeric() {
        return minNonAlphaNumeric;
    }
}
