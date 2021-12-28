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

package no.kantega.publishing.configuration;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.configuration.ConfigurationListener;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.common.Aksess;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LegacySystemConfiguration implements SystemConfiguration {

    private Configuration configuration;
    private List<no.kantega.publishing.api.configuration.ConfigurationListener> configurationListeners = new ArrayList<>();

    public LegacySystemConfiguration() {
        this.configuration = Aksess.getConfiguration();
        setConfigurationListener();
    }

    private void setConfigurationListener() {
        final SystemConfiguration c = this;
        this.configuration.addConfigurationListener(new ConfigurationListener() {
            @Override
            public void configurationRefreshed(Configuration configuration) {
                for (no.kantega.publishing.api.configuration.ConfigurationListener listener : configurationListeners) {
                    listener.configurationRefreshed(c);
                }
            }
        });
    }

    public String getString(String name) {
        return configuration.getString(name);
    }

    public String getString(String name, String defaultValue) {
        return configuration.getString(name, defaultValue);
    }

    public String[] getStrings(String name) {
        return configuration.getStrings(name);
    }

    public String[] getStrings(String name, String defaultValue) {
        return configuration.getStrings(name, defaultValue);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        return configuration.getBoolean(name, defaultValue);
    }

    public long getLong(String name, long defaultValue) {
        try {
            return configuration.getLong(name, defaultValue);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public int getInt(String name, int defaultValue) {
        return configuration.getInt(name, defaultValue);
    }

    public Locale getDefaultAdminLocale() {
        return Aksess.getDefaultAdminLocale();
    }

    public String getDefaultDateFormat() {
        return Aksess.getDefaultDateFormat();
    }

    public String getDefaultDatetimeFormat() {
        return Aksess.getDefaultDatetimeFormat();
    }

    @Override
    public void addConfigurationListener(no.kantega.publishing.api.configuration.ConfigurationListener listener) {
        configurationListeners.add(listener);
    }
}
