package no.kantega.publishing.spring;

import no.kantega.publishing.common.Aksess;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 *
 */
public class AksessAdminLocaleResolver implements LocaleResolver {
    public Locale resolveLocale(HttpServletRequest request) {
        return Aksess.getDefaultAdminLocale();
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException();
    }
}
