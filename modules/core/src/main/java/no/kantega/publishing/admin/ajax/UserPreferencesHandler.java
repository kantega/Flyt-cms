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

package no.kantega.publishing.admin.ajax;

import no.kantega.publishing.admin.preferences.UserPreference;
import no.kantega.publishing.admin.preferences.UserPreferencesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * Controller for maintaining the user's preferences.
 */
@Controller
@RequestMapping("/admin/publish/UserPreferences.action")
public class UserPreferencesHandler {

    @Autowired
    private UserPreferencesManager userPreferencesManager;

    /**
     * Saves a user preference. Delegates the responsibility of setting this preference to the UserPreferencesManager
     * @param preference
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity setPreference(@RequestBody UserPreference preference, HttpServletRequest request) {
        userPreferencesManager.setPreference(preference, request);
        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * Returns the user's preference for a given preference key.
     * @param key - Preference identifier.
     * @return UserPreference.
     */
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody UserPreference getPreference(@RequestParam String key, HttpServletRequest request) {
        return userPreferencesManager.getPreference(key, request);
    }

    /**
     * Removes a preference.
     * @param key - Preference identifier.
     */
    @RequestMapping(method =RequestMethod.DELETE)
    public ResponseEntity deletePreference(@RequestParam String key, HttpServletRequest request) {
        userPreferencesManager.deletePreference(key, request);
        return new ResponseEntity(HttpStatus.OK);
    }

}
