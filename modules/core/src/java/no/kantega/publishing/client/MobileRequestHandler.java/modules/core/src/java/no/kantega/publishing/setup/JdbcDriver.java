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

package no.kantega.publishing.setup;

public class JdbcDriver {

    private String id;
    private String name;
    private String driverClass;
    private String defaultUrl;
    private String helpText;

    public JdbcDriver(String id, String name, String driverClass, String defaultUrl, String helpText) {
        this.id = id;
        this.name = name;
        this.driverClass = driverClass;
        this.defaultUrl = defaultUrl;
        this.helpText = helpText;
    }

    public String getId() {
        return id;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public String getName() {
        return name;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
}
