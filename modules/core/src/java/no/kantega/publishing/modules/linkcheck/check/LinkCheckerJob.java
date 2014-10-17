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

package no.kantega.publishing.modules.linkcheck.check;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.runtime.ServerType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.util.Counter;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LinkCheckerJob implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(LinkCheckerJob.class);
    public static final String CONTENT = Aksess.VAR_WEB + Aksess.CONTENT_URL_PREFIX + "/";
    public static final String CONTENT_AP = Aksess.VAR_WEB + "/content.ap?thisId=";
    private static final String MULTIMEDIA_AP = Aksess.VAR_WEB +"/multimedia.ap?id=";
    private static final String MULTIMEDIA = Aksess.VAR_WEB + "/" + Aksess.MULTIMEDIA_URL_PREFIX;
    private static final String ATTACHMENT_AP = Aksess.VAR_WEB +"/" + Aksess.ATTACHMENT_REQUEST_HANDLER +"?id=";
    private static final String ATTACHMENT = Aksess.VAR_WEB +"/" + Aksess.ATTACHMENT_URL_PREFIX;
    private final int CONNECTION_TIMEOUT = 10000;

    private String webroot = "http://localhost";
    private String proxyHost;
    private int proxyPort = 8080;
    private String proxyUser;
    private String proxyPassword;

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private ContentAO contentAO;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Autowired
    private MultimediaAO multimediaAO;
    private HttpClientBuilder httpClientBuilder;

    @PostConstruct
    public void init() {
        if(isNotBlank(proxyHost)) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);

            httpClientBuilder = HttpClients.custom()
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

    public void execute() {
        if (Aksess.getServerType() == ServerType.SLAVE) {
            log.info( "Job is disabled for server type slave");
            return;
        } else {
            log.info("Started LinkCheckerJob");
        }

        if(!Aksess.isLinkCheckerEnabled()) {
            return;
        }
        Date week = new Date(System.currentTimeMillis() - 1000*60*60*24*7);

        int noLinks = linkDao.getNumberOfLinks();
        int maxLinksPerDay = 1000;
        if (noLinks > 7*maxLinksPerDay) {
            maxLinksPerDay = (noLinks/7) + 100;
        }

        log.debug("Found {} total links in database", noLinks);
        NotCheckedSinceTerm term = new NotCheckedSinceTerm(week, maxLinksPerDay);

        final Counter linkCounter = new Counter();

        long start = System.currentTimeMillis();
        try (CloseableHttpClient client = getHttpClient()){
            linkDao.doForEachLink(term, new LinkHandler() {
                public void handleLink(int id, String link, LinkOccurrence occurrence) {
                    if(link.startsWith("http")) {
                        checkRemoteUrl(link, occurrence, client);
                    } else if(link.startsWith(Aksess.VAR_WEB)) {
                        checkInternalLink(link, occurrence, client);
                    }

                    linkCounter.increment();
                }
            });
        } catch (IOException e) {
            log.error("Error with HttpClient", e);
        }
        log.info("Checked {} links in {} ms.", linkCounter.getI(), (System.currentTimeMillis()-start));

    }

    private CloseableHttpClient getHttpClient() {
        return httpClientBuilder.build();
    }

    private void checkInternalLink(String link, LinkOccurrence occurrence, CloseableHttpClient client) {
        log.debug("Checking internal url {}", link);
        if (link.startsWith(CONTENT_AP) || link.startsWith(CONTENT)) {
            checkContent(link, occurrence, client);

        } else if (link.startsWith(MULTIMEDIA_AP) || link.startsWith(MULTIMEDIA)) {
            checkMultimedia(link, occurrence, client);

        } else if (link.startsWith(ATTACHMENT_AP) || link.startsWith(ATTACHMENT)) {
            checkAttachment(link, occurrence, client);

        } else if (link.startsWith(Aksess.VAR_WEB + "/") && link.endsWith("/")) {
            checkAlias(link, occurrence, client);

        } else {
            checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
        }
    }

    private void checkContent(String link, LinkOccurrence occurrence, CloseableHttpClient client) {
        // Side i AP
        String idPart;
        if (link.startsWith(CONTENT_AP)) {
            idPart = link.substring(CONTENT_AP.length());
            if (idPart.contains("&")) {
                idPart = idPart.substring(0, idPart.indexOf('&'));
            }
        } else {
            idPart = link.substring(CONTENT.length());
            if (idPart.contains("/")) {
                idPart = idPart.substring(0, idPart.indexOf('/'));
            }
        }
        try {
            int i = Integer.parseInt(idPart);
            try {
                ContentIdentifier cid =  ContentIdentifier.fromAssociationId(i);
                Content c = contentAO.getContent(cid, true);
                if(c != null) {
                    occurrence.setStatus(CheckStatus.OK);
                } else {
                    occurrence.setStatus(CheckStatus.CONTENT_AP_NOT_FOUND);
                }
            } catch (SystemException e) {
                log.error("Error getting content with associationId  " + i, e);
                occurrence.setStatus(CheckStatus.CONTENT_AP_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
        }
    }

    private void checkMultimedia(String link, LinkOccurrence occurrence, CloseableHttpClient client) {
        // Bilde / multimedia
        String idPart;
        if (link.startsWith(MULTIMEDIA_AP)) {
            idPart = link.substring(MULTIMEDIA_AP.length());
            if (idPart.contains("&")) {
                idPart = idPart.substring(0, idPart.indexOf('&'));
            }
        } else {
            idPart = link.substring(MULTIMEDIA.length());
            if (idPart.contains("/")) {
                idPart = idPart.substring(0, idPart.indexOf('/'));
            }
        }
        try {
            int i = Integer.parseInt(idPart);
            try {
                Multimedia attachment = multimediaAO.getMultimedia(i);

                if(attachment != null) {
                    occurrence.setStatus(CheckStatus.OK);
                } else {
                    occurrence.setStatus(CheckStatus.MULTIMEDIA_AP_NOT_FOUND);
                }
            } catch (SystemException e) {
                log.error("Error getting multimedia with id " + i, e);
                occurrence.setStatus(CheckStatus.MULTIMEDIA_AP_NOT_FOUND);
            }

        } catch(NumberFormatException e) {
            checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
        }
    }

    private void checkAttachment(String link, LinkOccurrence occurrence, CloseableHttpClient client) {
        // Vedlegg
        String idPart;
        if (link.startsWith(ATTACHMENT_AP)) {
            idPart = link.substring(ATTACHMENT_AP.length());
            if (idPart.contains("&")) {
                idPart = idPart.substring(0, idPart.indexOf('&'));
            }
        } else {
            idPart = link.substring(ATTACHMENT.length());
            if (idPart.contains("/")) {
                idPart = idPart.substring(0, idPart.indexOf('/'));
            }
        }
        try {
            int i = Integer.parseInt(idPart);
            try {
                Attachment attachment = AttachmentAO.getAttachment(i);

                if(attachment != null) {
                    occurrence.setStatus(CheckStatus.OK);
                } else {
                    occurrence.setStatus(CheckStatus.ATTACHMENT_AP_NOT_FOUND);
                }
            } catch (SystemException e) {
                log.error("Error getting attachment with id " + i, e);
                occurrence.setStatus(CheckStatus.ATTACHMENT_AP_NOT_FOUND);
            }

        } catch (NumberFormatException e) {
            checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
        }
    }

    private void checkAlias(String link, LinkOccurrence occurrence, CloseableHttpClient client) {
        // Kan være et alias, sjekk
        String alias = link.substring(Aksess.VAR_WEB.length());
        try {
            ContentIdentifier cid = contentIdHelper.fromUrl(alias);
            Content c = contentAO.getContent(cid, true);
            if (c != null) {
                occurrence.setStatus(CheckStatus.OK);
            } else {
                occurrence.setStatus(CheckStatus.CONTENT_AP_NOT_FOUND);
            }

        } catch (ContentNotFoundException e) {
            // Ikke et alias eller slettet alias
            checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
        } catch (SystemException e) {
            log.error("Error checking alias " + alias, e);
            occurrence.setStatus(CheckStatus.IO_EXCEPTION);
        }
    }

    private void checkRemoteUrl(String link, LinkOccurrence occurrence, CloseableHttpClient client) {
        log.debug("Checking remote url {}", link);
        HttpGet get;
        try {
            get = new HttpGet(link);
        } catch (Exception e) {
            occurrence.setStatus(CheckStatus.INVALID_URL);
            return;
        }

        int httpStatus = -1;
        CheckStatus status = CheckStatus.OK;

        try (CloseableHttpResponse response = client.execute(get)){
            log.debug("Checking remote url {}, before client.execute(get)", link);

            httpStatus = response.getStatusLine().getStatusCode();
            log.debug("Checking remote url {}, after client.execute(get), status: {}", link, httpStatus);

            if (httpStatus != HttpStatus.SC_OK && httpStatus != HttpStatus.SC_UNAUTHORIZED && httpStatus != HttpStatus.SC_MULTIPLE_CHOICES && httpStatus != HttpStatus.SC_MOVED_TEMPORARILY && httpStatus != HttpStatus.SC_TEMPORARY_REDIRECT) {
                status = CheckStatus.HTTP_NOT_200;
            }
        } catch (UnknownHostException e) {
            status = CheckStatus.UNKNOWN_HOST;
        } catch (ConnectTimeoutException e) {
            status = CheckStatus.CONNECTION_TIMEOUT;
        } catch(ConnectException e) {
            status = CheckStatus.CONNECT_EXCEPTION;
        } catch (IOException e) {
            status = CheckStatus.IO_EXCEPTION;
        }  catch (Exception e) {
            status = CheckStatus.INVALID_URL;
        }
        occurrence.setStatus(status);
        occurrence.setHttpStatus(httpStatus);
    }

    public void setWebroot(String webroot) {
        this.webroot = webroot;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public void setLinkDao(LinkDao linkDao) {
        this.linkDao = linkDao;
    }

    public void afterPropertiesSet() throws Exception {
        setWebroot(Aksess.getApplicationUrl());
        Configuration configuration = Aksess.getConfiguration();
        setProxyHost(configuration.getString("linkchecker.proxy.host"));
        String proxyPort = configuration.getString("linkchecker.proxy.port");
        if(proxyPort != null) {
            setProxyPort(Integer.parseInt(proxyPort));
        }
        String proxyUser = configuration.getString("linkchecker.proxy.username");
        setProxyUser(proxyUser);

        String proxyPassword = configuration.getString("linkchecker.proxy.password");
        setProxyPassword(proxyPassword);
    }

}
