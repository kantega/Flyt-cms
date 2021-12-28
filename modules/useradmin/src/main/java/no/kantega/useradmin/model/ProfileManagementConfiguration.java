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

import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.profile.ProfileManager;
import no.kantega.security.api.profile.ProfileUpdateManager;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jun 26, 2007
 * Time: 2:01:27 PM
 */
public class ProfileManagementConfiguration {
    private String description = "";
    private String domain = "";
    private ProfileManager profileManager = null;
    private ProfileUpdateManager profileUpdateManager = null;
    private PasswordManager passwordManager = null;

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

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    public ProfileUpdateManager getProfileUpdateManager() {
        return profileUpdateManager;
    }

    public void setProfileUpdateManager(ProfileUpdateManager profileUpdateManager) {
        this.profileUpdateManager = profileUpdateManager;
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }

    public void setPasswordManager(PasswordManager passwordManager) {
        this.passwordManager = passwordManager;
    }
}
