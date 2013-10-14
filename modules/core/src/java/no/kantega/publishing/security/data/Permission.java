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

package no.kantega.publishing.security.data;

import no.kantega.publishing.security.data.enums.NotificationPriority;

import java.io.Serializable;

public class Permission {
    private int privilege = -1;
    private NotificationPriority notificationPriority;
    private SecurityIdentifier securityIdentifier = null;

    public Permission() {
    }

    public Permission(Permission p) {
        this.privilege = p.getPrivilege();
        this.securityIdentifier = p.getSecurityIdentifier();
        this.notificationPriority = p.getNotificationPriority();
    }
   
    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

    public SecurityIdentifier getSecurityIdentifier() {
        return securityIdentifier;
    }

    public void setSecurityIdentifier(SecurityIdentifier securityIdentifier) {
        this.securityIdentifier = securityIdentifier;
    }

    public NotificationPriority getNotificationPriority() {
        return notificationPriority;
    }

    public void setNotificationPriority(NotificationPriority notificationPriority) {
        this.notificationPriority = notificationPriority;
    }
}
