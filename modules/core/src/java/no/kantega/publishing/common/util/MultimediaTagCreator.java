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

package no.kantega.publishing.common.util;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.MultimediaImageMapAO;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaImageMap;
import no.kantega.publishing.common.data.enums.Cropping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MultimediaTagCreator {
    private static final Logger log = LoggerFactory.getLogger(MultimediaTagCreator.class);

    public static String mm2HtmlTag(Multimedia mm, String cssClass) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, null, -1, -1, cssClass, false);
    }

    public static String mm2HtmlTag(Multimedia mm, String align, int maxW, int maxH) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, align, maxW, maxH, null, false);
    }

    public static String mm2HtmlTag(Multimedia mm, String align, int maxW, int maxH, String cssClass) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, align, maxW, maxH, cssClass, false);
    }

    public static String mm2HtmlTag(Multimedia mm, String align, int maxW, int maxH, Cropping cropping, String cssClass) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, align, maxW, maxH, cropping, cssClass, false);
    }

    // TODO: Cleanup and delete methods not needed
    public static String mm2HtmlTag(String baseUrl, Multimedia mm, String align, int maxW, int maxH, String cssClass) {
        return mm2HtmlTag(baseUrl, mm, align, maxW, maxH, cssClass, false);
    }

    public static String mm2HtmlTag(String baseUrl, Multimedia mm, String align, int resizeWidth, int resizeHeight, String cssClass, boolean skipImageMap) {
        return mm2HtmlTag(baseUrl, mm, align, resizeWidth, resizeHeight, Cropping.CONTAIN, cssClass, skipImageMap);

    }

    public static String mm2HtmlTag(String baseUrl, Multimedia mm, String align, int resizeWidth, int resizeHeight, Cropping cropping, String cssClass, boolean skipImageMap) {
        String url = mm.getUrl();
        String altname = defaultIfBlank(mm.getAltname(), mm.getName());

        String mimeType = mm.getMimeType().getType();

        if (mimeType.contains("image")) {
            return createImgTag(mm, align, resizeWidth, resizeHeight, cropping, cssClass, skipImageMap, url, altname);
        } else if (mimeType.contains("x-ms-wmv") || mimeType.contains("x-msvideo") || mimeType.startsWith("video")) {
            return createVideoTag(baseUrl, mm, resizeWidth, resizeHeight);
        } else if (mimeType.startsWith("audio")) {
            return createAudioTag(baseUrl, mm);
        } else {
            return createDefaultTag(mm, url, mimeType);

        }
    }

    private static String createAudioTag(String baseUrl, Multimedia mm) {
        return setupPlayer(baseUrl) +
            "<audio id=\"" + mm.getId() + "\" src=\"" + mm.getUrl() + "\" controls=\"controls\"></audio>";
    }

    private static String setupPlayer(String baseUrl) {
        return Aksess.mediaElementIncludeJquery() ? "<script src=\"" + baseUrl + "/aksess/js/mediaelement/jquery.js\"></script>" : "" +
                "<script src=\"" + baseUrl + "/aksess/js/mediaelement/mediaelement-and-player.min.js\"></script>" +
                "<script src=\"" + baseUrl + "/aksess/js/mediaelement/insertstyle.jjs\"></script>";
    }

    private static String createDefaultTag(Multimedia mm, String url, String mimeType) {
        StringBuilder tag = new StringBuilder();

        mimeType = mimeType.replace('/', '-');
        mimeType = mimeType.replace('.', '-');
        SimpleDateFormat sdf = new SimpleDateFormat(Aksess.getDefaultDateFormat());
        String lastModifiedDateString = sdf.format(mm.getLastModified());
        tag.append("<a href=\"").append(url).append("\"><div class=\"media\"><div class=\"icon\">");
        tag.append("<span class=\"mediafile\"><span class=\"file ").append(mimeType).append("\"></span></span>");
        tag.append("</div><div class=\"mediaInfo\">");
        tag.append("<div class=\"name\">").append(mm.getName()).append("</div>");
        tag.append("<div class=\"details\">");
        tag.append(LocaleLabels.getLabel("aksess.multimedia.size", Aksess.getDefaultLocale())).append(": ").append(mm.getSize()).append(" bytes<br>");
        tag.append(LocaleLabels.getLabel("aksess.multimedia.lastmodified", Aksess.getDefaultLocale())).append(": ").append(lastModifiedDateString).append("<br>");
        tag.append("</div></div></div></a>");
        return tag.toString();
    }

    private static String createVideoTag(String baseUrl, Multimedia mm, int resizeWidth, int resizeHeight) {

        int width  = Aksess.getDefaultMediaWidth();
        if (resizeWidth > 0) {
            width = resizeWidth;
        }
        int height = Aksess.getDefaultMediaHeight();
        if (resizeHeight > 0) {
            height = resizeHeight;
        }
        String playerUrl = Aksess.getFlashVideoPlayerUrl();
        String movieUrl = mm.getUrl();

        return setupPlayer(baseUrl) +
            "<video width=\"" + width + "\" height=\"" + height + "\"" + " id=\"" + mm.getId() + "\" " +
                (Aksess.isJavascriptDebugEnabled() ? "data-mejsoptions='{\"enablePluginDebug\": true}' " : "") +
            "controls=\"controls\" preload=\"none\" class=\"mejs-player\">" +
            "<source " +
            "src=\"" + movieUrl + "\" />" +
            "<object width=\"" + width + "\" height=\"" + height + "\" type=\"application/x-shockwave-flash\" " +
            "data=\"" + movieUrl + "\">" +
            "<param name=\"movie\" value=\"" + baseUrl + playerUrl + "\" /> " +
            "<param name=\"flashvars\" value=\"controls=true&amp;file=" + movieUrl + "\" /> " +
            "</object> " +
            "</video>";
    }

    private static String createImgTag(Multimedia mm, String align, int resizeWidth, int resizeHeight, Cropping cropping, String cssClass, boolean skipImageMap, String url, String altname) {
        StringBuilder tag = new StringBuilder();

        tag.append("<img ");

        String author = defaultIfBlank(mm.getAuthor(), Aksess.getMultimediaDefaultCopyright());

        String copyright = "";
        if (isNotBlank(altname) && isNotBlank(author)) {
            copyright = " - &copy; " + author;
        }

        String title = Aksess.getMultimediaTitleFormat();

        title = StringHelper.replace(title, "$ALT", altname);
        title = StringHelper.replace(title, "$TITLE", mm.getName());
        title = StringHelper.replace(title, "$COPYRIGHT", copyright);

        // title attribut - dvs mouseover
        tag.append("title=\"").append(title);
        tag.append("\" ");

        // alt attribut - det som skal vises for screen readers etc
        String altString = Aksess.getMultimediaAltFormat();

        altString = StringHelper.replace(altString, "$ALT", altname);
        altString = StringHelper.replace(altString, "$TITLE", mm.getName());
        altString = StringHelper.replace(altString, "$COPYRIGHT", copyright);

        tag.append("alt=\"").append(altString);
        tag.append("\" ");

        int width  = mm.getWidth();
        int height = mm.getHeight();

        if (resizeWidth != -1 || resizeHeight != -1){
            StringBuilder urlBuilder = new StringBuilder(url);
            boolean containsq = url.contains("?");

            if (resizeWidth != -1) {
                urlBuilder.append(!containsq ? "?" : "&amp;");
                urlBuilder.append("width=").append(resizeWidth);
                containsq = true;
            }
            if (resizeHeight != -1) {
                urlBuilder.append(!containsq ? "?" : "&amp;");
                urlBuilder.append("height=").append(resizeHeight);
            }

            if (cropping != Cropping.CONTAIN){
                urlBuilder.append("&amp;cropping=").append(cropping.getTypeAsString());
            }

            url = urlBuilder.toString();
        } else {
            // Image will not be resised, specify dimensions in tag
            if (width > 0) {
                tag.append(" width=").append(width);
            }
            if (height > 0) {
                tag.append(" height=").append(height);
            }
        }
        if (cssClass != null && cssClass.length() > 0) {
            tag.append(" class=\"").append(cssClass).append("\"");
        }

        if (align != null && align.length() > 0) {
            tag.append(" align=").append(align);
        }
        tag.append(" src=\"").append(url).append("\"");

        if (!skipImageMap && mm.hasImageMap()) {
            try {
                MultimediaImageMap mim = MultimediaImageMapAO.loadImageMap(mm.getId());
                if (mim.getCoordUrlMap().length > 0) {
                    Date dt = new Date();
                    String mapId = "imagemap" + mm.getId() + dt.getTime();

                    // Avslutter bildet med referanse til bildekart
                    tag.append(" usemap=\"#").append(mapId).append("\">");
                    tag.append("<map id=\"").append(mapId).append("\" name=\"").append(mapId).append("\">");

                    for (int i=0; i < mim.getCoordUrlMap().length; i++){
                        String mapURL = mim.getCoordUrlMap()[i].getUrl();
                        if (mapURL.startsWith("/")) {
                            mapURL = Aksess.getContextPath() + mapURL;
                        }

                        // Henter eventuelle resizede koordinater
                        String coord = mim.getCoordUrlMap()[i].getResizedCoord(resizeWidth, width, resizeHeight, height);
                        if (coord != null) {
                            String target = "";
                            if (mim.getCoordUrlMap()[i].isOpenInNewWindow()) {
                                target = " onclick=\"window.open(this.href); return false\"";
                            }
                            tag.append("<area shape=\"rect\" coords=\"").append(coord).append("\" href=\"").append(mapURL).append("\" title=\"").append(mim.getCoordUrlMap()[i].getAltName()).append("\" alt=\"").append(mim.getCoordUrlMap()[i].getAltName()).append("\"").append(target).append(">");
                        }
                    }
                    tag.append("</map>");
                }
            } catch(SystemException e){
                log.error("Error creating image map", e);
            }
        }
        // Legg til > på slutten hvis ikke avsluttet
        if (tag.charAt(tag.length() - 1) != '>') {
            tag.append(">");
        }
        return tag.toString();
    }


}
