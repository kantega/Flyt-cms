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
import no.kantega.commons.log.Log;
import no.kantega.commons.media.ImageInfo;
import no.kantega.commons.media.MimeType;
import no.kantega.commons.media.MimeTypes;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.MultimediaImageMapAO;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaImageMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class MultimediaTagCreator {
    private static final String SOURCE = "aksess.MultimediaHelper";

    public static String mm2HtmlTag(Multimedia mm) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, null, -1, -1, null, false);
    }

    public static String mm2HtmlTag(Multimedia mm, String cssClass) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, null, -1, -1, cssClass, false);
    }

    public static String mm2HtmlTag(Multimedia mm, int maxW, int maxH) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, null, maxW, maxH, null, false);
    }

    public static String mm2HtmlTag(Multimedia mm, String align, int maxW, int maxH) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, align, maxW, maxH, null, false);
    }

    public static String mm2HtmlTag(Multimedia mm, String align, int maxW, int maxH, String cssClass) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, align, maxW, maxH, cssClass, false);
    }

    // TODO: Cleanup and delete methods not needed
    public static String mm2HtmlTag(String baseUrl, Multimedia mm, String align, int maxW, int maxH, String cssClass) {
        return mm2HtmlTag(baseUrl, mm, align, maxW, maxH, cssClass, false);
    }

    public static String mm2HtmlTag(String baseUrl, Multimedia mm, String align, int maxW, int maxH, String cssClass, boolean skipImageMap) {
        StringBuffer tag = new StringBuffer();

        String url = mm.getUrl();
        String altname = mm.getAltname();

        if (altname == null || altname.length() == 0) {
            altname = mm.getName();
        }

        String mimeType = mm.getMimeType().getType();

        if (mimeType.indexOf("image") != -1) {
            // Bilde
            tag.append("<img ");

            String author = mm.getAuthor();
            if (author == null || author.length() == 0) {
                author = Aksess.getMultimediaDefaultCopyright();
            }

            String copyright = "";
            if (author != null && author.length() > 0) {
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

            if ((maxW != -1 && maxW < width) || (maxH != -1 && maxH < height)) {
                if (maxW != -1) {
                    url += "&amp;width=" + maxW;
                }
                if (maxH != -1) {
                    url += "&amp;height=" + maxH;
                }
            } else {
                // Bildet skal ikke krympes, angi st�rrelse i tag'en
                if (width > 0) {
                    tag.append(" width=" + width);
                }
                if (height > 0) {
                    tag.append(" height=" + height);
                }
            }
            if (cssClass != null && cssClass.length() > 0) {
                tag.append(" class=\"" + cssClass + "\"");
            }

            if (align != null && align.length() > 0) {
                tag.append(" align=" + align);
            }
            tag.append(" src=\"" + url + "\"");

            if (!skipImageMap) {
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
                            String coord = mim.getCoordUrlMap()[i].getResizedCoord(maxW, width, maxH, height);
                            if (coord != null) {
                                String target = "";
                                if (mim.getCoordUrlMap()[i].isOpenInNewWindow()) {
                                    target = " onclick=\"window.open(this.href); return false\"";
                                }
                                tag.append("<area shape=\"rect\" coords=\"" + coord + "\" href=\"" + mapURL + "\" title=\"" + mim.getCoordUrlMap()[i].getAltName() + "\" alt=\"" + mim.getCoordUrlMap()[i].getAltName() + "\"" + target + ">");
                            }
                        }
                        tag.append("</map>");
                    }
                } catch(SystemException e){
                    System.err.println(e);
                    Log.error(SOURCE, e, null, null);
                }
            }
            // Legg til > p� slutten hvis ikke avsluttet
            if (tag.charAt(tag.length() - 1) != '>') {
                tag.append(">");
            }

        } else if (mimeType.indexOf("flash") != -1) {
            int width  = mm.getWidth();
            int height = mm.getHeight();
            if (Aksess.isFlashUseJavascript()) {
                tag.append("<script type=\"text/javascript\">");
                tag.append("try {");
                tag.append("aksessMultimedia.embedFlash(\"" + url + "\", " + mm.getId() + ", " + width + ", " + height + ");");
                tag.append("} catch (e) {");
                tag.append("}");
                tag.append("</script>");
                tag.append("<div id=\"swf" + mm.getId() + "\">");
                tag.append("<p><a href=\"http://www.adobe.com/go/getflashplayer\"><img src=\"http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif\" alt=\"Get Adobe Flash player\" /></a></p>");
                tag.append("</div>");
                tag.append("<noscript>");
            }
            tag.append("<OBJECT classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" codebase=\"https://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab\" width=\"" + width + "\" height=\"" + height + "\">");
            tag.append("<PARAM name=\"movie\" value=\"" + url + "\">");
            tag.append("<PARAM name=\"quality\" value=\"high\">");
            tag.append("<PARAM name=\"wmode\" value=\"transparent\">");
            tag.append("<EMBED src=\"" + url + "\" quality=\"high\" wmode=\"transparent\" pluginspage=\"https://www.macromedia.com/go/getflashplayer\" type=\"application/x-shockwave-flash\" swliveconnect=\"true\" width=\"" + width + "\" height=\"" + height + "\"></EMBED></OBJECT>");
            if (Aksess.isFlashUseJavascript()) {
                tag.append("</noscript>");
            }
        } else if (mimeType.startsWith("video") || mimeType.startsWith("audio")) {
            int width  = Aksess.getDefaultMediaWidth();
            if (maxW != -1) {
                width = maxW;
            }
            int height = Aksess.getDefaultMediaHeight();
            if (maxH != -1) {
                height = maxH;
            }
            String playerUrl = Aksess.getFlashVideoPlayerUrl();
            String movieUrl = baseUrl + "/multimedia/" + mm.getUrl();
            String playerStr = baseUrl + playerUrl + "?movieAutoPlay=" + Aksess.isFlashVideoAutoplay() + "&movieUrl=" + movieUrl;
            if (Aksess.isFlashUseJavascript()) {
                String id = "swf" + mm.getId();
                tag.append("<script type=\"text/javascript\">\n");
                tag.append("try {\n");
                tag.append("aksessMultimedia.embedFlashVideo(\"" + movieUrl + "\", " + mm.getId() + ", " + width + ", " + height + ");\n");
                tag.append("} catch (e) {\n");
                tag.append("}\n");
                tag.append("</script>\n");
                tag.append("<div id=\"" + id + "\">\n");
                tag.append("<p><a href=\"http://www.adobe.com/go/getflashplayer\"><img src=\"http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif\" alt=\"Get Adobe Flash player\" /></a></p>\n");
                tag.append("</div>\n");
                tag.append("<noscript>");
            }
            tag.append("<OBJECT classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" codebase=\"https://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab\" width=\"" + width + "\" height=\"" + height + "\">");
            tag.append("<PARAM name=\"movie\" value=\"" + playerStr + "\">");
            tag.append("<PARAM name=\"quality\" value=\"high\">");
            tag.append("<PARAM name=\"allowFullScreen\" value=\"true\" />");
            tag.append("<EMBED src=\"" + playerStr + "\" quality=\"high\" allowFullScreen=\"true\"  pluginspage=\"https://www.macromedia.com/go/getflashplayer\" type=\"application/x-shockwave-flash\" swliveconnect=\"true\" width=\"" + width + "\" height=\"" + height + "\"></EMBED></OBJECT>");
            if (Aksess.isFlashUseJavascript()) {
                tag.append("</noscript>");
            }
        } else {
            mimeType = mimeType.replace('/', '-');
            mimeType = mimeType.replace('.', '-');
            tag.append("<A href=" + url + " class=\"file " + mimeType + "\">" + mm.getName() + "</A>");
        }
        return tag.toString();
    }


}
