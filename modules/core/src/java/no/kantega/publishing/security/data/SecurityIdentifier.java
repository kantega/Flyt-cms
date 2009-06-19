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

public abstract class SecurityIdentifier {
    public String id = null;


    public SecurityIdentifier() {
    }

    public String getId() {
        if (id != null && id.length() > 0) {
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
        if (role.indexOf(":") != -1 && id.indexOf(":") == -1) {
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
