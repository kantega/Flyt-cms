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
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.util.MultimediaTagCreator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;

public class PhotoImageTag extends TagSupport {

    private int offset = -1;
    private String settorequest = null;
    private String property = "image";
    private String cssClass = null;
    private String url = null;
    private String defaultvalue = "";
    private int width = -1;
    private int height = -1;
    private boolean random = false;
    private int albumId = -1;


    public void setSettorequest(String settorequest) {
        this.settorequest = settorequest;
    }
    
    public void setOffset(int offset){
        this.offset = offset;
    }

    public void setAlbumid(int albumId) {
        this.albumId = albumId;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }

    public void setCssclass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        RequestParameters param = new RequestParameters(request, "utf-8");
        List photos = PhotoAlbumHelper.getPhotos(pageContext, albumId);
        if (photos != null && photos.size() > 0) {

            offset = param.getInt("offset");
            if (offset == -1) {
                if (random) {
                    offset = (int)(photos.size() * Math.random());
                } else {
                    offset = 0;
                }
            }

            if (offset >= photos.size()) {
                offset = photos.size() - 1;
            }

            Multimedia mm = (Multimedia)photos.get(offset);
            JspWriter out = pageContext.getOut();
            try {
                if (settorequest != null && settorequest.trim().length() > 0 ){
                    // Setter gitt parameter til request
                    // <photo:image settorequest="varname" param="paramname" />

                    String rout = "";
                    if ("name".equalsIgnoreCase(property))      rout = mm.getName();
                    if ("id".equalsIgnoreCase(property))        rout = "" + mm.getId();
                    if ("offset".equalsIgnoreCase(property))    rout = "" + offset+1;
                    if ("total".equalsIgnoreCase(property))     rout = "" + photos.size();
                    if ("author".equalsIgnoreCase(property))    rout = mm.getAuthor();
                    if ("description".equalsIgnoreCase(property)) rout = mm.getDescription();

                    request.setAttribute(settorequest, rout);

                } else if ("name".equalsIgnoreCase(property) || "title".equalsIgnoreCase(property)) {
                    out.write(mm.getName());
                } else  if ("id".equalsIgnoreCase(property)) {
                    out.write(mm.getId());
                } else  if ("offset".equalsIgnoreCase(property)) {
                    out.write("" + (offset+1));
                } else  if ("total".equalsIgnoreCase(property)) {
                    out.write("" + photos.size());
                } else if ("author".equalsIgnoreCase(property)) {
                    if (mm.getAuthor() != null && mm.getAuthor().length() > 0) {
                        out.write(mm.getAuthor());
                    } else {
                        out.write(defaultvalue);
                    }
                } else if ("description".equalsIgnoreCase(property)) {
                    if (mm.getDescription() != null) {
                        out.write(mm.getDescription());
                    }
                } else {
                    if (url != null) {
                        if (!url.contains("?")) {
                            url = url + "?id=" + mm.getId();
                        } else {
                            url = url + "&amp;id=" + mm.getId();
                        }
                        out.write("<a onclick=\"window.open(this.href); return false\" href=\"" + Aksess.getContextPath() + url + "\">");
                    }
                    if (width != -1 && mm.getWidth() > width || height != -1 && mm.getHeight() > height) {
                        out.write(MultimediaTagCreator.mm2HtmlTag(mm, null, width, height, cssClass));
                    } else {
                        out.write(MultimediaTagCreator.mm2HtmlTag(mm, cssClass));
                    }
                    if (url != null) {
                        out.write("</a>");
                    }

                }
            } catch (IOException e) {
                throw new JspTagException(e);
            }
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        albumId = -1;
        return EVAL_PAGE;
    }
}

