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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.List;

public class PhotoNavigateTag extends BodyTagSupport {
    private static final String SOURCE = "aksess.PhotoImageTag";

    private String cssClass = null;
    private int offset = 0;

    public void setCssclass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspException
    {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        RequestParameters  param = new RequestParameters(request, "utf-8");

        try {
            List photos = PhotoAlbumHelper.getPhotos(pageContext, -1);
            if (photos != null && photos.size() > 0) {
                int curOffset = param.getInt("offset");
                if (curOffset == -1) {
                    curOffset = 0;
                } else if (curOffset >= photos.size()) {
                    curOffset = photos.size() - 1;
                }

                String url = Aksess.getContextPath() + "/" + Aksess.CONTENT_REQUEST_HANDLER + "?";
                try {
                    ContentIdentifier cid = ContentIdHelper.fromRequest(request);
                    url = url + "thisId=" + cid.getAssociationId() + "&amp;language=" + cid.getLanguage();
                } catch (Exception e) {
                    // Kan skje ved testing at malen ikke er knyttet opp til en side
                }

                JspWriter out = getPreviousOut();

                boolean printBody = true;

                int newOffset = 0;

                if (offset > 0 && curOffset + offset > photos.size() - 1) {
                    printBody = false;
                } else if (offset < 0 && curOffset + offset < 0) {
                    newOffset = 0;
                    if (curOffset == 0) {
                        printBody = false;
                    }
                } else {
                    newOffset = curOffset + offset;
                }

                if (printBody) {
                    out.write("<a");
                    if (cssClass != null) {
                        out.write(" class=\"" + cssClass + "\"");
                    }
                    out.write(" href=\"" + url + "&amp;offset=" + (newOffset) + "\">");
                    bodyContent.writeOut(out);
                    out.write("</a>");
                }
            }
        } catch (Exception e) {
            throw new JspTagException(SOURCE, e);
        } finally {
            bodyContent.clearBody();
        }



        return SKIP_BODY;
    }
}
