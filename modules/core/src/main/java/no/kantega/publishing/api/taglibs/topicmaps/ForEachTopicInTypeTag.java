package no.kantega.publishing.api.taglibs.topicmaps;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.ao.TopicMapDao;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.LoopTagSupport;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class ForEachTopicInTypeTag extends LoopTagSupport {

    private static final Logger log = LoggerFactory.getLogger(ForEachTopicInTypeTag.class);

    private Iterator<Topic> i;
    private int topicmapid =-1;
    private String topictypeid = null;
    private static TopicMapDao topicMapDao;


    protected Topic next() throws JspTagException {
        return i.next();
    }

    protected boolean hasNext() throws JspTagException {
        return i.hasNext();
    }

    @SuppressWarnings("unchecked")
    protected void prepare() throws JspTagException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        try {
            TopicMapService topicService = new TopicMapService(request);
            List<Topic> topicList = Collections.emptyList();
            if (topicmapid != -1 && topictypeid != null && !"".equals(topictypeid)) {
                Topic t = topicService.getTopic(topicmapid, topictypeid);
                if (t != null) {
                    topicList = topicService.getTopicsByInstance(t);
                }
            }
            i = topicList.iterator();
        } catch (SystemException e) {
            log.error("Could not set topiclist", e);
            throw new JspTagException(e);
        }
    }

    public void setTopicmap(String topicmap) {
        TopicMap tm = topicMapDao.getTopicMapByName(topicmap);
        if (tm != null) {
            this.topicmapid = tm.getId();
        }
    }

    public void setTopictypeid(String topictypeid) {
        this.topictypeid = topictypeid;
    }

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        if (topicMapDao == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
            topicMapDao = context.getBean(TopicMapDao.class);
        }
    }
}
