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

package no.kantega.publishing.api.taglibs.photoalbum;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.util.MultimediaTagCreator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;

public class PhotoIndexTag extends TagSupport {
    private static final String SOURCE = "aksess.PhotoImageTag";

    private int cols   = -1;
    private int max    = -1;
    private int width  = 120;
    private int height = 80;
    private int space = -1;
    private String cssClass = null;
    private String selectedCssClass = null;

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public void setCssclass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setSelectedcssclass(String selectedCssClass) {
        this.selectedCssClass = selectedCssClass;
    }

    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();

        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        try {
            List photos = PhotoAlbumHelper.getPhotos(pageContext, -1);
            if (photos != null && photos.size() > 0) {
                RequestParameters param = new RequestParameters(request, "utf-8");
                int offset = param.getInt("offset");

                if (cols != -1) {
                    out.write("<table>");
                }
                int noObjs = photos.size();
                int colNo = 0;
                int start = 0;
                int end   = noObjs;

                String url = Aksess.getContextPath() + "/" + Aksess.CONTENT_REQUEST_HANDLER + "?";
                try {
                    ContentIdentifier cid = ContentIdHelper.fromRequest(request);
                    url = url + "thisId=" + cid.getAssociationId() + "&amp;language=" + cid.getLanguage();
                } catch (Exception e) {
                    // Kan skje ved testing at malen ikke er knyttet opp til en side
                }

                if (max != -1 && max < noObjs) {
                    // Kan ikke vise alle bilder
                    if (offset == -1) {
                        offset = 0;
                    }

                    if (offset + max > noObjs) {
                        start = noObjs - max;
                        end   = noObjs;
                    } else if (offset > 0) {
                        start = offset - 1;
                        end   = offset + max - 1;
                    } else {
                        start = offset;
                        end   = start + max;
                    }
                }

                for (int i = start; i < end; i++) {
                    Multimedia mm = (Multimedia)photos.get(i);
                    if (cols != -1 && colNo == 0) {
                        out.write("<tr>");
                    }

                    MultimediaType type = mm.getType();
                    String mimeType = mm.getMimeType().getType();

                    String css = cssClass;
                    if (offset == i && selectedCssClass != null) {
                        css = selectedCssClass;
                    }

                    if (type == MultimediaType.MEDIA && mimeType.contains("image")) {
                        if (space != -1 && colNo > 0) {
                            out.write("<td style=\"width: " + space + "px;\"></td>");
                        }
                        out.write("<td style=\"text-align: center; vertical-align: middle;\"");
                        if (css != null) {
                            out.write(" class=\"" + css + "\"");
                        }
                        out.write("><a href=\"" + url + "&amp;offset=" + i + "\">");
                        out.write(MultimediaTagCreator.mm2HtmlTag(mm, null, width, height));
                        out.write("</a></td>\n");
                        colNo++;
                    }

                    if (cols != -1 && colNo == cols) {
                        out.write("</tr>\n");
                        colNo = 0;
                    }
                }
                if (cols != -1 && colNo != 0) {
                    for (int i = colNo; i < cols; i++) {
                        out.write("<td>&nbsp;</td>");
                    }
                    out.write("</tr>");
                }
                if (cols != -1) {
                    out.write("</table>");
                }
            }
        } catch (IOException e) {
            throw new JspTagException(SOURCE, e);
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
}