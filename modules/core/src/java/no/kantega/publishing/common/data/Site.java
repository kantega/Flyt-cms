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

package no.kantega.publishing.common.data;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.api.model.PublicIdObject;

import java.util.ArrayList;
import java.util.List;

public class Site implements PublicIdObject, no.kantega.publishing.api.model.Site {
    private int id = -1;
    private String name;
    private String alias;
    private boolean disabled = false;
    private String scheme;
    private String publicId = "";

    private List<String> hostnames = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getHostnames() {
        if (hostnames == null) {
            hostnames = new ArrayList<String>();
        }
        return hostnames;
    }

    public void setHostnames(List<String> hostnames) {
        this.hostnames = hostnames;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setIsDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getDefaultBaseUrl() {
        String baseurl = Aksess.getBaseUrl();
        if (hostnames.size() > 0) {
            baseurl = scheme + "://" + hostnames.get(0);
        }

        return baseurl;
    }
}
