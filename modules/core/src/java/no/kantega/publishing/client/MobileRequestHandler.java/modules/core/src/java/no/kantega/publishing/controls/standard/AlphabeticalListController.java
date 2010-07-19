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

package no.kantega.publishing.controls.standard;

import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.controls.AksessController;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.Collator;
import java.util.*;

/**
 * Author: Kristian Lier Selnæs, Kantega
 * Date: 22.mar.2007
 * Time: 13:41:37
 */
public class AlphabeticalListController implements AksessController {

    private String associationCategory;
    private String rootUrl;
    private boolean skipAttributes = false;

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContentManagementService cms = new ContentManagementService(request);

        ContentQuery query = new ContentQuery();

        if (associationCategory != null && !associationCategory.equals("")) {
            AssociationCategory association = cms.getAssociationCategoryByName(associationCategory);
            if (association != null) {
                query.setAssociationCategory(association);
            }
        }

        ContentIdentifier cid = null;
        if (rootUrl != null && !rootUrl.equals("")) {

            try {
                int rootId = Integer.parseInt(rootUrl);
                cid = new ContentIdentifier();
                cid.setAssociationId(rootId);
            } catch (NumberFormatException e) {
                int siteId = -1;
                Content current = (Content) request.getAttribute("aksess_this");
                if (current != null && current.getAssociation() != null) {
                    siteId = current.getAssociation().getSiteId();
                }
                try {
                    cid = ContentIdHelper.findContentIdentifier(siteId, rootUrl);
                } catch (ContentNotFoundException e1) {
                    Log.info("aksess.AlphabeticalListContoller", "Could not find cid for rootUrl: " + rootUrl, null, null);
                }
            }
            if (cid != null) {
                query.setPathElementId(cid);
            }

        } else {
            cid = new ContentIdentifier(request);
            query.setAssociatedId(cid);
        }

        SortOrder sort = new SortOrder("title", false);
        List contentList;
        if(skipAttributes) {
            contentList = cms.getContentSummaryList(query, -1, sort);
        } else {
            contentList = cms.getContentList(query, -1, sort);
        }
        Map letters = new TreeMap(Collator.getInstance(new Locale("no", "NO")));


        for (int i = 0; i < contentList.size(); i++) {
            Content content = (Content) contentList.get(i);
            String title = content.getTitle();
            String letter = title.substring(0, 1).toUpperCase();

            if(letters.get(letter) == null) {
                letters.put(letter, new ArrayList());
            }

            List links = (List) letters.get(letter);
            links.add(content);
        }

        Map model = new HashMap();

        model.put("letters", letters);

        return model;
    }

    public String getDescription() {
        return "Alfabetisk liste";
    }


    public void setAssociationCategory(String associationCategory) {
        this.associationCategory = associationCategory;
    }


    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public void setSkipAttributes(boolean skipAttributes) {
        this.skipAttributes = skipAttributes;
    }
}
