package no.kantega.publishing.api.taglibs.topicmaps;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.ao.TopicMapAO;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagSupport;
import java.util.ArrayList;
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
            List<Topic> topicList = new ArrayList<Topic>();
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

    /**
     * @deprecated use topicMap
     */
    @Deprecated
    public void setTopicmapid(int topicmapid) {
        this.topicmapid = topicmapid;
    }

    public void setTopicmap(String topicmap) {
        TopicMap tm = TopicMapAO.getTopicMapByName(topicmap);
        if (tm != null) {
            this.topicmapid = tm.getId();
        }
    }

    public void setTopictypeid(String topictypeid) {
        this.topictypeid = topictypeid;
    }

}
