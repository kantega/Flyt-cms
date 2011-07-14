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
import no.kantega.publishing.topicmaps.ao.TopicMapAO;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagSupport;
import java.util.Iterator;
import java.util.List;

public class ForEachTopicInMapTag extends LoopTagSupport {

    private Logger log = Logger.getLogger(getClass());

    private Iterator i;
    private int topicmapid =-1;

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
                List l = topicService.getTopicsByTopicMapId(topicmapid);
                i = l.iterator();
            } else {
                List l = topicService.getAllTopics();
                i = l.iterator();
            }

        } catch (SystemException e) {
            log.error(e);
            throw new JspTagException(e.getMessage());
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
}
