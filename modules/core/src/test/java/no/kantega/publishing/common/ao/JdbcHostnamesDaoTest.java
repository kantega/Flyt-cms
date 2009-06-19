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

import junit.framework.TestCase;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import no.kantega.publishing.test.database.HSQLDBDatabaseCreator;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 15, 2009
 * Time: 1:49:44 PM
 */
public class JdbcHostnamesDaoTest extends TestCase {
    public void testHostnames() {
        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-db.sql")).createDatabase();

        List<String> hostnames = new ArrayList<String>();
        hostnames.add("www.kantega.no");
        hostnames.add("kantega.no");
        hostnames.add("kurs.kantega.no");

        JdbcHostnamesDao hostnamesDao = new JdbcHostnamesDao();
        hostnamesDao.setDataSource(dataSource);
        hostnamesDao.setHostnamesForSiteId(1, hostnames);

        List<String> tmp = hostnamesDao.getHostnamesForSiteId(1);

        assertEquals(3, tmp.size());
        assertEquals("www.kantega.no", tmp.get(0));

    }    
}
