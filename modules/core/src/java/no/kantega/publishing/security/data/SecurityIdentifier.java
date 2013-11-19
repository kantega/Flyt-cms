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

import no.kantega.publishing.common.Aksess;

import java.io.Serializable;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public abstract class SecurityIdentifier implements Serializable {
    private static final long serialVersionUID = -8588017268623687907L;
    public String id = null;


    public SecurityIdentifier() {
    }
    
    public String getId() {
        if (isNotBlank(id)) {
            return id;
        } else {
            return getName();
        }

    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract String getName();

    public abstract String getType();

    public boolean equals(String role) {
        String id = this.id;
        if (role.contains(":") && !id.contains(":")) {
            id = Aksess.getDefaultSecurityDomain() + ":" + id;
        }

        role = role.toLowerCase();
        if (role.charAt(role.length()-1) == '*') {
            role = role.substring(0, role.length() - 1).toLowerCase();
            return id.toLowerCase().startsWith(role);
        } else {
            return id.equalsIgnoreCase(role);
        }
    }
}
