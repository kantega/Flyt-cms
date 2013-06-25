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

package no.kantega.publishing.admin.content.behaviours.attributes;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 3, 2007
 * Time: 11:06:12 AM
 */
public class MapUserAttributeValueToContentPropertyBehaviour implements MapAttributeValueToContentPropertyBehaviour {
    private static final Logger log = LoggerFactory.getLogger(MapUserAttributeValueToContentPropertyBehaviour.class);
    private final static String SOURCE = "aksess.MapUserAttributeValueToContentPropertyBehaviour";

    public void mapAttributeValue(RequestParameters param, Content content, Attribute attribute, String field, ValidationErrors errors) {
        // Knytt verdien til bestemte felter hvis nødvendig
        String fullName = "";

        String userId = attribute.getValue();

        HttpServletRequest request = param.getRequest();
        try {
            if (userId != null && userId.length() > 0) {
                User u = SecuritySession.getInstance(request).getRealm().lookupUser(userId);
                if (u != null) {
                    fullName = u.getName();
                }
            }
        } catch (SystemException e) {
            log.error("Error getting user", e);
        }

        if (field != null) {
            if (field.equalsIgnoreCase(ContentProperty.TITLE)) {
                if (fullName.equals("")) fullName = "Uten tittel";
                content.setTitle(fullName);
            } else if (field.equalsIgnoreCase(ContentProperty.ALT_TITLE)) {
                content.setAltTitle(fullName);
            } else if (field.equalsIgnoreCase(ContentProperty.OWNER)) {
                content.setOwner(userId);
            } else if (field.equalsIgnoreCase(ContentProperty.OWNERPERSON)) {
                content.setOwnerPerson(userId);
            }
        }
    }
}
