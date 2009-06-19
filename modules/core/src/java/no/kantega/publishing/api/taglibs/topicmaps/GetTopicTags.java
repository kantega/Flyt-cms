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
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import org.apache.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetTopicTags extends LoopTagSupport {
    private String contentId;

    private String collection;

    private Logger log = Logger.getLogger(getClass());

    private Iterator i;
    private String associatedid = null;
    private int topicmapid =-1;
    private String ignoretopics = null;
    private String instance = null;
    private String topicid = null;
    private String topiclist = null;

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
                if (topicmapid == -1) {
                    throw new JspTagException("topicmapid must be specified when topicid is specified");
                }
                Topic t = topicService.getTopic(topicmapid, topicid);
                List list = new ArrayList();
                if(t != null) {
                    list.add(t);
                }
                i = list.iterator();

            } else if (instance != null) {
                if(topicmapid == -1) {
                    throw new JspTagException("topicmapid must be specified when instance is specified");
                }
                List list = topicService.getTopicsByInstance(new Topic(instance, topicmapid));
                i = list.iterator();

            } else if (associatedid != null) {
                if(topicmapid == -1) {
                    throw new JspTagException("topicmapid must be specified when associatedid is specified");
                }
                Topic topic = topicService.getTopic(topicmapid, associatedid);

                List associations = topicService.getTopicAssociations(topic);

                i = clean(associations, ignoretopics).iterator();
            } else if (topiclist != null) {
                if (topiclist.length() > 0) {
                    String[] topics = topiclist.split(",");

                    List l = new ArrayList();
                    for (int j = 0; j < topics.length; j++) {
                        String topicStr = topics[j];
                        if (topicStr.indexOf(":") != -1) {
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
            } else {
                List l;
                Content content = AttributeTagHelper.getContent(pageContext, collection, contentId);
                if (content != null) {
                    l = content.getTopics();
                    if (l == null || l.size() == 0) {
                        l = topicService.getTopicsByContentId(content.getId());
                    }
                } else {
                    l = new ArrayList();
                }
                i = l.iterator();
            }

            associatedid = null;
            topicmapid = -1;
            ignoretopics = null;
            instance = null;
            topicid = null;
            topiclist = null;

        } catch (SystemException e) {
            log.error(e);
            throw new JspTagException(e.getMessage());

        } catch (NotAuthorizedException e) {
            log.error(e);
            throw new JspTagException(e.getMessage());
        } catch (JspException e) {
            log.error(e);
            throw new JspTagException(e.getMessage());
        }
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    private List clean(List associations, String ignore) {
        List ignoreList = new ArrayList();
        if(ignore != null) {
            String[] ignoreIds = ignore.split(",");
            for (int j = 0; j < ignoreIds.length; j++) {
                String ignoreId = ignoreIds[j];
                if(ignoreId != "") {
                    ignoreList.add(ignoreId);
                }
            }
        }
        if(ignoreList.size() > 0) {
            List clean = new ArrayList();
            for (int i = 0; i < associations.size(); i++) {
                TopicAssociation association = (TopicAssociation) associations.get(i);
                if(!ignoreList.contains(association.getAssociatedTopicRef().getId())) {
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

    public void setTopicmapid(int topicmapid) {
        this.topicmapid = topicmapid;
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
}
