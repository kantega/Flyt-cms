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
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ContentlistAttribute extends ListAttribute {
    private static final Logger log = LoggerFactory.getLogger(ContentlistAttribute.class);

    private ContentAO contentAO;

    protected String contentTemplateId = null;
    protected int documentTypeId = -1;
    protected String siteId;
    protected int currentSiteId = -1;
    protected boolean showEmptyOption = false;

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            contentTemplateId = config.getAttribute("contenttemplate");
            setDoctype(config);
            this.siteId = config.getAttribute("site");
            this.showEmptyOption = !"false".equals(config.getAttribute("showemptyoption"));
        }
    }

    private void setDoctype(Element config) {
        String docType = config.getAttribute("documenttype");
        if (docType != null && docType.length() > 0) {
            DocumentType dt = DocumentTypeCache.getDocumentTypeByPublicId(docType);
            if (dt != null) {
                documentTypeId = dt.getId();
            }
        }
    }

    public int getDocumentTypeId() {
        return documentTypeId;
    }

    public String getSiteId() {
        return siteId;
    }

    public List<ListOption> getListOptions(int language) {
        int requestedSiteId = -1;
        ContentQuery query = new ContentQuery();
        setContentTemplateId(query);
        setDocumentTypeId(query);
        setSiteId(requestedSiteId, query);

        setLanguage(language, query);

        return createListOptions(query);
    }

    private List<ListOption> createListOptions(ContentQuery query) {
        List<ListOption> options = new ArrayList<>();
        addEmptyOption(options);
        try {
            if(contentAO == null){
                contentAO = RootContext.getInstance().getBean(ContentAO.class);
            }
            List<Content> all = contentAO.getContentList(query, -1, new SortOrder(ContentProperty.TITLE, false), false);
            for (Content c : all) {
                String id = String.valueOf(c.getAssociation().getId());
                ListOption option = new ListOption();
                option.setText(c.getTitle());
                option.setValue(id);
                options.add(option);
            }
        } catch (SystemException e) {
            log.error("", e);
        }

        return options;
    }

    private void addEmptyOption(List<ListOption> options) {
        // When multiple is true, checkboxes are chosen so it is not necessary to show empty option.
        if (!multiple && showEmptyOption) {
            ListOption emptyOption = new ListOption();
            options.add(emptyOption);
        }
    }

    private void setLanguage(int language, ContentQuery query) {
        if (language != -1 ) {
            query.setLanguage(language);
        }
    }

    private void setContentTemplateId(ContentQuery query) {
        if (isNotBlank(contentTemplateId)){
            query.setContentTemplate(contentTemplateId);
        }
    }

    private void setDocumentTypeId(ContentQuery query) {
        if (documentTypeId != -1) {
            query.setDocumentType(documentTypeId);
        }
    }

    private void setSiteId(int requestedSiteId, ContentQuery query) {
        if("$SITE".equals(siteId)){
            requestedSiteId = currentSiteId;
        }else{
            if (isNotBlank(siteId)) {

                try {
                    requestedSiteId = Integer.parseInt(siteId);
                } catch (NumberFormatException e) {
                    // site er et alias
                    Site s = RootContext.getInstance().getBean(SiteCache.class).getSiteByPublicIdOrAlias(siteId);
                    if(s != null) {
                        requestedSiteId = s.getId();
                    }
                }
            }
        }
        query.setSiteId(requestedSiteId);
    }

    public List<ContentIdentifier> getValueAsContentIdentifiers() {
        List<String> values = super.getValues();
        List<ContentIdentifier> cids = new ArrayList<>(values.size());
        for (String v : values) {
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(Integer.parseInt(v));
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
