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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/testContext.xml")
public class JdbcHostnamesDaoTest {

    @Autowired
    private HostnamesDao hostnamesDao;

    @Test
    public void testHostnames() {

        List<String> hostnames = new ArrayList<>();
        hostnames.add("www.kantega.no");
        hostnames.add("kantega.no");
        hostnames.add("kurs.kantega.no");

        hostnamesDao.setHostnamesForSiteId(1, hostnames);

        List<String> tmp = hostnamesDao.getHostnamesForSiteId(1);

        assertEquals(3, tmp.size());
        assertEquals("www.kantega.no", tmp.get(0));

    }
}
