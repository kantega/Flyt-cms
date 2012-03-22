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

import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.data.enums.ObjectType;

import java.util.ArrayList;
import java.util.List;

public class TopicMap extends BaseObject {
    private String name = "";
    private String defaultTopicInstance = "";
    private boolean isEditable = true;
    private String wSOperation = "";
    private String wSSoapAction = "";
    private String wSEndPoint = "";
    private List<Topic> topicTypes = new ArrayList<Topic>();
    private String url = "";

    public TopicMap() {
    }

    public int getObjectType() {
        return ObjectType.TOPICMAP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public String getDefaultTopicInstance() {
        return defaultTopicInstance;
    }

    public void setDefaultTopicInstance(String defaultTopicInstance) {
        this.defaultTopicInstance = defaultTopicInstance;
    }

    public String getWSOperation() {
        return wSOperation;
    }

    public void setWSOperation(String wSOperation) {
        this.wSOperation = wSOperation;
    }

    public String getWSSoapAction() {
        return wSSoapAction;
    }

    public void setWSSoapAction(String wSSoapAction) {
        this.wSSoapAction = wSSoapAction;
    }

    public String getWSEndPoint() {
        return wSEndPoint;
    }

    public void setWSEndPoint(String wSEndPoint) {
        this.wSEndPoint = wSEndPoint;
    }

    public String getOwner() {
        return null;
    }

    public String getOwnerPerson() {
        return null;
    }

    public List<Topic> getTopicTypes() {
        return topicTypes;
    }

    public void setTopicTypes(List<Topic> topicTypes) {
        this.topicTypes = topicTypes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
