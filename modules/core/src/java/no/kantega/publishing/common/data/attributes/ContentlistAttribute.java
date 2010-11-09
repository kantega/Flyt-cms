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

package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContentlistAttribute extends ListAttribute {
    private static String SOURCE = "aksess.ContentlistAttribute";

    protected String contentTemplateId = null;
    protected int documentTypeId = -1;
    protected String siteId;
    protected int currentSiteId = -1;


    public void setConfig(Element config, Map model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            contentTemplateId = config.getAttribute("contenttemplate");
            String docType = config.getAttribute("documenttype");
            if (docType != null && docType.length() > 0) {
                DocumentType dt = DocumentTypeCache.getDocumentTypeByPublicId(docType);
                if (dt != null) {
                    documentTypeId = dt.getId();
                }
            }
            this.siteId = config.getAttribute("site");

        }
    }

    public int getDocumentTypeId() {
        return documentTypeId;
    }

    public String getSiteId() {
        return siteId;
    }

    public List getListOptions(int language) {
        int requestedSiteId = -1;
        ContentQuery query = new ContentQuery();
        if (contentTemplateId != null && contentTemplateId != ""){
            query.setContentTemplate(contentTemplateId);
        }
        if (documentTypeId != -1) {
            query.setDocumentType(documentTypeId);
        }
        if("$SITE".equals(siteId)){
            requestedSiteId = currentSiteId;
        }else{
            if (siteId != null && siteId.trim().length() > 0) {

                try {
                    requestedSiteId = Integer.parseInt(siteId);
                } catch (NumberFormatException e) {
                    // site er et alias
                    Site s = SiteCache.getSiteByPublicIdOrAlias(siteId);
                    if(s != null) {
                        requestedSiteId = s.getId();
                    }
                } 
            }
        }
        query.setSiteId(requestedSiteId);

        if (language != -1 ) {
            query.setLanguage(language);
        }

        List options = new ArrayList();
        try {
            List all = ContentAO.getContentList(query, -1, new SortOrder(ContentProperty.TITLE, false),  false);
            for (int i = 0; i < all.size(); i++) {
                Content c = (Content) all.get(i);
                String id = "" + c.getAssociation().getId();
                ListOption option = new ListOption();
                option.setText(c.getTitle());
                option.setValue(id);
                options.add(option);
            }
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        }
        return options;
    }

    public List<ContentIdentifier> getValueAsContentIdentifiers() {
        List<ContentIdentifier> cids = new ArrayList<ContentIdentifier>();
        List<String> values = super.getValues();
        for (String v : values) {
            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(Integer.parseInt(v));
            cids.add(cid);
        }
        return cids;
    }

    public void setCurrentSiteId(int currentSiteId) {
        this.currentSiteId = currentSiteId;
    }

    public String getRenderer() {
        return "contentlist";
    }

}
