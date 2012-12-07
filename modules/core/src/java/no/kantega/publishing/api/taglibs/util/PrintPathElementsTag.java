package no.kantega.publishing.api.taglibs.util;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.api.path.PathEntryAO;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PrintPathElementsTag extends TagSupport {
    private final String defaultCssClass = "contentPath";
    private final String defaultSeparator = "&gt;";

    private Integer associationId;
    private String cssClass = defaultCssClass;
    private String separator = defaultSeparator;
    private PathEntryAO pathEntryAO;

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
        pathEntryAO = context.getBean(PathEntryAO.class);
    }

    @Override
    public int doStartTag() throws JspException {
        if(associationId != null){
            try {
                ContentIdentifier contentIdentifier = ContentIdentifier.fromAssociationId(associationId);
                List<PathEntry> pathByContentId = pathEntryAO.getPathEntriesByContentIdentifier(contentIdentifier);
                JspWriter writer = pageContext.getOut();

                writer.write("<div class=\"");
                writer.write(cssClass);
                writer.write("\">");

                for (Iterator<PathEntry> iterator = pathByContentId.iterator(); iterator.hasNext(); ) {
                    PathEntry pathEntry = iterator.next();
                    writer.write("<span>");
                    writer.write(pathEntry.getTitle());
                    writer.write("</span>");
                    if(iterator.hasNext()){
                        writer.write(separator);
                    }
                }
                writer.write("</div>");
            } catch (IOException e) {
                throw new JspException(e);
            }
        } else {
            throw new JspException("associationId has to be set");
        }
        return super.doStartTag();
    }

    @Override
    public int doEndTag() throws JspException {
        cssClass = defaultCssClass;
        separator = defaultSeparator;
        return super.doEndTag();
    }

    public void setAssociationId(Integer associationId) {
        this.associationId = associationId;
    }
}
