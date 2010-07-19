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

package no.kantega.publishing.search.model;

import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.result.SearchHitContext;
import no.kantega.search.result.QueryInfo;

/**
 * User: Anders Skar, Kantega AS
 * Date: Nov 8, 2007
 * Time: 12:39:40 PM
 */
public class AksessSearchHitContext implements SearchHitContext {

    private int siteId = -1;
    private SecuritySession securitySession;
    private QueryInfo queryInfo;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public SecuritySession getSecuritySession() {
        return securitySession;
    }

    public void setSecuritySession(SecuritySession securitySession) {
        this.securitySession = securitySession;
    }

    public QueryInfo getQueryInfo() {
        return queryInfo;
    }

    public void setQueryInfo(QueryInfo queryInfo) {
        this.queryInfo = queryInfo;
    }

}
