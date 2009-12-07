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
import org.springframework.beans.factory.annotation.Autowired;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.annotations.RemoteMethod;


/**
 * Used by DWR to maintain the user's preferences.
 */
@RemoteProxy(name="UserPreferences")
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
}
