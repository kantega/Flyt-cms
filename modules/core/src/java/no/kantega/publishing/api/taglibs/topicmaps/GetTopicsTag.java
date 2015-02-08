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

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.ao.TopicMapDao;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.LoopTagSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetTopicsTag extends LoopTagSupport {
    private String contentId;

    private String collection;

    private static final Logger log = LoggerFactory.getLogger(GetTopicsTag.class);

    private Iterator i;
    private String associatedid = null;
    private int topicmapid =-1;
    private String ignoretopics = null;
    private String instance = null;
    private String topicid = null;
    private String topiclist = null;
    private TopicMapDao topicMapDao;

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
            if (topicid != null) {
                handleTopicId(topicService);

            } else if (instance != null) {
                handleTopicInstance(topicService);

            } else if (associatedid != null) {
                handleAssociatedId(topicService);
            } else if (topiclist != null) {
                handleTopicList(topicService);
            } else {
                List<Topic> l;
                Content content = AttributeTagHelper.getContent(pageContext, collection, contentId);
                if (content != null) {
                    l = content.getTopics();
                    if (l == null || l.size() == 0) {
                        l = topicService.getTopicsByContentId(content.getId());
                    }
                } else {
                    l = new ArrayList<>();
                }
                i = l.iterator();
            }

            associatedid = null;
            topicmapid = -1;
            ignoretopics = null;
            instance = null;
            topicid = null;
            topiclist = null;

        } catch (SystemException | NotAuthorizedException | JspException e) {
            throw new JspTagException(e);

        }
    }

    private void handleTopicList(TopicMapService topicService) {
        if (topiclist.length() > 0) {
            String[] topics = topiclist.split(",");

            List<Topic> l = new ArrayList<>();
            for (String topicStr : topics) {
                if (topicStr.contains(":")) {
                    String topicMapId = topicStr.substring(0, topicStr.indexOf(":"));
                    String topicId = topicStr.substring(topicStr.indexOf(":") + 1, topicStr.length());
                    Topic t = topicService.getTopic(Integer.parseInt(topicMapId), topicId);
                    if (t != null) {
                        l.add(t);
                    }
                }
            }
            i = l.iterator();
        }
    }

    private void handleAssociatedId(TopicMapService topicService) throws JspTagException {
        if(topicmapid == -1) {
            throw new JspTagException("topicmapid must be specified when associatedid is specified");
        }
        Topic topic = topicService.getTopic(topicmapid, associatedid);

        List<TopicAssociation> associations = topicService.getTopicAssociations(topic);

        i = clean(associations, ignoretopics).iterator();
    }

    private void handleTopicInstance(TopicMapService topicService) throws JspTagException {
        if(topicmapid == -1) {
            throw new JspTagException("topicmapid must be specified when instance is specified");
        }
        List<Topic> list = topicService.getTopicsByInstance(new Topic(instance, topicmapid));
        i = list.iterator();
    }

    private void handleTopicId(TopicMapService topicService) throws JspTagException {
        if (topicmapid == -1) {
            throw new JspTagException("topicmapid must be specified when topicid is specified");
        }
        Topic t = topicService.getTopic(topicmapid, topicid);
        List<Topic> list = new ArrayList<>();
        if(t != null) {
            list.add(t);
        }
        i = list.iterator();
    }

    public void setContentid(String contentId) {
        this.contentId = contentId;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    private List<TopicAssociation> clean(List<TopicAssociation> associations, String ignore) {
        List<String> ignoreList = new ArrayList<>();
        if(ignore != null) {
            String[] ignoreIds = ignore.split(",");
            for (String ignoreId : ignoreIds) {
                if (!ignoreId.equals("")) {
                    ignoreList.add(ignoreId);
                }
            }
        }
        if(ignoreList.size() > 0) {
            List<TopicAssociation> clean = new ArrayList<>();
            for (TopicAssociation association : associations) {
                if (!ignoreList.contains(association.getAssociatedTopicRef().getId())) {
                    clean.add(association);
                }
            }
            return clean;
        } else {
            return associations;
        }
    }

    public void setAssociatedid(String associatedid) {
        this.associatedid = associatedid;
    }

    /**
     * @deprecated use topicMap
     */
    @Deprecated
    public void setTopicmapid(int topicmapid) {
        this.topicmapid = topicmapid;
    }

    public void setTopicmap(String topicmap) {
        TopicMap tm = topicMapDao.getTopicMapByName(topicmap);
        if (tm != null) {
            this.topicmapid = tm.getId();
        }
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }


    public void setTopicid(String topicid) {
        this.topicid = topicid;
    }


    public void setIgnoretopics(String ignoretopics) {
        this.ignoretopics = ignoretopics;
    }

    public void setTopiclist(String topiclist) {
        this.topiclist = topiclist;
    }

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        topicMapDao = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext()).getBean(TopicMapDao.class);
    }
}
