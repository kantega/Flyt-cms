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

package no.kantega.useradmin.model;

import java.util.Iterator;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jul 5, 2007
 * Time: 1:22:55 PM
 */
public class RoleSet {
    private String description = "";
    private String domain = "";
    private Iterator userRoles =  null;
    private Iterator availableRoles =  null;
    private boolean isEditable = false;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Iterator getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Iterator userRoles) {
        this.userRoles = userRoles;
    }

    public Iterator getAvailableRoles() {
        return availableRoles;
    }

    public void setAvailableRoles(Iterator availableRoles) {
        this.availableRoles = availableRoles;
    }

    public boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(boolean editable) {
        isEditable = editable;
    }

}
