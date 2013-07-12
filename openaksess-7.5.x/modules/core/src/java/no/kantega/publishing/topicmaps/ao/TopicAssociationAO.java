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

package no.kantega.publishing.topicmaps.ao;

import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import no.kantega.commons.exception.SystemException;

import java.util.List;

@Deprecated
public class TopicAssociationAO {
    public static void deleteTopicAssociation(TopicAssociation association) throws SystemException {
        TopicAssociationDao dao = getDao();
        dao.deleteTopicAssociation(association);
    }

    public static void addTopicAssociation(TopicAssociation association) throws SystemException {
        TopicAssociationDao dao = getDao();
        dao.addTopicAssociation(association);
    }

    public static List<TopicAssociation> getTopicAssociations(Topic topic) throws SystemException {
        TopicAssociationDao dao = getDao();
        return dao.getTopicAssociations(topic);
    }


    public static void deleteTopicAssociations(Topic topic) throws SystemException {
        TopicAssociationDao dao = getDao();
        dao.deleteTopicAssociations(topic);
    }

    private static TopicAssociationDao getDao() {
        return (TopicAssociationDao) RootContext.getInstance().getBean("aksessTopicAssociationDao");
    }
}
