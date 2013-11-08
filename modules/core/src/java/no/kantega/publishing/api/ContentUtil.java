package no.kantega.publishing.api;

import no.kantega.publishing.common.data.Content;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

public abstract class ContentUtil {
    public static Content tryGetFromRequest(ServletRequest request){
        return (Content)request.getAttribute("aksess_this");
    }

    public static Content tryGetFromPageContext(PageContext pageContext){
        return tryGetFromRequest(pageContext.getRequest());
    }
}
