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

package no.kantega.publishing.common;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.configuration.ConfigurationListener;
import no.kantega.publishing.common.data.enums.ServerType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 */
public class Aksess {
    private static final Logger log = LoggerFactory.getLogger(Aksess.class);

    public static final String PROPERTY_FILE = "aksess.conf";
    public static final String VAR_WEB = "<@WEB@>";
    public static final String ERROR_URL = "/aksess/error/error.jsp";

    public static final String CONTENT_URL_PREFIX = "/content";
    public static final String MULTIMEDIA_URL_PREFIX = "/multimedia";
    public static final String ATTACHMENT_URL_PREFIX = "/attachment";

    public static final String CONTENT_REQUEST_HANDLER = "content.ap";
    public static final String ATTACHMENT_REQUEST_HANDLER = "attachment.ap";
    public static final String MULTIMEDIA_REQUEST_HANDLER = "multimedia.ap";


    private static String startPage = "/index.jsp";
    private static String loginUrl;

    private static boolean securityAllowPasswordReset;
    private static String securityRealm = "";
    private static String defaultSecurityDomain = "ldap";
    private static String contextPath = "";
    private static String baseUrl;
    private static String dateFormat = "";
    private static String[] internalIpSegment = null;

    private static String flashPluginVersion;
    private static boolean flashUseJavascript = false;
    private static String version = "unknown";
    private static String revision;
    private static String buildDate;
    private static String webappRevision;
    private static String webappVersion;
    private static String webappDate;

    private static String roleEveryone = "everyone";
    private static String[] roleHtmlEditor;
    private static String roleAdmin = "admin";
    private static String[] roleAuthor;
    private static String[] roleForms;
    private static String roleUnit;
    private static String roleOwner = "owner";
    private static String roleDeveloper;
    private static String[] rolePhotographer;
    private static String roleQualityAdmin;

    private static boolean isDefaultMinorChange = false;

    private static boolean openLinksInNewWindow = false;
    private static boolean urlRewritingEnabled = true;
    private static boolean smartLinksDefaultChecked = false;
    private static String customUrlRedirector = null;

    private static int historyMaxVersions;

    private static int defaultMediaWidth;
    private static int defaultMediaHeight;
    private static int maxMediaWidth = 1024;
    private static int maxMediaHeight = 768;

    private static boolean flashVideoAutoplay;
    private static String flashVideoPlayerUrl;

    private static String multimediaAltFormat = "";
    private static String multimediaTitleFormat = "";

    private static int deletedItemsMaxAge = 1;

    // Tilleggsmoduler
    private static boolean trafficLogEnabled = false;
    private static int trafficLogMaxAge = 1;

    private static boolean eventLogEnabled  = false;
    private static boolean searchLogEnabled  = false;
    private static int eventLogMaxAge = 6;


    private static boolean topicMapsEnabled = false;

    private static int securitySessionTimeout = 7200;
    private static int lockTimeToLive = 3600;
    private static boolean linkCheckerEnabled = false;

    private static int databaseCacheTimeout = -1;

    private static String language;
    private static String country;

    private static boolean csrfCheckEnabled = true;
    private static boolean javascriptDebugEnabled;

    private static Configuration c;
    
    private static String multimediaDefaultCopyright;

    private static ServerType serverType;

    private static String queryStringEncoding;
    private static int xmlCacheMaxAge = 3;

    public static void loadConfiguration() {

        baseUrl = c.getString("location.baseurl");
        if (baseUrl == null && c.getString("location.applicationurl") != null) {
            // For kompabilitet med de som har satt kun applicationurl
            baseUrl = c.getString("location.applicationurl");

        }

        dateFormat = c.getString("default.dateformat", "dd.MM.yyyy");

        defaultMediaWidth = c.getInt("multimedia.defaultwidth", 500);
        defaultMediaHeight = c.getInt("multimedia.defaultheight", 305);

        maxMediaWidth = c.getInt("multimedia.maxwidth", maxMediaWidth);
        maxMediaHeight = c.getInt("multimedia.maxheight", maxMediaHeight);

        flashPluginVersion = c.getString("multimedia.swf.defaultversion", "10.0.0.0");
        flashUseJavascript = c.getBoolean("multimedia.swf.usejavascript", false);
        flashVideoAutoplay = c.getBoolean("multimedia.flv.autoplay", true);
        flashVideoPlayerUrl = c.getString("multimedia.flv.playerurl", "/aksess/multimedia/videoplayer.swf");

        isDefaultMinorChange = c.getBoolean("default.minorchange", isDefaultMinorChange);

        historyMaxVersions = c.getInt("history.maxversions", 20);

        // Sikkerhetsconfig
        loginUrl = c.getString("security.login.url", getContextPath()  + "/Login.action");

        securityAllowPasswordReset = c.getBoolean("security.allowpasswordreset", true);

        defaultSecurityDomain = c.getString("security.defaultdomain", "ldap");
        log.info( "Using default security domain:" + defaultSecurityDomain);

        securityRealm = c.getString("security.realm", defaultSecurityDomain + "Realm");
        log.info( "Using security realm:" + securityRealm);

        securitySessionTimeout = c.getInt("security.sessiontimeout", 7200);

        // Rewrite URLs to userfriendly URLS
        urlRewritingEnabled = c.getBoolean("links.rewrite.enabled", true);

        // Open external links in new window
        openLinksInNewWindow = c.getBoolean("openlinksinnewwindow", false);

        // Insert smartlinks as default ?
        smartLinksDefaultChecked = c.getBoolean("links.smartlinksdefaultchecked", false);

        // Custom redirect page for links
        customUrlRedirector = c.getString("links.customurlredirector", null);

        // Tilleggsmoduler
        trafficLogEnabled = c.getBoolean("trafficlog.enabled", false);
        if (trafficLogEnabled) {
            log.info( "Module enabled: Traffic log");
        }
        trafficLogMaxAge = c.getInt("trafficlog.maxage", trafficLogMaxAge);
        deletedItemsMaxAge = c.getInt("deleteditems.maxage", deletedItemsMaxAge);
        xmlCacheMaxAge = c.getInt("xmlcache.maxage", xmlCacheMaxAge);

        internalIpSegment = c.getStrings("trafficlog.internalipsegment", "172.16.1");

        eventLogEnabled = c.getBoolean("eventlog.enabled", false);
        if (eventLogEnabled) {
            log.info( "Module enabled: Event log");
        }
        searchLogEnabled = c.getBoolean("searchlog.enabled", false);
        if (searchLogEnabled) {
            log.info( "Module enabled: searchlog");
        }

        eventLogMaxAge = c.getInt("eventlog.maxage", eventLogMaxAge);

        topicMapsEnabled = c.getBoolean("topicmaps.enabled", false);
        if (topicMapsEnabled) {
            log.info( "Module enabled: Topic maps");
        }

        linkCheckerEnabled = c.getBoolean("linkchecker.enabled", true);
        if (linkCheckerEnabled) {
            log.info( "Module enabled: Link checker");
        }

        // Roller
        roleAdmin = c.getString("security.role.admin", "admin");
        roleAuthor = c.getStrings("security.role.author", "innholdsprodusent");
        roleForms = c.getStrings("security.role.forms", roleAdmin);
        roleHtmlEditor = c.getStrings("security.role.htmleditor", roleAdmin + "," + StringUtils.join(roleAuthor, ','));
        roleUnit = c.getString("security.role.unit", "enhet*");
        roleDeveloper = c.getString("security.role.developer", "developer");
        roleQualityAdmin = c.getString("security.role.qualityadmin", "qualityadmin");
        rolePhotographer = c.getStrings("security.role.photographer", "photographer");

        // ContentLock
        lockTimeToLive = c.getInt("lock.timeToLive", lockTimeToLive);

        // Setting Flyt CMS language. Use no_NO if locale isn't defined
        language = c.getString("admin.locale.language", "no");
        country = c.getString("admin.locale.country", "NO");

        javascriptDebugEnabled = c.getBoolean("javascript.debug", false);

        // Format of alt and title-attributes
        multimediaAltFormat = c.getString("multimedia.alt.format", "$ALT");
        multimediaTitleFormat = c.getString("multimedia.title.format", "$TITLE$COPYRIGHT");

        multimediaDefaultCopyright = c.getString("multimedia.copyright.default", "");

        csrfCheckEnabled = c.getBoolean("csrfcheck.enabled", true);

        serverType = ServerType.valueOf(c.getString("server.type", ServerType.MASTER.name()).toUpperCase());
        log.info( "Server type:" + serverType);

        if (serverType == ServerType.SLAVE) {
            // Caching of database only lasts 15 minutes for slave servers
            databaseCacheTimeout = 900000;
        }
        databaseCacheTimeout = c.getInt("database.cache.timeout", databaseCacheTimeout);


        queryStringEncoding = c.getString("querystring.encoding", "iso-8859-1");
        log.info( "Using " + queryStringEncoding + " query string encoding.  Set querystring.encoding to match web server setting if necessary");

        // Load version from file in classpath
        {
            try {
                URL versionResource = Aksess.class.getResource("aksessVersion.properties");
                if(versionResource == null) {
                    throw new IllegalStateException("Couldn't find version information file aksessVersion.properties");
                }

                Properties versionProps = new Properties();

                versionProps.load(versionResource.openStream());

                String theVersion = versionProps.getProperty("version");

                if(theVersion != null) {
                    version = theVersion;
                } else {
                    throw new RuntimeException("'version' property not found in version file " + versionResource);
                }

                revision = versionProps.getProperty("revision", "unknown");
                buildDate =versionProps.getProperty("buildDate");
                if (isBlank(buildDate)) {
                    throw new RuntimeException(String.format("'buildDate' property not set in version file %s", versionResource));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Properties webappVersionInfo = new Properties();
            webappVersionInfo.load(Aksess.class.getResourceAsStream("/aksess-webapp-version.properties"));
            webappRevision = webappVersionInfo.getProperty("revision", "");
            webappVersion = webappVersionInfo.getProperty("version", "unknown");
            webappDate = webappVersionInfo.getProperty("date", "");
        } catch (IOException e) {
            throw new RuntimeException("/aksess-webapp-version.properties not found", e);
        }

        log.info( "location.contextpath=" + contextPath);
    }

    public static String getVersion() {
        return version;
    }

    /**
     * Get the build timestamp for this version.
     *
     * @return Date string with format <code>yyyyMMdd-HHmm</code>
     */
    public static String getBuildDate() {
        return buildDate;
    }

    public static String getBuildRevision() {
        return revision;
    }

    public static String getWebappRevision() {
        return webappRevision.isEmpty() ? webappVersion : webappRevision;
    }

    public static String getWebappVersion() {
        return webappVersion;
    }

    public static String getWebappDate() {
        return webappDate;
    }

    /**
     *
     * @deprecated Bruk getApplicationUrl + getContextPath
     */
    public static String getRootUrl() {
        // For kompabilitet
        return  getContextPath();
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getContextPath() {
        if (contextPath.length() == 0) {
            return "";
        } else if (contextPath.startsWith("/")){
            return contextPath;
        } else {
            return "/" + contextPath;
        }
    }

    public static void setContextPath(String contextPath) {
        Aksess.contextPath = contextPath;
    }

    public static String getStartPage() {
        return startPage;
    }

    public static String getContentFrameRef() {
        return null;
    }

    public static String getSecurityRealmName() {
        return securityRealm;
    }

    public static String getLoginUrl() {
        return loginUrl;
    }

    public static int getHistoryMaxVersions() {
        return historyMaxVersions;
    }

    public static String getDefaultDateFormat() {
        return dateFormat;
    }

    public static String getDefaultTimeFormat() {
        return "HH:mm";
    }

    public static String getDefaultDatetimeFormat() {
        return getDefaultDateFormat() + getDefaultDatetimeSeparator() + getDefaultTimeFormat();
    }

    public static String getDefaultDatetimeSeparator() {
        return " ";
    }

    public static Locale getDefaultLocale() {
        return new Locale("no", "NO");
    }

    public static Locale getDefaultAdminLocale() {
        return new Locale(language, country);
    }

    public static String getDefaultFlashVersion() {
        return flashPluginVersion;
    }

    public static String getDefaultSecurityDomain() {
        return defaultSecurityDomain;
    }

    public static String getAdminRole() {
        return roleAdmin;
    }

    public static String[] getAuthorRoles() {
        return roleAuthor;
    }

    public static String getDeveloperRole() {
            return roleDeveloper;
    }

    public static String getEveryoneRole() {
        return roleEveryone;
    }

    public static String[] getFormsRoles() {
        return roleForms;
    }

    public static String[] getHtmlEditorRoles() {
        return roleHtmlEditor;
    }

    public static String getUnitRole() {
        return roleUnit;
    }

    public static String getOwnerRole() {
        return roleOwner;
    }

    public static String[] getPhotographerRoles() {
        return rolePhotographer;
    }

    public static String getQualityAdminRole() {
        return roleQualityAdmin;
    }    

    public static boolean isTrafficLogEnabled() {
        return trafficLogEnabled;
    }

    public static int getTrafficLogMaxAge() {
        return trafficLogMaxAge;
    }

    public static boolean isEventLogEnabled() {
        return eventLogEnabled;
    }
    public static boolean isSearchLogEnabled() {
        return searchLogEnabled;
    }

    public static int getEventLogMaxAge() {
        return eventLogMaxAge;
    }


    public static boolean isTopicMapsEnabled() {
        return topicMapsEnabled;
    }

    public static Configuration getConfiguration() {
        return c;
    }

    public static boolean doOpenLinksInNewWindow() {
        return openLinksInNewWindow;
    }

    public static String[] getInternalIpSegment() {
        return internalIpSegment;
    }

    public static int getDefaultMediaWidth() {
        return defaultMediaWidth;
    }

    public static int getDefaultMediaHeight() {
        return defaultMediaHeight;
    }

    public static int getLockTimeToLive() {
        return lockTimeToLive;
    }

    public static boolean isLinkCheckerEnabled() {
        return linkCheckerEnabled;
    }

    public static String getApplicationUrl() {
        return getBaseUrl() + getContextPath();
    }

    public static int getSecuritySessionTimeout() {
        return securitySessionTimeout;
    }

    public static int getDeletedItemsMaxAge() {
        return deletedItemsMaxAge;
    }

    public static boolean isJavascriptDebugEnabled() {
        return javascriptDebugEnabled;
    }

    public static int getDatabaseCacheTimeout() {
        return databaseCacheTimeout;
    }

    public static String getMultimediaAltFormat() {
        return multimediaAltFormat;
    }

    public static String getMultimediaTitleFormat() {
        return multimediaTitleFormat;
    }

    public static boolean isFlashVideoAutoplay() {
        return flashVideoAutoplay;
    }

    public static String getFlashVideoPlayerUrl() {
        return flashVideoPlayerUrl;
    }

    public static boolean isUrlRewritingEnabled() {
        return urlRewritingEnabled;
    }

    public static boolean isSmartLinksDefaultChecked() {
        return smartLinksDefaultChecked;
    }

    public static String getCustomUrlRedirector() {
        return customUrlRedirector;
    }

    public static int getMaxMediaWidth() {
        return maxMediaWidth;
    }

    public static int getMaxMediaHeight() {
        return maxMediaHeight;
    }

    public static boolean isFlashUseJavascript() {
        return flashUseJavascript;
    }

    public static boolean isCsrfCheckEnabled() {
        return csrfCheckEnabled;
    }

    public static void setConfiguration(Configuration configuration) {
        Aksess.c = configuration;
        configuration.addConfigurationListener(new ConfigurationListener() {
            public void configurationRefreshed(Configuration configuration) {
                loadConfiguration();
            }
        });
    }

    public static String getMultimediaDefaultCopyright() {
        return multimediaDefaultCopyright;
    }

    public static ServerType getServerType() {
        return serverType;
    }

    public static String getQueryStringEncoding() {
        return queryStringEncoding;
    }

    public static boolean isSecurityAllowPasswordReset() {
        return securityAllowPasswordReset;
    }

    public static boolean isDefaultMinorChange() {
        return isDefaultMinorChange;
    }

    public static int getXmlCacheMaxAge() {
        return xmlCacheMaxAge;
    }
}
