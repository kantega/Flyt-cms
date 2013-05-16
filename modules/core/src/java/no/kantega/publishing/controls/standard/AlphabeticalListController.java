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

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.controls.AksessController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.Collator;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Controller that returns a model containing the subpages of the content it is defined on.
 */
public class AlphabeticalListController implements AksessController {

    private String associationCategory;
    private boolean skipAttributes = false;

    public Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContentManagementService cms = new ContentManagementService(request);

        ContentQuery query = new ContentQuery();

        if (isNotBlank(associationCategory)) {
            AssociationCategory association = cms.getAssociationCategoryByPublicId(associationCategory);
            if (association != null) {
                query.setAssociationCategory(association);
            }
        }

        Content current = (Content)request.getAttribute("aksess_this");
        ContentIdentifier cid = current.getContentIdentifier();
        query.setAssociatedId(cid);

        SortOrder sort = new SortOrder("title", false);
        List<Content> contentList;
        if(skipAttributes) {
            contentList = cms.getContentSummaryList(query, -1, sort);
        } else {
            contentList = cms.getContentList(query, -1, sort);
        }
        Map<String, List<Content>> letters = new TreeMap<>(Collator.getInstance(new Locale("no", "NO")));

        for (Content content : contentList) {
            String title = content.getTitle();
            String letter = title.substring(0, 1).toUpperCase();

            if (letters.get(letter) == null) {
                letters.put(letter, new ArrayList<Content>());
            }

            List<Content> links = letters.get(letter);
            links.add(content);
        }

        Map<String, Object> model = new HashMap<>();

        model.put("letters", letters);

        return model;
    }

    public String getDescription() {
        return "Alfabetisk liste";
    }

    public void setAssociationCategory(String associationCategory) {
        this.associationCategory = associationCategory;
    }

    public void setSkipAttributes(boolean skipAttributes) {
        this.skipAttributes = skipAttributes;
    }
}
