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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.XMLHelper;
import no.kantega.publishing.cache.CacheManagerFactory;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.XMLCacheAO;
import no.kantega.publishing.common.data.XMLCacheEntry;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.spring.RootContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.w3c.dom.Document;

import java.net.URL;
import java.net.MalformedURLException;

public class XMLImportJob  extends QuartzJobBean {
    private static final String SOURCE = "aksess.jobs.XMLImportJob";
    private String id  = null;
    private String url = null;
    private XMLImportValidator validator = new DefaultXMLImportValidator();


    protected void executeInternal(org.quartz.JobExecutionContext jobExecutionContext) throws org.quartz.JobExecutionException {
        
        if (Aksess.getServerType() == ServerType.SLAVE) {
            Log.info(SOURCE, "Job is disabled for server type slave", null, null);
            return;
        }

        if (id == null || url == null) {
            Log.error(SOURCE, "Missing parameter id or url", null, null);
            throw new JobExecutionException();
        }
        Log.info(SOURCE, "XMLImport started:" + id + ", url:" + url);

        try {
            Document xml = XMLHelper.openDocument(new URL(url));

            if (isValidXML(xml)) {
                XMLCacheEntry cacheEntry = new XMLCacheEntry(id, xml);
                XMLCacheAO.storeXMLInCache(cacheEntry);

                CacheManager cacheManager = (CacheManager) RootContext.getInstance().getBean("cacheManager");

                Cache xmlCache = cacheManager.getCache(CacheManagerFactory.CacheNames.XmlCache.name());

                xmlCache.remove((Object) id);
            }

        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        } catch (MalformedURLException e) {
            Log.error(SOURCE, e, null, null);
        }
        Log.info(SOURCE, "XMLImport ended:" + id, null, null);
    }

    private boolean isValidXML(Document xml) {
        if (xml == null) {
            return false;
        }

        boolean isValid = validator.isValidXML(xml);
        if (!isValid) {
            Log.error(SOURCE, "Validator failed, skipping XML import (" + id + ") from URL:" + url);
        }

        return isValid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setValidator(XMLImportValidator validator) {
        this.validator = validator;
    }
}

