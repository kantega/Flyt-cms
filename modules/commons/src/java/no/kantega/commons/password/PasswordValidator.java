package no.kantega.commons.password;

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.security.api.common.SystemException;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.password.PasswordManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class PasswordValidator implements ApplicationContextAware {
    private int minLength = 6;
    private int minDigits = 0;
    private int minLowerCase = 0;
    private int minUpperCase = 0;
    private int minNonAlphaNumeric = 0;
    private boolean allowUsernameInPassword = false;
    private boolean allowSameAsPreviousPassword = false;

    private String passwordManagerName;
    private Map<String, PasswordManager> passwordManagers;

    public ValidationErrors isValidPassword(String password, String password2, Identity identity) {
        ValidationErrors errors = new ValidationErrors();

        if (!Objects.equals(password, password2)) {
            errors.add("password", "password.mismatch", Collections.<String, Object>emptyMap());
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
            errors.add("password", "password.minlength", Collections.<String, Object>singletonMap("minlength", minLength));
        }

        if (digits < minDigits) {
            errors.add("password", "password.mindigits", Collections.<String, Object>singletonMap("mindigits", minDigits));
        }

        if (lowerCase < minLowerCase) {
            errors.add("password", "password.minlowercase", Collections.<String, Object>singletonMap("minlowercase", minLowerCase));
        }

        if (upperCase < minUpperCase) {
            errors.add("password", "password.minuppercase", Collections.<String, Object>singletonMap("minuppercase", minUpperCase));
        }

        if (nonAlphaNumeric < minNonAlphaNumeric) {
            errors.add("password", "password.minnonalpha", Collections.<String, Object>singletonMap("minnonalpha", minNonAlphaNumeric));
        }

        if(!allowUsernameInPassword && password.contains(identity.getUserId())){
            errors.add("password", "password.usernameinpassword", Collections.<String, Object>singletonMap("username", identity.getUserId()));
        }

        if(!allowSameAsPreviousPassword && passwordMatchesExisting(identity, password)){
            errors.add("password", "password.matchesPrevious", Collections.<String, Object>emptyMap());

        }

        return errors;
    }

    private boolean passwordMatchesExisting(Identity identity, String password) {
        PasswordManager passwordManager = passwordManagers.get(passwordManagerName);
        if(passwordManager == null){
            throw new IllegalStateException("Passwordmanager with name " + passwordManagerName + " not found!");
        }
        try {
            return passwordManager.verifyPassword(identity, password);
        } catch (SystemException e) {
            throw new IllegalStateException("Error verifying password", e);

        }
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

    public void setAllowUsernameInPassword(boolean allowUsernameInPassword) {
        this.allowUsernameInPassword = allowUsernameInPassword;
    }

    public void setAllowSameAsPreviousPassword(boolean allowSameAsPreviousPassword) {
        this.allowSameAsPreviousPassword = allowSameAsPreviousPassword;
    }

    public void setPasswordManagerName(String passwordManagerName) {
        this.passwordManagerName = passwordManagerName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        passwordManagers = applicationContext.getBeansOfType(PasswordManager.class);
    }
}
