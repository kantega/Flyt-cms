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

package no.kantega.publishing.common.cache;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.DocumentType;

import java.util.List;

/**
 * User: Anders Skar, Kantega AS
 * Date: Dec 17, 2008
 * Time: 2:10:49 PM
 */
public class DocumentTypeCache {
    public static List<DocumentType> getDocumentTypes() throws SystemException {
        return TemplateConfigurationCache.getInstance().getTemplateConfiguration().getDocumentTypes();
    }

    public static DocumentType getDocumentTypeById(int id) throws SystemException {
        List<DocumentType> documentTypes = getDocumentTypes();
        for (DocumentType dt : documentTypes) {
            if (dt.getId() == id) {
                return dt;
            }
        }
        return null;
    }

    public static DocumentType getDocumentTypeByPublicId(String id) throws SystemException {
        List<DocumentType> documentTypes = getDocumentTypes();
        for (DocumentType dt : documentTypes) {
            if (dt.getPublicId() != null && dt.getPublicId().equalsIgnoreCase(id)) {
                return dt;
            }
        }
        return null;
    }
}
