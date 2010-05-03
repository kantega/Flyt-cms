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

package no.kantega.publishing.admin.preferences;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class DefaultUserPreferencesManager implements UserPreferencesManager {

    private static final String PREFERENCES_ATTRIBUTE = "userPreferences";

    
    public void setPreference(UserPreference preference, HttpServletRequest request) {
        if (preference == null || preference.getKey() == null) {
            return;
        }
        HttpSession session = request.getSession();
        Map<String, UserPreference> preferences = (Map<String, UserPreference>) session.getAttribute(PREFERENCES_ATTRIBUTE);
        if (preferences == null) {
            preferences = new HashMap<String, UserPreference>();
            session.setAttribute(PREFERENCES_ATTRIBUTE, preferences);
        }
        preferences.remove(preference.getKey());
        preferences.put(preference.getKey(), preference);

    }

    public UserPreference getPreference(String key, HttpServletRequest request) {
        if (key == null || key.length() == 0) {
            return null;
        }
        HttpSession session = request.getSession();
        Map<String, UserPreference> preferences = (Map<String, UserPreference>) session.getAttribute(PREFERENCES_ATTRIBUTE);
        if (preferences == null) {
            preferences = new HashMap<String, UserPreference>();
            session.setAttribute(PREFERENCES_ATTRIBUTE, preferences);
        }
        return preferences.get(key);
    }

    public void deletePreference(String key, HttpServletRequest request) {
        if (key == null || key.trim().length() == 0) {
            return;
        }
        HttpSession session = request.getSession();
        Map<String, UserPreference> preferences = (Map<String, UserPreference>) session.getAttribute(PREFERENCES_ATTRIBUTE);
        
        preferences.remove(key);
    }

    public List<UserPreference> getAllPreferences(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, UserPreference> preferences = (Map<String, UserPreference>) session.getAttribute(PREFERENCES_ATTRIBUTE);
        if (preferences != null) {
            return new ArrayList<UserPreference>(preferences.values());
        }
        return null;
    }
}
