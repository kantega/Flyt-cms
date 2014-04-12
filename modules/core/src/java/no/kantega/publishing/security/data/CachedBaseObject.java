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

import no.kantega.publishing.api.model.BaseObject;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 29, 2008
 * Time: 10:04:49 AM
 */
public class CachedBaseObject {
    int securityId = -1;
    int objectType = -1;
    String owner = null;
    String ownerPerson = null;

    public CachedBaseObject(BaseObject obj) {
        this.securityId = obj.getSecurityId();
        this.objectType = obj.getObjectType();
        this.owner = obj.getOwner();
        if (this.owner == null) {
            this.owner = "";
        }
        this.ownerPerson = obj.getOwnerPerson();
        if (this.ownerPerson == null) {
            this.ownerPerson = "";
        }
    }

    public boolean isSameAs(Object obj) {
        if (obj instanceof BaseObject) {
            BaseObject b = (BaseObject)obj;

            if (b.getObjectType() == objectType && b.getSecurityId() == securityId) {
                if (!owner.equals(b.getOwner()) || !ownerPerson.equals(b.getOwnerPerson())) {
                    return false;
                }
                return true;
            }
        }

        return false;
    }
}
