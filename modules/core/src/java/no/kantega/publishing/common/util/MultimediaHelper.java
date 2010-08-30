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

public class MultimediaHelper {
    private static final String SOURCE = "aksess.MultimediaHelper";

    public static String mm2HtmlTag(Multimedia mm) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, null, -1, -1, null);
    }

    public static String mm2HtmlTag(Multimedia mm, String cssClass) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, null, -1, -1, cssClass);
    }

    public static String mm2HtmlTag(Multimedia mm, int maxW, int maxH) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, null, maxW, maxH, null);
    }

    public static String mm2HtmlTag(Multimedia mm, String align, int maxW, int maxH) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, align, maxW, maxH, null);
    }

    public static String mm2HtmlTag(Multimedia mm, String align, int maxW, int maxH, String cssClass) {
        return mm2HtmlTag(Aksess.getContextPath(), mm, align, maxW, maxH, cssClass);
    }
    public static String mm2HtmlTag(String baseUrl, Multimedia mm, String align, int maxW, int maxH, String cssClass) {
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
                            if (mim.getCoordUrlMap()[i].openInNewWindow()) {
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
        } else if(mimeType.indexOf("x-ms-wmv") != -1 || mimeType.indexOf("x-msvideo") != -1) {
            int width  = mm.getWidth();
            int height = mm.getHeight();
            tag.append("<OBJECT ID=\"MediaPlayer\"");
            tag.append(" classid=\"CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95\"");
            tag.append(" codebase=\"http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=6,4,7,1112\"");
            tag.append(" type=\"application/x-oleobject\" width=\""+width+"\" height=\""+height+"\">");
            tag.append("<PARAM name=\"filename\" value=\"" +url+ "\">");
            tag.append("<EMBED type=\"application/x-mplayer2\"");
            tag.append(" pluginspage=\"http://www.microsoft.com/windows/windowsmedia/download/AllDownloads.aspx\"");
            tag.append(" width=\""+width+"\"");
            tag.append(" height=\""+height+"\"");
            tag.append(" src=\""+url +"\">");
            tag.append("</EMBED>");
            tag.append("</OBJECT>");            
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
            String movieUrl = baseUrl + "/multimedia/" + mm.getId() + "." + mm.getMimeType().getFileExtension();
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
            tag.append("<A href=" + url + ">" + mm.getName());
            if (mimeType.indexOf("octet-stream") == -1) {
                tag.append(" (" + mm.getMimeType().getDescription() + ")");
            }
            tag.append("</A>");

        }
        return tag.toString();
    }


    public static byte[] convertImageFormat(byte[] source) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(source));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, Aksess.getOutputImageFormat(), out);
            return out.toByteArray();
        } catch (Exception e) {
            return source;
        }
    }

    public static void updateMultimediaFromData(Multimedia mm, byte[] data, String filename) {
        mm.setData(data);

        MimeType mimeType = MimeTypes.getMimeType(filename);
        if (mimeType.getType().indexOf("image") != -1 || mimeType.getType().indexOf("flash") != -1) {
            // Dette er et bilde eller Flash fil, finn st�rrelse
            ImageInfo ii = new ImageInfo();
            ii.setInput(new ByteArrayInputStream(mm.getData()));
            if (ii.check()) {
                mm.setWidth(ii.getWidth());
                mm.setHeight(ii.getHeight());
            }
        } else if (mimeType.isDimensionRequired() && (mm.getWidth() <= 0 || mm.getHeight() <= 0)) {
            mm.setWidth(Aksess.getDefaultMediaWidth());
            mm.setHeight(Aksess.getDefaultMediaHeight());
        }

        if (filename.length() > 255) {
            filename = filename.substring(filename.length() - 255, filename.length());
        }
        mm.setFilename(filename);
    }

}
