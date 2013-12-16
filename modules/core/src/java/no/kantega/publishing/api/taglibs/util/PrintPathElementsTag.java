package no.kantega.publishing.api.taglibs.util;

import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.api.path.PathEntryService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;

/**
 * Tag for printing the path of a page, i.e. its ancestors, given its associationId.
 * Example output:
 * &lt;div class=&quot;contentPath&quot;&gt;
 *  &lt;span&gt;Frontpage&lt;/span&gt; &gt; &lt;span&gt;some other page&lt;/span&gt; &gt; &lt;span&gt;parent of page&lt;/span&gt;
 * &lt;/div&gt;
 *
 * The class of the div and the separator between the spans are configurable.
 */
public class PrintPathElementsTag extends TagSupport {
    private final String defaultCssClass = "contentPath";
    private final String defaultSeparator = "&gt;";

    private Integer associationId;
    private String cssClass = defaultCssClass;
    private String separator = defaultSeparator;
    private boolean skipFirst = false;
    private PathEntryService pathEntryService;

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
        pathEntryService = context.getBean(PathEntryService.class);
    }

    @Override
    public int doStartTag() throws JspException {
        if(associationId != null){
            try {
                List<PathEntry> pathByContentId = pathEntryService.getPathEntriesByAssociationIdInclusive(associationId);
                JspWriter writer = pageContext.getOut();

                writer.write("<div class=\"");
                writer.write(cssClass);
                writer.write("\">");

                int lastElement = pathByContentId.size() - 1;
                for(int i = getStartIndex(); i < pathByContentId.size(); i++){
                    PathEntry pathEntry = pathByContentId.get(i);
                    writer.write("<span>");
                    writer.write(pathEntry.getTitle());
                    writer.write("</span>");
                    if(i < lastElement){
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

    private int getStartIndex() {
        return skipFirst ? 1 : 0;
    }

    @Override
    public int doEndTag() throws JspException {
        associationId = -1;
        cssClass = defaultCssClass;
        separator = defaultSeparator;
        skipFirst = false;
        return super.doEndTag();
    }

    public void setAssociationId(Integer associationId) {
        this.associationId = associationId;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setSkipFirst(boolean skipFirst) {
        this.skipFirst = skipFirst;
    }
}
