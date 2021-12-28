package no.kantega.publishing.spring;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import java.util.Arrays;
import java.util.Locale;

/**
 *
 */
public class PluginMessageSource implements MessageSource {
    private final OpenAksessPlugin plugin;

    public PluginMessageSource(OpenAksessPlugin plugin) {
        this.plugin = plugin;
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        for(MessageSource messageSource : plugin.getMessageSources()) {
            try {
                return messageSource.getMessage(code, args, defaultMessage, locale);
            } catch (Exception e) {

            }
        }
        throw new NoSuchMessageException(code, locale);
    }

    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        for(MessageSource messageSource : plugin.getMessageSources()) {
            try {
                return messageSource.getMessage(code, args, locale);
            } catch (NoSuchMessageException e) {

            }

        }
        throw new NoSuchMessageException(code, locale);
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        for(MessageSource messageSource : plugin.getMessageSources()) {
            try {
                return messageSource.getMessage(resolvable, locale);
            } catch (NoSuchMessageException e) {
                
            }
        }
        throw new NoSuchMessageException(Arrays.toString(resolvable.getCodes()), locale);
    }
}
