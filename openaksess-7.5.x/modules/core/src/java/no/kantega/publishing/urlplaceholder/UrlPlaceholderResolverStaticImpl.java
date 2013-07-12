package no.kantega.publishing.urlplaceholder;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.urlplaceholder.UrlPlaceholderResolver;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;

import javax.servlet.jsp.PageContext;

public class UrlPlaceholderResolverStaticImpl implements UrlPlaceholderResolver {
    public String replaceMacros(String url, PageContext pageContext) {
        try {
            return AttributeTagHelper.replaceMacros(url, pageContext);
        } catch (NotAuthorizedException e) {
            return url;
        }
    }
}
