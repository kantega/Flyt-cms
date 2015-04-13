/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.util.Iterator;
import java.util.List;

public class ForEachTopicInMapTag extends LoopTagSupport {

    private static final Logger log = LoggerFactory.getLogger(ForEachTopicInMapTag.class);

    private Iterator<Topic> i;
    private int topicmapid =-1;
    private static TopicMapDao topicMapDao;

    protected Object next() throws JspTagException {
        return i.next();
    }

    protected boolean hasNext() throws JspTagException {
        return i.hasNext();
    }

    protected void prepare() throws JspTagException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        try {
            TopicMapService topicService = new TopicMapService(request);
            if (topicmapid != -1) {
                List<Topic> l = topicService.getTopicsByTopicMapId(topicmapid);
                i = l.iterator();
            } else {
                List<Topic> l = topicService.getAllTopics();
                i = l.iterator();
            }

        } catch (SystemException e) {
            log.error("Error iterating topics", e);
            throw new JspTagException(e);
        }
    }

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        if (topicMapDao == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
            topicMapDao = context.getBean(TopicMapDao.class);
        }
    }

    public void setTopicmap(String topicmap) {
        TopicMap tm = topicMapDao.getTopicMapByName(topicmap);
        if (tm != null) {
            this.topicmapid = tm.getId();
        }
    }
}
