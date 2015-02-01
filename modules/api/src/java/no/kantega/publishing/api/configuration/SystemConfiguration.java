/*
 * Copyright 2010 Kantega AS
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

package no.kantega.publishing.api.configuration;

import java.util.Locale;

/**
 * Access the configuration for the running system
 */
public interface SystemConfiguration {

    /**
     * @param name - configuration key to get.
     * @return configuration string, or null if not defined.
     */
    public String getString(String name);

    /**
     * @param name - configuration key to get.
     * @param defaultValue - value to return if value is not present.
     * @return configuration string, or defaultValue if not defined.
     */
    public String getString(String name, String defaultValue);

    /**
     * @param name configuration key to get.
     * @return the configuration value split by «,». If the configuration is not defined, [] is returned.
     */
    public String[] getStrings(String name);

    /**
     * @param name configuration key to get.
     * @param defaultValue - value to return if value is not present.
     * @return the configuration value split by «,». If the configuration is not defined, {@code defaultValue.split(",")} is returned
     */
    public String[] getStrings(String name, String defaultValue);

    /**
     * @param name - configuration key to get.
     * @param defaultValue - value to return if value is not present.
     * @return configuration string as boolean, or defaultValue if not defined.
     */
    public boolean getBoolean(String name, boolean defaultValue);

    /**
     * @param name - configuration key to get.
     * @param defaultValue- value to return if value is not present.
     * @return configuration string as long, or defaultValue if not defined.
     */
    public long getLong(String name, long defaultValue);

    /**
     * @param name - configuration key to get.
     * @param defaultValue- value to return if value is not present.
     * @return configuration string as int, or defaultValue if not defined.
     */
    public int getInt(String name, int defaultValue);

    /**
     * @return Locale used in the admin interface
     */
    public Locale getDefaultAdminLocale();

    /**
     * @return defualt dateformat, dd.MM.yyyy
     */
    public String getDefaultDateFormat();

    /**
     * @return defualt dateformat, dd.MM.yyyy HH:mm
     */
    public String getDefaultDatetimeFormat();

    /**
     * @param listener to notfiy when the SystemConfiguration is updated.
     */
    public void addConfigurationListener(ConfigurationListener listener);
}
