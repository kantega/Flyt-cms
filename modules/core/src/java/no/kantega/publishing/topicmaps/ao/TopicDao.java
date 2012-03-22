
/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.topicmaps.ao;

import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.topicmaps.data.Topic;

import java.util.List;

public interface TopicDao {

    void deleteTopic(Topic topic);

    void deleteTopic(Topic topic, boolean deleteRelatedTables);

    void deleteAllTopics(int topicMapId);

    void deleteAllImportedTopics(int topicMapId);

    Topic getTopic(int topicMapId, String topicId);

    void setTopic(Topic topic);

    void addTopicToContentAssociation(Topic topic, int contentId);

    void deleteTopicToContentAssociation(Topic topic, int contentId);

    void addTopicToSecurityIdentifierAssociation(Topic topic, SecurityIdentifier securityIdentifier);

    void deleteTopicToSecurityIdentifierAssociation(Topic topic, SecurityIdentifier securityIdentifier);
     /**
     * Deletes all topic associations for a given content.
     *
     * @param contentId
     */
    void deleteTopicAssociationsForContent(int contentId);

    List<Role> getRolesForTopic(Topic topic);

    List<Topic> getAllTopics();
    List<Topic> getTopicsByContentId(int contentId);
    List<Topic> getTopicsByTopicMapId(int topicMapId);
    List<Topic> getTopicTypesForTopicMapId(int topicMapId);
    List<Topic> getTopicsByTopicInstance(Topic instance);
    List<Topic> getTopicsForSecurityIdentifier(SecurityIdentifier sid);
    List<Topic> getTopicsByNameAndTopicMapId(String topicName, int topicMapId);
    List<Topic> getTopicsByNameAndTopicInstance(String topicName, Topic instance);

    /**
     * @param contentId - ID of root
     * @param topicMapId - ID of topicmap to find topics from
     * @return Topics that are used on children of content with contentId
     */
    List<Topic> getTopicsInUseByChildrenOf(int contentId, int topicMapId);
}
