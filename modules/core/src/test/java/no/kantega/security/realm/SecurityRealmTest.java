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

package no.kantega.security.realm;

import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.util.SecurityHelper;
import no.kantega.security.api.common.SystemException;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.profile.DefaultProfile;
import no.kantega.security.api.profile.ProfileManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;


public class SecurityRealmTest {

    private SecurityRealm realm = new SecurityRealm();
    @Mock private ProfileManager profileManager;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        realm.setProfileManager(profileManager);
    }

    @Test
    public void assertThatLookupUserIsCachingUser() throws SystemException {
        String userid = "krisel";

        Identity identity  = SecurityHelper.createApiIdentity(userid);
        DefaultProfile expected = new DefaultProfile();
        expected.setIdentity(identity);

        when(profileManager.getProfileForUser(refEq(identity))).thenReturn(expected);
        User actual = realm.lookupUser(userid, true);
        verify(profileManager).getProfileForUser(refEq(identity));

        assertEquals(expected.getIdentity().getUserId(), actual.getId());

        User actualCachedUser = realm.lookupUser(userid, true);
        verifyNoMoreInteractions(profileManager);

        assertEquals(expected.getIdentity().getUserId(), actualCachedUser.getId());
    }

    @Test
    public void assertThatLookupUserIsNotCachingUser() throws SystemException {
        String userid = "krisel";

        Identity identity  = SecurityHelper.createApiIdentity(userid);
        DefaultProfile expected = new DefaultProfile();
        expected.setIdentity(identity);

        when(profileManager.getProfileForUser(refEq(identity))).thenReturn(expected);

        User actual = realm.lookupUser(userid, false);
        assertEquals(expected.getIdentity().getUserId(), actual.getId());

        User actualCachedUser = realm.lookupUser(userid, false);
        assertEquals(expected.getIdentity().getUserId(), actualCachedUser.getId());
        
        verify(profileManager, times(2)).getProfileForUser(refEq(identity));

    }
}
