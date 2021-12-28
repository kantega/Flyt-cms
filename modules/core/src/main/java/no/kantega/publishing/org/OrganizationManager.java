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

package no.kantega.publishing.org;

import no.kantega.publishing.security.data.User;

import java.util.List;

/**
 *
 */
public interface OrganizationManager<O extends OrgUnit> {
    List<O> getChildUnits(OrgUnit unit);
    void doForUsersInOrgUnit(O unit, UserCallbackHandler handler);
    List<O> getOrgUnitsAboveUser(String  userId);
    boolean isUserInOrgUnit(String user, O unit);
    O getUnitByExternalId(String externalId);
    O getUnitByUser(User user);
    boolean isUnitLeader(User user);
    User getUnitLeader(O unit);
    List<O> searchOrgUnits(String phrase);
}
