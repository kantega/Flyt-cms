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

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class JdbcHostnamesDao implements HostnamesDao {
    private DataSource dataSource;

    @SuppressWarnings("unchecked")
    public List<String> getHostnamesForSiteId(int siteId) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.queryForList("select hostname from site2hostname where SiteId = ? order by IsDefault desc", new Object[] {siteId}, String.class);
    }

    public void setHostnamesForSiteId(int siteId, List<String> hostnames) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update("delete from site2hostname where SiteId = ?", siteId);

        boolean isDefault = true;
        for (String hostname : hostnames) {
            template.update("insert into site2hostname values (?,?,?)", siteId, hostname, isDefault);
            if (isDefault) isDefault = false;
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
