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

package no.kantega.commons.configuration;

import no.kantega.commons.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Object containing the current configuration.
 */
public class Configuration {

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private static File applicationDirectory;

    private Properties properties = null;

    private List<ConfigurationListener> listeners = new ArrayList<>();

    public Configuration(Properties properties) {
        this.properties = properties;
    }

    public static String getConfigDirectory() {
        return appendSlash(new File(applicationDirectory, "conf"));
    }

    /**
     * Hack to keep backward compatibility of methods returning string paths in this class. They must end with a '/'
     * @param file
     * @return
     */
    private static String appendSlash(File file) {
        String dir = file.getAbsolutePath();
        return dir.endsWith("/") ? dir : dir +"/";
    }


    public static String getApplicationDirectory() {
        return appendSlash(applicationDirectory);
    }


    public String getString(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("name kan ikke vaere null");
        }
        String val = properties.getProperty(name);
        if (val != null) {
            val = val.trim();
        }
        return val;
    }


    public String getString(String name, String defaultValue) throws IllegalArgumentException {
        String val = getString(name);
        if (val == null) {
            return defaultValue;
        } else {
            return val;
        }
    }

    public String[] getStrings(String name) throws IllegalArgumentException {
        String val[] = null;

        if (name == null) {
            throw new IllegalArgumentException("name must be non-null");
        }
        String v = properties.getProperty(name);
        if (v != null) {
            v = v.trim();
            val = v.split(",");

        }
        return val;
    }


    public String[] getStrings(String name, String defaultValue) throws IllegalArgumentException {
        String[] val = getStrings(name);
        if (val == null) {
            return defaultValue.split(",");
        } else {
            return val;
        }
    }


    public boolean getBoolean(String name, boolean defaultValue) throws IllegalArgumentException {
        String val = getString(name);
        if (val  == null) {
            return defaultValue;
        } else {
            return Boolean.valueOf(val);
        }
    }


    public long getLong(String name, long defaultValue) throws ConfigurationException, IllegalArgumentException {
        try {
            String val = getString(name);
            if (val  == null) {
                return defaultValue;
            } else {
                return Long.parseLong(val);
            }
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Forventet long:" + name, e);
        }
    }


    public int getInt(String name, int defaultValue) {
        String val = getString(name);
        if (val  == null) {
            return defaultValue;
        } else {
            try {

                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                log.warn("NumberFormatException when trying to parse {} as {}, returning default value {}", val, name, defaultValue);
                return defaultValue;
            }
        }
    }

    public static void setApplicationDirectory(File applicationDirectory) {
        Configuration.applicationDirectory = applicationDirectory;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
        for(ConfigurationListener listener : listeners) {
            listener.configurationRefreshed(this);
        }
    }

    public void addConfigurationListener(ConfigurationListener listener) {
        listeners.add(listener);
    }
}
