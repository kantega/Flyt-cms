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

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.XMLHelper;
import no.kantega.publishing.api.xmlcache.XMLCacheEntry;
import no.kantega.publishing.api.xmlcache.XmlCache;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.ServerType;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.w3c.dom.Document;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class XMLImportJob  extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(XMLImportJob.class);
    private String id  = null;
    private String url = null;
    private XMLImportValidator validator = new DefaultXMLImportValidator();
    private XmlCache xmlCache;

    private HttpClientBuilder httpClientBuilder;
    private final int CONNECTION_TIMEOUT = 10000;

    protected void executeInternal(org.quartz.JobExecutionContext jobExecutionContext) throws org.quartz.JobExecutionException {
        xmlCache = (XmlCache)jobExecutionContext.getMergedJobDataMap().get("xmlCache");
        init();
        if (Aksess.getServerType() == ServerType.SLAVE) {
            log.info( "Job is disabled for server type slave");
            return;
        }

        if (id == null || url == null) {
            log.error( "Missing parameter id or url");
            throw new JobExecutionException();
        }
        log.info( "XMLImport started:" + id + ", url:" + url);

        try (CloseableHttpResponse execute = httpClientBuilder.build().execute(new HttpGet(url))){
            Document xml = XMLHelper.openDocument(execute.getEntity().getContent());

            if (isValidXML(xml)) {
                XMLCacheEntry cacheEntry = new XMLCacheEntry(id, xml);
                xmlCache.storeXMLInCache(cacheEntry);
            }

        } catch (SystemException | IOException e) {
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

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setValidator(XMLImportValidator validator) {
        this.validator = validator;
    }

    private void init() {
        Configuration configuration = Aksess.getConfiguration();
        String proxyHost = configuration.getString("linkchecker.proxy.host");
        String proxyPort = configuration.getString("linkchecker.proxy.port");

        String proxyUser = configuration.getString("linkchecker.proxy.username");

        String proxyPassword = configuration.getString("linkchecker.proxy.password");
        if(isNotBlank(proxyHost)) {
            HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));

            httpClientBuilder = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setRedirectsEnabled(true)
                            .setConnectTimeout(CONNECTION_TIMEOUT).build())
                    .setProxy(proxy);

            if(isNotBlank(proxyUser)) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPassword));
                httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
            }
        } else {
            httpClientBuilder = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setRedirectsEnabled(true)
                            .setConnectTimeout(CONNECTION_TIMEOUT).build());
        }
    }
}

