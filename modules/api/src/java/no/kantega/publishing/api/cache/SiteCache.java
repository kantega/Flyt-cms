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

package no.kantega.publishing.api.cache;

import no.kantega.publishing.api.model.Site;

import java.util.List;

/**
 */
public interface SiteCache {
    /**
     * @param hostname to get site for
     * @return the particular site if it exists, otherwise null.
     */
    Site getSiteByHostname(String hostname) ;

    Site getSiteById(int siteId);

    Site getSiteByPublicIdOrAlias(String id);

    List<Site> getSites() ;

    public void reloadCache();
}
