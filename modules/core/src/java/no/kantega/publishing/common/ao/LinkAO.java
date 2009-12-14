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

package no.kantega.publishing.common.ao;

import no.kantega.commons.sqlsearch.SearchTerm;
import no.kantega.commons.sqlsearch.dialect.SQLDialect;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrenceHandler;
import no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter;
import no.kantega.publishing.modules.linkcheck.crawl.LinkHandler;
import no.kantega.publishing.spring.RootContext;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.Date;
import java.util.List;

/**
 * Deprecated. Use the Spring instanciated LinkDao instead.
 */
@Deprecated
public class LinkAO {

    private static final String LINK_DAO_BEAN_ID = "aksessLinkDao";


    public static void deleteAllLinks() {
        LinkDao dao = getLinkDao();
        if (dao != null) {
            dao.deleteAllLinks();
        }
    }


    public static void saveAllLinks(final LinkEmitter emitter) {
        LinkDao dao = getLinkDao();
        if (dao != null) {
            dao.saveAllLinks(emitter);
        }
    }


    public static void doForEachLink(SearchTerm term, no.kantega.publishing.modules.linkcheck.check.LinkHandler handler) {
        LinkDao dao = getLinkDao();
        if (dao != null) {
            dao.doForEachLink(term, handler);
        }
    }

    public static void doForEachLinkOccurrence(int siteId, String sort, final LinkOccurrenceHandler handler) {
        LinkDao dao = getLinkDao();
        if (dao != null) {
            dao.doForEachLinkOccurrence(siteId, sort, handler);
        }
    }



    public static void deleteLinksForContentId(int contentId) {
        LinkDao dao = getLinkDao();
        if (dao != null) {
            dao.deleteLinksForContentId(contentId);
        }
    }



    public static int getNumberOfLinks() {
        LinkDao dao = getLinkDao();
        if (dao != null) {
            return dao.getNumberOfLinks();
        }
        return 0;
    }

    private static LinkDao getLinkDao() {
        return  (LinkDao) RootContext.getInstance().getBean(LINK_DAO_BEAN_ID);
    }
}
