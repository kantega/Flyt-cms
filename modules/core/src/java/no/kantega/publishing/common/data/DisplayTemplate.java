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

import no.kantega.publishing.api.model.PublicIdObject;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class DisplayTemplate implements PublicIdObject {
    private ContentTemplate contentTemplate  = null;
    private ContentTemplate metaDataTemplate = null;

    private String name = "";
    private String description = "";
    private String image = "";
    private String view;
    private String miniView;
    private String rssView;
    private String searchView;

    private Boolean allowMultipleUsages;
    private Boolean isNewGroup;

    private Long defaultForumId;

    private List<Site> sites;

    private List<DisplayTemplateControllerId> controllers;

    private int id = -1;
    private String publicId = "";

    private Properties properties;

    public DisplayTemplate() {
        allowMultipleUsages = true;
        isNewGroup = false;
    }

    public ContentTemplate getContentTemplate() {
        return contentTemplate;
    }

    public void setContentTemplate(ContentTemplate contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    public ContentTemplate getMetaDataTemplate() {
        return metaDataTemplate;
    }

    public void setMetaDataTemplate(ContentTemplate metaDataTemplate) {
        this.metaDataTemplate = metaDataTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null) {
            this.description = description;
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if (image != null) {
            this.image = image;
        }
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        if (view != null) {
            this.view = view;
        }
    }

    public String getMiniView() {
        return miniView;
    }

    public void setMiniView(String miniView) {
        this.miniView = miniView;
    }

    public String getRssView() {
        return rssView;
    }

    public void setRssView(String rssView) {
        this.rssView = rssView;
    }

    public String getSearchView() {
        return searchView;
    }

    public void setSearchView(String searchView) {
        this.searchView = searchView;
    }

    public boolean allowMultipleUsages() {
        if (allowMultipleUsages == null) {
            return true;
        } else {
            return allowMultipleUsages;
        }
    }

    public void setAllowMultipleUsages(boolean allowMultipleUsages) {
        this.allowMultipleUsages = allowMultipleUsages;
    }

    public boolean isNewGroup() {
        if (isNewGroup == null) {
            return false;
        } else {
            return isNewGroup;
        }
    }

    public void setIsNewGroup(boolean isNewGroup) {
        this.isNewGroup = isNewGroup;
    }

    public Long getDefaultForumId() {
        return defaultForumId;
    }

    public void setDefaultForumId(Long defaultForumId) {
        this.defaultForumId = defaultForumId;
    }

    public List<DisplayTemplateControllerId> getControllers() {
        return controllers;
    }

    public void setControllers(List<DisplayTemplateControllerId> controllers) {
        this.controllers = controllers;
    }

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

    public List<Site> getSites() {
        if (sites == null) {
            sites = new ArrayList<Site>();
        }
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public synchronized Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
        }
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String toString() {
        String str = "";

        if (publicId != null && publicId.length() > 0) {
            str = publicId;
        }

        if (id != -1) {
            str = str + "(" + id + ")";
        }

        return str;
    }
}
