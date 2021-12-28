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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.List;

public class PhotoLinkTag extends BodyTagSupport {

    private String cssClass = null;
    private static final String DEFAULT_URL = "/multimedia.ap";
    private String url = DEFAULT_URL;


    public void setCssclass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException
    {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        RequestParameters  param = new RequestParameters(request, "utf-8");

        try {
            List<Multimedia> photos = PhotoAlbumHelper.getPhotos(pageContext, -1);
            if (photos != null && photos.size() > 0) {
                int curOffset = param.getInt("offset");
                if (curOffset == -1) {
                    curOffset = 0;
                } else if (curOffset >= photos.size()) {
                    curOffset = photos.size() - 1;
                }

                JspWriter out = getPreviousOut();

                Multimedia mm = photos.get(curOffset);

                out.write("<a");
                if (cssClass != null) {
                    out.write(" class=\"" + cssClass + "\"");
                }
                if (!url.contains("?")) {
                    url = url + "?id=" + mm.getId();
                } else {
                    url = url + "&amp;id=" + mm.getId();
                }
                out.write(" href=\"" + Aksess.getContextPath() + url + "\">");
                bodyContent.writeOut(out);
                out.write("</a>");
            }
        } catch (Exception e) {
            throw new JspTagException(e);
        } finally {
            bodyContent.clearBody();
            url = DEFAULT_URL;
        }

        return SKIP_BODY;
    }
}

