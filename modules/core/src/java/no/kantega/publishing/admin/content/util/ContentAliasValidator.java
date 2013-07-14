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
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class ValidatorHelper {
    private static final Logger log = LoggerFactory.getLogger(ValidatorHelper.class);

    private static  final Pattern ALIAS_PATTERN = Pattern.compile("^[\\w\\.\\-\\+=/\\&]*$");

    public static void validateAlias(String alias, Content content, ValidationErrors errors) {

        if (!ALIAS_PATTERN.matcher(alias).matches()) {
            errors.add(null, "aksess.error.aliasisillegal");
        }

        try {
            ContentIdentifierDao contentIdentifierDao = RootContext.getInstance().getBean(ContentIdentifierDao.class);
            List<Association> associations = content.getAssociations();
            for (Association association : associations) {
                ContentIdentifier cid = contentIdentifierDao.getContentIdentifierBySiteIdAndAlias(association.getSiteId(), alias);
                if (cid != null && cid.getContentId() != content.getId() && cid.getSiteId() == association.getSiteId()) {
                    errors.add(null, "aksess.error.aliasinuse");
                    break;
                }
            }
        } catch (SystemException ex) {
            log.error("", ex);
        }
    }
}
