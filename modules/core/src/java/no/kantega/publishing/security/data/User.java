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

package no.kantega.publishing.security.data;

import no.kantega.publishing.security.data.enums.RoleType;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.security.api.identity.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class User extends SecurityIdentifier {

    private Identity identity;
    private String givenName = "";
    private String surname = "";
    private String email = null;
    private String department;
    private HashMap<String, Role> roles = new HashMap<String, Role>();
    private List<Topic> topics = null;
    private List orgUnits = new ArrayList();
    private Properties attributes;

    public String getType() {
        return RoleType.USER;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        if (!givenName.equals("") && !surname.equals("")) {
            return givenName + " " + surname;
        } else {
            return givenName + surname;
        }
    }

    public HashMap getRoles() {
        return roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void addRole(Role role) {
        roles.put(role.getId(), role);
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic topic) {
        if (topic == null) {
            return;
        }
        if (topics == null) {
            topics = new ArrayList();
        }
        boolean found = false;
        for (int i = 0; i < topics.size(); i++) {
            Topic t = (Topic)topics.get(i);
            if (t.getId().equalsIgnoreCase(topic.getId()) && t.getTopicMapId() == topic.getTopicMapId()) {
                found = true;
                break;
            }
        }
        if (!found) {
            topics.add(topic);
        }
    }

    public void removeTopic(Topic topic) {
        if(topic == null || topics == null) {
            return;
        }
        topics.remove(topic);
    }

    public List getOrgUnits() {
        return orgUnits;
    }

    public void setOrgUnits(List orgUnits) {
        this.orgUnits = orgUnits;
    }

    public void setAttributes(Properties attributes) {
        this.attributes = attributes;
    }

    public Properties getAttributes() {
        return attributes;
    }
}
