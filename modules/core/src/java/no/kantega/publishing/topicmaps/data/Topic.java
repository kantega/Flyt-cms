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

package no.kantega.publishing.topicmaps.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Topic {
    private String id = null;

    private int topicMapId = -1;
    private Topic instanceOf = null;
    private String subjectIdentity = null;

    private boolean isTopicType   = false;
    private boolean isAssociation = false;
    private boolean isSelectable  = false;

    private Date lastUpdated = null;

    private List<TopicBaseName> baseNames = null;
    private List<TopicOccurence> occurences = null;

    private int noUsages = 0;
    private boolean imported;

    public Topic() {
    }

    public Topic(String id) {
        if (id.length() > 64) {
            id = id.substring(0, 63);
        }
        this.id = id;
    }

    public Topic(String id, int topicMapId) {
        this(id);
        this.topicMapId = topicMapId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id.length() > 64) {
            id = id.substring(0, 63);
        }
        this.id = id;
    }

    public int getTopicMapId() {
        return topicMapId;
    }

    public void setTopicMapId(int topicMapId) {
        this.topicMapId = topicMapId;
    }

    public Topic getInstanceOf() {
        return instanceOf;
    }

    public void setInstanceOf(Topic instanceOf) {
        this.instanceOf = instanceOf;
    }

    public String getSubjectIdentity() {
        return subjectIdentity;
    }

    public void setSubjectIdentity(String subjectIdentity) {
        this.subjectIdentity = subjectIdentity;
    }

    public boolean isTopicType() {
        return isTopicType;
    }

    public void setIsTopicType(boolean topicType) {
        isTopicType = topicType;
    }

    public boolean isAssociation() {
        return isAssociation;
    }

    public void setIsAssociation(boolean association) {
        isAssociation = association;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getBaseName() {
        if (baseNames != null && baseNames.size() > 0) {
            TopicBaseName t = (TopicBaseName)baseNames.get(0);
            return t.getBaseName();
        } else {
            return null;
        }
    }

    public void setBaseName(String name) {
        if (baseNames == null) {
            baseNames = new ArrayList<TopicBaseName>();
        }
        if (baseNames.size() == 0) {
            TopicBaseName baseName = new TopicBaseName();
            baseNames.add(baseName);
        }

        TopicBaseName tbn = (TopicBaseName)baseNames.get(0);
        tbn.setBaseName(name);
    }

    public List<TopicBaseName> getBaseNames() {
        return baseNames;
    }

    public void setBaseNames(List<TopicBaseName> baseNames) {
        this.baseNames = baseNames;
    }

    public List<TopicOccurence> getOccurences() {
        return occurences;
    }

    public void setOccurences(List<TopicOccurence> occurences) {
        this.occurences = occurences;
    }

    public void addOccurence(TopicOccurence occurence) {
        if (occurences == null) {
            occurences = new ArrayList<TopicOccurence>();
        }
        occurences.add(occurence);
    }

    public boolean equals(Object obj) {
        try {
            Topic t = (Topic)obj;
            if(this.id == null && t.getId() == null && this.topicMapId == t.getTopicMapId()) {
                return true;
            }
            if(this.id.equals(t.getId()) && this.topicMapId == t.getTopicMapId()) {
                return true;
            }
            return false;
            
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setIsSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    public int getNoUsages() {
        return noUsages;
    }

    public void setNoUsages(int noUsages) {
        this.noUsages = noUsages;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }
}
