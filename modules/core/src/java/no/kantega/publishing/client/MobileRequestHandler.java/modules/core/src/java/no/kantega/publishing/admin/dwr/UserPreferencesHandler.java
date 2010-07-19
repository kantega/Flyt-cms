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

package no.kantega.publishing.admin.dwr;

import no.kantega.publishing.admin.preferences.UserPreference;
import no.kantega.publishing.admin.preferences.UserPreferencesManager;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;


/**
 * Used by DWR to maintain the user's preferences.
 */
@RemoteProxy(name="UserPreferencesHandler")
public class UserPreferencesHandler extends AbstractDwrController {

    @Autowired
    private UserPreferencesManager userPreferencesManager;

    /**
     * Saves a user preference. Delegates the responsibility of setting this preference to the UserPreferencesManager
     * @param preference
     */
    @RemoteMethod
    public void setPreference(UserPreference preference) {
        userPreferencesManager.setPreference(preference, getRequest());
    }


    /**
     * Returns the user's preference for a given preference key.
     * @param key - Preference identifier.
     * @return UserPreference.
     */
    @RemoteMethod
    public UserPreference getPreference(String key) {
        return userPreferencesManager.getPreference(key, getRequest());
    }

    /**
     * Removes a preference.
     * @param key - Preference identifier.
     */
    @RemoteMethod
    public void deletePreference(String key) {
        userPreferencesManager.deletePreference(key, getRequest());
    }

    /**
     * Returns all preferences for the current user.
     * @return List of all preferences, permanent and non-permanent.
     */
    @RemoteMethod
    public List<UserPreference> getAllPreferences() {
        return userPreferencesManager.getAllPreferences(getRequest());
    }
}
