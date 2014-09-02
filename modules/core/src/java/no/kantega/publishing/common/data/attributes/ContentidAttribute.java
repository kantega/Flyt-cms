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
import no.kantega.publishing.admin.content.behaviours.attributes.ContentidAttributeValueXMLExporter;
import no.kantega.publishing.admin.content.behaviours.attributes.XMLAttributeValueExporter;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ContentidAttribute extends Attribute {
    private static final Logger log = LoggerFactory.getLogger(ContentidAttribute.class);
    private static ContentIdHelper contentIdHelper;

    protected boolean multiple = false;
    protected int maxitems = Integer.MAX_VALUE;
    protected String startId = "";
    protected String contentTemplate = "-1";

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            String multiple = config.getAttribute("multiple");
            if ("true".equalsIgnoreCase(multiple)) {
                this.multiple = true;
            }
            String maxitemsS = config.getAttribute("maxitems");
            if(isNotBlank(maxitemsS)) {
                maxitems = Integer.parseInt(maxitemsS);
            }
            String startIdS = config.getAttribute("startid");
            if(startIdS != null && startIdS.trim().length() > 0) {
                this.startId = startIdS;
            }
            String contentTemplateS = config.getAttribute("contenttemplate");
            if(contentTemplateS != null && contentTemplateS.trim().length() > 0) {
                this.contentTemplate = contentTemplateS;
            }
        }
    }

    public String getRenderer() {
        if (multiple) {
            return "contentid_multiple";
        } else {
            return "contentid";
        }
    }

    public int getMaxitems() {
        return maxitems;
    }

    public int getStartId(Content content) {
        int start = -1;

        if (isNotBlank(startId)) {
            try {
                start = Integer.parseInt(startId);
            } catch (NumberFormatException e) {
                try {
                    if(contentIdHelper == null){
                        contentIdHelper = RootContext.getInstance().getBean(ContentIdHelper.class);
                    }
                    ContentIdentifier cid = contentIdHelper.findRelativeContentIdentifier(content, startId);
                    start = cid.getAssociationId();
                } catch (ContentNotFoundException e1) {
                    log.error("", e);
                }
            }

        }
        return start;
    }

     public String getContentTemplate(){
        return contentTemplate;
    }


    public List<ContentIdentifier> getValueAsContentIdentifiers() {
        List<ContentIdentifier> cids = new ArrayList<>();
        if (isNotBlank(value)) {
            String[] values = value.split(",");
            for (String v : values) {
                ContentIdentifier cid =  ContentIdentifier.fromAssociationId(Integer.parseInt(v));
                cids.add(cid);
            }
        }

        return cids;
    }

    public XMLAttributeValueExporter getXMLAttributeValueExporter() {
        return new ContentidAttributeValueXMLExporter();
    }
}
