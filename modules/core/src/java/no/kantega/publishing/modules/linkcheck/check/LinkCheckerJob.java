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

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.sqlsearch.SearchTerm;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.util.Counter;
import no.kantega.publishing.modules.linkcheck.sqlsearch.NotCheckedSinceTerm;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Date;

public class LinkCheckerJob implements InitializingBean {
    private Logger log = Logger.getLogger(getClass());
    public static final String CONTENT = Aksess.VAR_WEB + Aksess.CONTENT_URL_PREFIX + "/";
    public static final String CONTENT_AP = Aksess.VAR_WEB + "/content.ap?thisId=";
    private static final String MULTIMEDIA_AP = Aksess.VAR_WEB +"/multimedia.ap?id=";
    private static final String MULTIMEDIA = Aksess.VAR_WEB + "/" + Aksess.MULTIMEDIA_URL_PREFIX;
    private static final String ATTACHMENT_AP = Aksess.VAR_WEB +"/" + Aksess.ATTACHMENT_REQUEST_HANDLER +"?id=";

    private String webroot = "http://localhost";
    private String proxyHost;
    private int proxyPort = 8080;
    private String proxyUser;
    private String proxyPassword;

    @Autowired
    private LinkDao linkDao;

    public void execute() {
        if (Aksess.getServerType() == ServerType.SLAVE) {
            Log.info(getClass().getName(), "Job is disabled for server type slave", null, null);
            return;
        }

        if(!Aksess.isLinkCheckerEnabled()) {
            return;
        }
        final HttpClient client = new HttpClient();

        if(proxyHost != null && !proxyHost.equals("")) {
            client.getHostConfiguration().setProxy(proxyHost, proxyPort);
            if(proxyUser != null && !proxyUser.equals("")) {
                client.getState().setProxyCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPassword));
            }
        }

        client.setConnectionTimeout(10000);
        Date week = new Date(System.currentTimeMillis() - 1000*60*60*24*7);
        SearchTerm term = new NotCheckedSinceTerm(week);

        int noLinks = linkDao.getNumberOfLinks();
        int maxLinksPerDay = 1000;
        if (noLinks > 7*maxLinksPerDay) {
            maxLinksPerDay = (noLinks/7) + 100;
        }

        log.debug("Found " + noLinks + " total links in database");

        term.setMaxResults(maxLinksPerDay);

        final Counter linkCounter = new Counter();

        long start = System.currentTimeMillis();
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
        log.info("Checked " +linkCounter.getI() +" links in " + (System.currentTimeMillis()-start) +" ms.");
    }

    private void checkInternalLink(String link, LinkOccurrence occurrence, HttpClient client) {
        if (link.startsWith(CONTENT_AP) || link.startsWith(CONTENT)) {
            // Side i AP
            String idPart;
            if (link.startsWith(CONTENT_AP)) {
                idPart = link.substring(CONTENT_AP.length());
                if (idPart.indexOf("&") != -1) {
                    idPart = idPart.substring(0, idPart.indexOf("&"));
                }
            } else {
                idPart = link.substring(CONTENT.length());
                if (idPart.indexOf("/") != -1) {
                    idPart = idPart.substring(0, idPart.indexOf("/"));
                }
            }
            try {
                int i = Integer.parseInt(idPart);
                try {
                    ContentIdentifier cid = new ContentIdentifier();
                    cid.setAssociationId(i);
                    Content c = ContentAO.getContent(cid, true);
                    if(c != null) {
                        occurrence.setStatus(CheckStatus.OK);
                    } else {
                        occurrence.setStatus(CheckStatus.CONTENT_AP_NOT_FOUND);
                    }
                } catch (SystemException e) {
                    occurrence.setStatus(CheckStatus.CONTENT_AP_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
            }
        } else if (link.startsWith(MULTIMEDIA_AP) || link.startsWith(MULTIMEDIA_AP)) {
            // Bilde / multimedia
            String idPart;
            if (link.startsWith(MULTIMEDIA_AP)) {
                idPart = link.substring(MULTIMEDIA_AP.length());
                if (idPart.indexOf("&") != -1) {
                    idPart = idPart.substring(0, idPart.indexOf("&"));
                }
            } else {
                idPart = link.substring(MULTIMEDIA.length());
                if (idPart.indexOf("/") != -1) {
                    idPart = idPart.substring(0, idPart.indexOf("/"));
                }
            }
            try {
                int i = Integer.parseInt(idPart);
                try {
                    Multimedia attachment = MultimediaAO.getMultimedia(i);

                    if(attachment != null) {
                        occurrence.setStatus(CheckStatus.OK);
                    } else {
                        occurrence.setStatus(CheckStatus.MULTIMEDIA_AP_NOT_FOUND);
                    }
                } catch (SystemException e) {
                    occurrence.setStatus(CheckStatus.MULTIMEDIA_AP_NOT_FOUND);
                }

            } catch(NumberFormatException e) {
                checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
            }
        } else if (link.startsWith(ATTACHMENT_AP)) {
            // Vedlegg
            String idPart = link.substring(ATTACHMENT_AP.length());
            if (idPart.indexOf("&") != -1) {
                idPart = idPart.substring(0, idPart.indexOf("&"));
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
                    occurrence.setStatus(CheckStatus.ATTACHMENT_AP_NOT_FOUND);
                }

            } catch (NumberFormatException e) {
                checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
            }
        } else if (link.startsWith(Aksess.VAR_WEB + "/") && link.endsWith("/")) {
            // Kan v�re et alias, sjekk
            String alias = link.substring(Aksess.VAR_WEB.length());
            try {
                ContentIdentifier cid = new ContentIdentifier(alias);
                Content c = ContentAO.getContent(cid, true);
                if (c != null) {
                    occurrence.setStatus(CheckStatus.OK);
                } else {
                    occurrence.setStatus(CheckStatus.CONTENT_AP_NOT_FOUND);
                }

            } catch (ContentNotFoundException e) {
                // Ikke et alias eller slettet alias
                checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
            } catch (SystemException e) {
                occurrence.setStatus(CheckStatus.IO_EXCEPTION);
            }
        } else {
            checkRemoteUrl(webroot + link.substring(Aksess.VAR_WEB.length()), occurrence, client);
        }
    }

    private void checkRemoteUrl(String link, LinkOccurrence occurrence, HttpClient client) {
        HeadMethod head;
        try {
            head = new HeadMethod(link);
        } catch (Exception e) {
            occurrence.setStatus(CheckStatus.INVALID_URL);
            return;
        }

        int httpStatus = -1;
        int status = CheckStatus.OK;

        try {
            head.setFollowRedirects(true);

            client.executeMethod(head);
            httpStatus = head.getStatusCode();
            if(httpStatus != HttpStatus.SC_OK) {
                GetMethod get = new GetMethod(link);
                get.setFollowRedirects(true);
                try {
                    client.executeMethod(get);
                } finally{
                    get.releaseConnection();
                }
                httpStatus = get.getStatusCode();
                if (httpStatus != HttpStatus.SC_OK && httpStatus != HttpStatus.SC_UNAUTHORIZED && httpStatus != HttpStatus.SC_MULTIPLE_CHOICES && httpStatus != HttpStatus.SC_MOVED_TEMPORARILY && httpStatus != HttpStatus.SC_TEMPORARY_REDIRECT) {
                    status = CheckStatus.HTTP_NOT_200;
                }
            }
        } catch (UnknownHostException e) {
            status = CheckStatus.UNKNOWN_HOST;
        } catch (ConnectTimeoutException e) {
            status = CheckStatus.CONNECTION_TIMEOUT;
        } catch(CircularRedirectException e) {
            status = CheckStatus.CIRCULAR_REDIRECT;
        } catch(ConnectException e) {
            status = CheckStatus.CONNECT_EXCEPTION;
        } catch (IOException e) {
            status = CheckStatus.IO_EXCEPTION;
        } finally {
            head.releaseConnection();
        }

        occurrence.setStatus(status);
        occurrence.setHttpStatus(httpStatus);

    }

    public static void main(String[] args) {

        final HttpClient client = new HttpClient();
        HeadMethod head = new HeadMethod(args[0]);

        int httpStatus = -1;
        int status = CheckStatus.OK;

        try {
            head.setFollowRedirects(true);

            client.executeMethod(head);
            httpStatus = head.getStatusCode();
            if(httpStatus != HttpStatus.SC_OK) {
                GetMethod get = new GetMethod(args[0]);
                get.setFollowRedirects(true);
                client.executeMethod(get);
                httpStatus = get.getStatusCode();
                if(httpStatus != HttpStatus.SC_OK) {
                    status = CheckStatus.HTTP_NOT_200;
                }
            }
        } catch (UnknownHostException e) {
            status = CheckStatus.UNKNOWN_HOST;
        } catch (ConnectTimeoutException e) {
            status = CheckStatus.CONNECTION_TIMEOUT;
        } catch(CircularRedirectException e) {
            status = CheckStatus.CIRCULAR_REDIRECT;
        } catch (IOException e) {
            status = CheckStatus.IO_EXCEPTION;
        }
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
        setProxyHost(Aksess.getConfiguration().getString("linkchecker.proxy.host"));
        String proxyPort = Aksess.getConfiguration().getString("linkchecker.proxy.port");
        if(proxyPort != null) {
            setProxyPort(Integer.parseInt(proxyPort));
        }
        String proxyUser = Aksess.getConfiguration().getString("linkchecker.proxy.username");
        setProxyUser(proxyUser);

        String proxyPassword = Aksess.getConfiguration().getString("linkchecker.proxy.password");
        setProxyPassword(proxyPassword);
    }

}
