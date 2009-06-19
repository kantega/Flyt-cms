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

import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * User: Anders Skar, Kantega AS
 * Date: Oct 13, 2008
 * Time: 10:51:41 AM
 */
public class MultimediaUsageAO {

    public static void removeUsageForContentId(int contentId) {
        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());

        template.update("delete from multimediausage where ContentId = ?", new Object[] {contentId});
    }

    public static void removeMultimediaId(int multimediaId) {
        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());

        template.update("delete from multimediausage where MultimediaId = ?", new Object[] {multimediaId});
    }

    public static List getUsagesForMultimediaId(int multimediaId) {
        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());
        return template.queryForList("select ContentId from multimediausage where MultimediaId = ?", new Object[] {multimediaId}, Integer.class);
    }

    public static void addUsageForContentId(int contentId, int multimediaId) {
        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());
        if (template.queryForInt("select count(*) from multimediausage where ContentId = ?", new Object[] {contentId}) == 0) {
            template.update("insert into multimediausage values (?,?)", new Object[] {contentId, multimediaId});
        }
    }
}
