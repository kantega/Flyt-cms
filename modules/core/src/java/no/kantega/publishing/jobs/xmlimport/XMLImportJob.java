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

package no.kantega.publishing.jobs.xmlimport;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.XMLHelper;
import no.kantega.publishing.api.runtime.ServerType;
import no.kantega.publishing.api.xmlcache.XMLCacheEntry;
import no.kantega.publishing.api.xmlcache.XmlCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class XMLImportJob   {
    private static final Logger log = LoggerFactory.getLogger(XMLImportJob.class);
    private String id;
    private String url;
    private XMLImportValidator validator = new DefaultXMLImportValidator();
    private XmlCache xmlCache;
    @Autowired
    private ServerType serverType;

    private ServerType disableForServerType;

    protected void importXml() {
        if (Objects.equals(serverType, disableForServerType)) {
            log.info( "{} Import {} disabled for server type {}", id, url, disableForServerType);
            return;
        }

        log.info( "XMLImport started:" + id + ", url:" + url);

        try {
            Document xml = XMLHelper.openDocument(new URL(url));

            if (isValidXML(xml)) {
                XMLCacheEntry cacheEntry = new XMLCacheEntry(id, xml);
                xmlCache.storeXMLInCache(cacheEntry);
            }

        } catch (SystemException | MalformedURLException e) {
            log.error("", e);
        }
        log.info( "XMLImport ended:" + id);
    }

    private boolean isValidXML(Document xml) {
        if (xml == null) {
            return false;
        }

        boolean isValid = validator.isValidXML(xml);
        if (!isValid) {
            log.error( "Validator failed, skipping XML import (" + id + ") from URL:" + url);
        }

        return isValid;
    }

    @Required
    public void setId(String id) {
        this.id = id;
    }

    @Required
    public void setUrl(String url) {
        this.url = url;
    }

    public void setValidator(XMLImportValidator validator) {
        this.validator = validator;
    }

    public void setDisableForServerType(ServerType disableForServerType) {
        this.disableForServerType = disableForServerType;
    }
}

