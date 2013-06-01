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
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.regex.Pattern;

public class ContentAliasValidator {

    private static Pattern validAliasPattern = Pattern.compile("^[\\w\\.\\-\\+=/\\&]*$");

    @Autowired
    private ContentIdentifierDao contentIdentifierDao;

    public void validateAlias(String alias, Content content, ValidationErrors errors) {
            if (!matchesAliasPattern(alias)) {
                errors.add(null, "aksess.error.aliasisillegal");
            }

            List<Association> associations = content.getAssociations();
            for (Association association : associations) {
                ContentIdentifier cid = contentIdentifierDao.getContentIdentifierBySiteIdAndAlias(association.getSiteId(), alias);
                if (cid != null && cid.getContentId() != content.getId() && cid.getSiteId() == association.getSiteId()) {
                    errors.add(null, "aksess.error.aliasinuse");
                    break;
                }
            }
    }

    public static boolean matchesAliasPattern(String alias) {
        return validAliasPattern.matcher(alias).matches();
    }
}
