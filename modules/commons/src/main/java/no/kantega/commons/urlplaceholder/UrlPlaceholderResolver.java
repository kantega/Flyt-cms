package no.kantega.commons.urlplaceholder;

import javax.servlet.jsp.PageContext;

/**
 * Resolve a path like /css/$SITE/default.css to /css/internet/default.css,
 * based on the site the request is from.
 */
public interface UrlPlaceholderResolver {

    /**
     * @param url to process
     * @param pageContext of page
     * @return the url with placeholders like $SITE, $DEVICE and $LANGUAGE.
     */
    public String replaceMacros(String url, PageContext pageContext);
}
