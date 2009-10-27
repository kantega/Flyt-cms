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
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.common.data.enums.HTMLVersion;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.net.URL;

/**
 *
 */
public class Aksess {
    private static final String SOURCE = "aksess.common.Aksess";

    public static final String PROPERTY_FILE = "aksess.conf";
    public static final String VAR_WEB = "<@WEB@>";
    public static final String ATTRIBUTE_CLASS_PATH = "no.kantega.publishing.common.data.attributes.";
    public static final String ERROR_URL = "/aksess/error/error.jsp";

    public static final String CONTENT_REQUEST_HANDLER = "content.ap";
    public static final String ATTACHMENT_REQUEST_HANDLER = "attachment.ap";
    public static final String MULTIMEDIA_REQUEST_HANDLER = "multimedia.ap";


    private static String startPage = "/index.jsp";
    private static String loginUrl;
    private static String securityRealm = "";
    private static String defaultSecurityDomain = "ldap";
    private static String contextPath = "";
    private static String baseUrl;
    private static String dateFormat = "";
    private static String[] internalIpSegment = null;

    private static String flashPluginVersion;
    private static boolean flashUseJavascript = false;
    private static String version = "unknown";

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

    private static String outputImageFormat = "jpg";
    private static int outputImageQuality = 85;
    // Hvis produktet av bredden og hoyden i piksler kommer under denne verdien ved skalering,
    // blir det skalerte bildet lagret i png-format.
    private static long pngPixelLimit = 3000;
    private static boolean imageConversionEnabled = false;

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
    private static boolean formsEnabled = false;

    private static String luceneIndexDir;

    private static int securitySessionTimeout = 7200;
    private static int lockTimeToLive = 3600;
    private static boolean linkCheckerEnabled = false;

    private static int databaseCacheTimeout = -1;

    private static String language;
    private static String country;

    private static String htmlVersion;

    private static Configuration c;

    public static void loadConfiguration() {

        try {
            contextPath = c.getString("location.contextpath", "");
            if (contextPath.length() > 0) {
                if (contextPath.indexOf("/") != -1) {
                    // Fjern ekstra / start eller slutt av adresse
                    contextPath = StringHelper.replace(contextPath, "/", "");
                }
            }

            baseUrl = c.getString("location.baseurl");
            if (baseUrl == null && c.getString("location.applicationurl") != null) {
                // For kompabilitet med de som har satt kun applicationurl
                baseUrl = c.getString("location.applicationurl");

            }

            dateFormat = c.getString("default.dateformat", "dd.MM.yyyy");

            // Format og kvalitet på bilder
            outputImageFormat = c.getString("default.thumbnailformat", outputImageFormat);
            outputImageQuality = c.getInt("default.thumbnailformat.jpg.quality", outputImageQuality);
            pngPixelLimit = c.getLong("default.thumbnail.pngpixellimit", pngPixelLimit);

            defaultMediaWidth = c.getInt("multimedia.defaultwidth", 500);
            defaultMediaHeight = c.getInt("multimedia.defaultheight", 306);

            maxMediaWidth = c.getInt("multimedia.maxwidth", maxMediaWidth);
            maxMediaHeight = c.getInt("multimedia.maxheight", maxMediaHeight);

            imageConversionEnabled = c.getBoolean("multimedia.convertimages", false);

            flashPluginVersion = c.getString("multimedia.swf.defaultversion", "9.0.0.54");
            flashUseJavascript = c.getBoolean("multimedia.swf.usejavascript", false);
            flashVideoAutoplay = c.getBoolean("multimedia.flv.autoplay", true);
            flashVideoPlayerUrl = c.getString("multimedia.flv.playerurl", "/aksess/multimedia/videoplayer.swf");

            historyMaxVersions = c.getInt("history.maxversions", 20);

            // Sikkerhetsconfig
            loginUrl = c.getString("security.login.url", getContextPath()  + "/Login.action");

            defaultSecurityDomain = c.getString("security.defaultdomain", "ldap");
            Log.debug(SOURCE, "Bruker standard sikkerhetsdomene:" + defaultSecurityDomain, null, null);

            securityRealm = c.getString("security.realm", defaultSecurityDomain + "Realm");
            Log.debug(SOURCE, "Bruker sikkerhetsrealm:" + securityRealm, null, null);

            securitySessionTimeout = c.getInt("security.sessiontimeout", 7200);

            // Rewrite URLs to userfriendly URLS
            urlRewritingEnabled = c.getBoolean("links.rewrite.enabled", true);

            // Åpne eksterne lenker i nytt vindu
            openLinksInNewWindow = c.getBoolean("openlinksinnewwindow", false);

            // Insert smartlinks as default ?
            smartLinksDefaultChecked = c.getBoolean("links.smartlinksdefaultchecked", false);

            // Custom redirect page for links
            customUrlRedirector = c.getString("links.customurlredirector", null);

            // Tilleggsmoduler
            trafficLogEnabled = c.getBoolean("trafficlog.enabled", false);
            if (trafficLogEnabled) {
                Log.debug(SOURCE, "Tillegsmodul: Trafikklogg", null, null);
            }
            trafficLogMaxAge = c.getInt("trafficlog.maxage", trafficLogMaxAge);

            internalIpSegment = c.getStrings("trafficlog.internalipsegment", "172.16.1");

            eventLogEnabled = c.getBoolean("eventlog.enabled", false);
            if (eventLogEnabled) {
                Log.debug(SOURCE, "Tillegsmodul: Eventlogg", null, null);
            }
            searchLogEnabled = c.getBoolean("searchlog.enabled", false);
            if (searchLogEnabled) {
                Log.debug(SOURCE, "Tillegsmodul: searchlog", null, null);
            }

            eventLogMaxAge = c.getInt("eventlog.maxage", eventLogMaxAge);

            topicMapsEnabled = c.getBoolean("topicmaps.enabled", false);
            if (topicMapsEnabled) {
                Log.debug(SOURCE, "Tillegsmodul: Emnekart", null, null);
            }

            formsEnabled = c.getBoolean("forms.enabled", false);
            if (formsEnabled) {
                Log.debug(SOURCE, "Tillegsmodul: Skjema", null, null);
            }
            linkCheckerEnabled = c.getBoolean("linkchecker.enabled", false);


            // Plassering søkeindeks
            luceneIndexDir = c.getString("lucene.index.dir", Configuration.getApplicationDirectory() +"/index");


            // Roller
            roleAdmin = c.getString("security.role.admin", "admin");
            roleAuthor = c.getStrings("security.role.author", "innholdsprodusent");
            roleForms = c.getStrings("security.role.forms", roleAdmin);
            roleHtmlEditor = c.getStrings("security.role.htmleditor", roleAdmin);
            roleUnit = c.getString("security.role.unit", "enhet*");
            roleDeveloper = c.getString("security.role.developer", "developer");
            roleQualityAdmin = c.getString("security.role.qualityadmin", "qualityadmin");
            rolePhotographer = c.getStrings("security.role.photographer", "photographer");            

            // ContentLock
            lockTimeToLive = c.getInt("lock.timeToLive", lockTimeToLive);

            // Setter aksess språk. Bruker no_NO hvis locale ikke er definert
            language = c.getString("admin.locale.language", "no");
            country = c.getString("admin.locale.country", "NO");

            htmlVersion = c.getString("html.version", HTMLVersion.HTML_401_TRANS);

            databaseCacheTimeout = c.getInt("database.cache.timeout", -1);

            // Format på alt og title attributter
            multimediaAltFormat = c.getString("multimedia.alt.format", "$ALT");
            multimediaTitleFormat = c.getString("multimedia.title.format", "$TITLE$COPYRIGHT");

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

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (ConfigurationException e) {
            Log.debug(SOURCE, "********* Klarte ikke å lese aksess.conf **********", null, null);
            Log.error(SOURCE, e, null, null);
            System.out.println("********* Klarte ikke å lese aksess.conf **********" + e);
        }

        Log.debug(SOURCE, "location.contextpath=" + contextPath, null, null);
    }

    public static String getVersion() {
        return version;
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
        } else {
            return "/" + contextPath;
        }
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

    public static boolean isFormsEnabled() {
        return formsEnabled;
    }

    public static Configuration getConfiguration() throws ConfigurationException {
        return c;
    }

    public static boolean doOpenLinksInNewWindow() {
        return openLinksInNewWindow;
    }

    public static String getOutputImageFormat() {
        return outputImageFormat;
    }

    public static int getOutputImageQuality() {
        return outputImageQuality;
    }

    public static long getPngPixelLimit() {
        return pngPixelLimit;
    }

    public static String getLuceneIndexDir() {
        return luceneIndexDir;
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

    public static boolean isImageConversionEnabled() {
        return imageConversionEnabled;
    }

    public static int getDeletedItemsMaxAge() {
        return deletedItemsMaxAge;
    }

    public static String getHtmlVersion() {
        return htmlVersion;
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


    public static void setConfiguration(Configuration configuration) {
        Aksess.c = configuration;
        configuration.addConfigurationListener(new ConfigurationListener() {
            public void configurationRefreshed(Configuration configuration) {
                loadConfiguration();
            }
        });
    }
}
