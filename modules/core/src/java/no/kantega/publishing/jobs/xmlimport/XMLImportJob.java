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
import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.api.runtime.ServerType;
import no.kantega.publishing.api.xmlcache.XMLCacheEntry;
import no.kantega.publishing.api.xmlcache.XmlCache;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class XMLImportJob   {
    private static final Logger log = LoggerFactory.getLogger(XMLImportJob.class);
    private String id;
    private String url;
    private XMLImportValidator validator = new DefaultXMLImportValidator();

    @Autowired
    private XmlCache xmlCache;
    @Autowired
    private ServerType serverType;

    @Autowired
    private SystemConfiguration configuration;

    private ServerType disableForServerType;

    private HttpClientBuilder httpClientBuilder;

    public void importXml() {
        if (Objects.equals(serverType, disableForServerType)) {
            log.info( "{} Import {} disabled for server type {}", id, url, disableForServerType);
            return;
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

    @PostConstruct
    private void init() {
        int timeout = configuration.getInt("httpclient.connectiontimeout", 10000);
        String proxyHost = configuration.getString("httpclient.proxy.host");
        String proxyPort = configuration.getString("httpclient.proxy.port");

        String proxyUser = configuration.getString("httpclient.proxy.username");

        String proxyPassword = configuration.getString("httpclient.proxy.password");
        if(isNotBlank(proxyHost)) {
            HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));

            httpClientBuilder = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setRedirectsEnabled(true)
                            .setConnectTimeout(timeout).build())
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
                            .setConnectTimeout(timeout).build());
        }
    }
}

