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

package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.topicmaps.ao.TopicMapDao;
import no.kantega.publishing.topicmaps.data.TopicMap;

import java.util.List;

public class TopicmapAttribute extends Attribute {

    private static TopicMapDao topicMapDao;

    public String getRenderer() {
        return "topicmap";
    }

    public List<TopicMap> getTopicMaps() throws SystemException {
        if(topicMapDao == null){
            topicMapDao = RootContext.getInstance().getBean(TopicMapDao.class);
        }
        return topicMapDao.getTopicMaps();
    }
}

