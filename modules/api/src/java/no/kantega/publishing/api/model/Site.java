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

package no.kantega.publishing.api.model;

import java.util.Collections;
import java.util.List;

/**
 * Represent a single site.
 */
public class Site implements PublicIdObject{
    private int id;
    private String name;
    private String alias;
    private boolean disabled = false;
    private String scheme;
    private String publicId = "";
    private String displayTemplateId;
    private boolean isDefault;

    private List<String> hostnames = Collections.emptyList();

    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getPublicId(){
        return publicId;
    }

    public String getName(){
        return name;
    }

    public String getAlias(){
        return alias;
    }

    public List<String> getHostnames(){
        return hostnames;
    }

    public String getScheme(){
        return scheme;
    }

    public String getDisplayTemplateId(){
        return displayTemplateId;
    }

    public boolean isDisabled(){
        return disabled;
    }

    public boolean isDefault(){
        return isDefault;
    }

    public Site setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public Site setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public Site setDisplayTemplateId(String displayTemplateId) {
        this.displayTemplateId = displayTemplateId;
        return this;
    }

    public Site setDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public Site setHostnames(List<String> hostnames) {
        this.hostnames = hostnames;
        return this;
    }

    public Site setName(String name) {
        this.name = name;
        return this;
    }

    public Site setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public String toString() {
        return "Site: " + name + "(" + id + ") Alias: " + alias;
    }
}
