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

package no.kantega.publishing.security.realm;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class SecurityRealmFactory {
    private static final Logger log = LoggerFactory.getLogger(SecurityRealmFactory.class);

    public static SecurityRealm getInstance() throws SystemException {
        return getInstance(Aksess.getSecurityRealmName());
    }

    public static SecurityRealm getInstance(String realmName) throws SystemException {
        ApplicationContext context = RootContext.getInstance();
        SecurityRealm realm = (SecurityRealm) context.getBean(realmName);
        if (realm == null) {
            log.error("Realm with name" + realmName + " not found");
            throw new SystemException("Realm with name" + realmName + " not found", null);
        }

        return realm;
    }
}
