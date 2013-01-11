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

package no.kantega.publishing.admin.content.util;

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.RegExp;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.cache.ContentIdentifierCache;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;

import java.util.List;

public class ValidatorHelper {
    private static String SOURCE = "ValidatorHelper";

    public static void validateAlias(String alias, Content content, ValidationErrors errors) {
        String regexp = "^[\\w\\.\\-\\+=/\\&]*$";

        try {
            if (!RegExp.matches(regexp, alias)) {
                errors.add(null, "aksess.error.aliasisillegal");
            }
        } catch (RegExpSyntaxException e) {
            Log.error(SOURCE, e);
        }       

        try {
            List<Association> associations = content.getAssociations();
            for (Association association : associations) {
                ContentIdentifier cid = ContentIdentifierCache.getContentIdentifierByAlias(association.getSiteId(), alias);
                if (cid != null && cid.getContentId() != content.getId() && cid.getSiteId() == association.getSiteId()) {
                    errors.add(null, "aksess.error.aliasinuse");
                    break;
                }
            }
        } catch (SystemException ex) {
            Log.error(SOURCE, ex);
        }
    }
}
