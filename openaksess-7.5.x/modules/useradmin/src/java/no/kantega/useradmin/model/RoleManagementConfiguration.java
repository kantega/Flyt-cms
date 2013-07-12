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

import no.kantega.security.api.role.RoleManager;
import no.kantega.security.api.role.RoleUpdateManager;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jun 26, 2007
 * Time: 2:12:49 PM
 */
public class RoleManagementConfiguration {
    private String description = "";
    private String domain = "";
    private RoleManager roleManager;
    private RoleUpdateManager roleUpdateManager;

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

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public RoleUpdateManager getRoleUpdateManager() {
        return roleUpdateManager;
    }

    public void setRoleUpdateManager(RoleUpdateManager roleUpdateManager) {
        this.roleUpdateManager = roleUpdateManager;
    }
}
