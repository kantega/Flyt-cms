/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.security.util;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.util.SecurityHelper;
import no.kantega.security.api.profile.Profile;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: Kristian Selnæs
 * Date: 12.apr.2010
 * Time: 12:57:50
 */
public class SecurityHelperTest {


    @Test
    public void shouldMapNullUserToNullApiProfile(){
        assertNull(SecurityHelper.createApiProfile(null));
    }


    @Test
    public void shouldMapUserToApiProfile(){

        Properties properties = new Properties();
        properties.put("prop1", "prop1_value");
        properties.put("prop2", "prop2_value");

        User user = new User();
        user.setId("someid");
        user.setAttributes(properties);
        user.setDepartment("FAD");
        user.setEmail("test@kantega.no");
        user.setGivenName("Ola");
        user.setSurname("Svenske");

        Profile profile = SecurityHelper.createApiProfile(user);

        assertEquals(profile.getIdentity().getUserId(), user.getId());
        assertEquals(profile.getIdentity().getDomain(), Aksess.getDefaultSecurityDomain());
        assertEquals(profile.getRawAttributes(), user.getAttributes());
        assertEquals(profile.getDepartment(), user.getDepartment());
        assertEquals(profile.getEmail(), user.getEmail());
        assertEquals(profile.getGivenName(), user.getGivenName());
        assertEquals(profile.getSurname(), user.getSurname());

    }

    @Test
    public void shouldMapUserWithDomainToApiProfileWithDomain(){
        User user = new User();
        user.setId("domain1:user1");
        Profile profile = SecurityHelper.createApiProfile(user);
        assertEquals(profile.getIdentity().getUserId(), "user1");
        assertEquals(profile.getIdentity().getDomain(), "domain1");


    }

}
