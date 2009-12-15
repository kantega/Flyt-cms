/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.search.control;

import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.search.index.Fields;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
class HitCountHelper {
    public static String[] getDocumentTypes() {
        List<DocumentType> documentTypes = DocumentTypeCache.getDocumentTypes();
        String[] docTypeIds = new String[documentTypes.size()];
        for (int i = 0; i < documentTypes.size(); i++) {
            docTypeIds[i] = "" + documentTypes.get(i).getId();
        }
        return docTypeIds;
    }

    /**
     * Gets a list of subpages based on parentid, if no parentid is specified, gets subpages under root
     * @param siteId
     * @param request
     * @return
     */
    public static String[] getParents(int siteId, HttpServletRequest request) {
        ContentManagementService cms = new ContentManagementService(request);

        RequestParameters param = new RequestParameters(request);
        int parentId = param.getInt(Fields.CONTENT_PARENTS);

        try {
            ContentIdentifier cid;
            if (parentId == -1) {
                cid = new ContentIdentifier(siteId, "/");
            } else {
                cid = new ContentIdentifier();
                cid.setAssociationId(parentId);
            }
            ContentQuery query = new ContentQuery();
            query.setAssociatedId(cid);
            List<Content> pages = cms.getContentSummaryList(query, -1, new SortOrder(ContentProperty.TITLE, false));
            String[] parents = new String[pages.size()];
            for (int i = 0; i < pages.size(); i++) {
                parents[i] = "" + pages.get(i).getAssociation().getId();
            }
            return parents;
        } catch (ContentNotFoundException e) {
            Log.error("HitCountHelper", e, null, null);
        }

        return null;
    }
}
