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

import org.springframework.scheduling.quartz.QuartzJobBean;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.XMLHelper;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.ao.XMLCacheAO;
import no.kantega.publishing.common.data.XMLCacheEntry;

import java.net.URL;
import java.net.MalformedURLException;

public class XMLImportJob  extends QuartzJobBean {
    private static final String SOURCE = "aksess.jobs.XMLImportJob";
    private String id  = null;
    private String url = null;

    protected void executeInternal(org.quartz.JobExecutionContext jobExecutionContext) throws org.quartz.JobExecutionException {
        if (id == null || url == null) {
            Log.error(SOURCE, "Manglende parameter id eller url", null, null);
            throw new JobExecutionException();
        }
        Log.debug(SOURCE, "XMLImport start:" + id + ", url:" + url, null, null);

        try {
            Document xml = XMLHelper.openDocument(new URL(url));

            XMLCacheEntry cacheEntry = new XMLCacheEntry(id, xml);
            XMLCacheAO.storeXMLInCache(cacheEntry);

        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        } catch (MalformedURLException e) {
            Log.error(SOURCE, e, null, null);
        }
        Log.debug(SOURCE, "XMLImport slutt:" + id, null, null);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}

