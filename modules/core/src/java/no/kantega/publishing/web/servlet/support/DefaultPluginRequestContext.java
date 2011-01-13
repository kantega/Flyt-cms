package no.kantega.publishing.web.servlet.support;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.web.servlet.support.PluginRequestContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class DefaultPluginRequestContext implements PluginRequestContext {
    private final OpenAksessPlugin plugin;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;
    private final Map<String, Object> model;
    private final Locale locale;
    private boolean defaultHtmlEscape;

    public DefaultPluginRequestContext(OpenAksessPlugin plugin, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, Map<String, Object> model) {
        this.plugin = plugin;
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
        this.model = model;

        // Determine locale to use for this RequestContext.
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver != null) {
            // Try LocaleResolver (we're within a DispatcherServlet request).
            this.locale = localeResolver.resolveLocale(request);
        } else {
            // No LocaleResolver available -> try fallback.
            this.locale = getFallbackLocale();
        }

        // Determine default HTML escape setting from the "defaultHtmlEscape"
        // context-param in web.xml, if any.
        final Boolean esc = WebUtils.getDefaultHtmlEscape(servletContext);
        this.defaultHtmlEscape = esc == null ? false : esc;

    }

    protected Locale getFallbackLocale() {
        return request.getLocale();
    }

    public String getMessage(String code) {
        return getMessage(code, (Object[]) null, defaultHtmlEscape);
    }

    public String getMessage(String code, List args, boolean htmlEscape) {
        return getMessage(code, args == null ? null : args.toArray(new Object[args.size()]), htmlEscape);
    }

    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, defaultHtmlEscape);
    }

    public String getMessage(String code, List args) {
        return getMessage(code, args, defaultHtmlEscape);  
    }

    public String getMessage(String code, Object[] args, boolean htmlEscape) throws NoSuchMessageException {

        List<MessageSource> messageSources = new ArrayList<MessageSource>();

        // Some plugins might want to set a priority message source to be used before the
        // plugin's normal message sources. For instance, the plugin could be delegating
        // message lookup to it's own plugins.
        List<MessageSource> prioritySources = (List<MessageSource>) request.getAttribute(PRIORITY_MESSAGE_SOURCE_ATTR);
        if(prioritySources != null) {
            messageSources.addAll(prioritySources);
        }

        // Add the plugin's normal message sources
        messageSources.addAll(plugin.getMessageSources());

        for (MessageSource messageSource : messageSources) {

            try {
                String msg = messageSource.getMessage(code, args, this.locale);
                return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
            } catch (NoSuchMessageException e) {

            }
        }
        throw new NoSuchMessageException(code, this.locale);

    }
}
