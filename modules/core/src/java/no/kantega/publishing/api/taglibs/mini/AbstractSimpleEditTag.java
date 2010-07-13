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

package no.kantega.publishing.api.taglibs.mini;

import no.kantega.publishing.common.data.Content;

import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jul 2, 2008
 * Time: 1:28:25 PM
 */
public class AbstractSimpleEditTag extends BodyTagSupport {
    protected String SOURCE = "aksess.AbstractSimpleEditTag";
    protected String linkId = null;
    protected String cssclass = null;
    protected String redirectUrl = null;
    protected String cancelUrl = null;
    protected String collection = null;
    protected Content content = null;

    public void setLinkid(String linkid) {
        this.id = linkid;
    }

    public void setCssclass(String cssclass) {
        this.cssclass = cssclass;
    }

    public void setRedirecturl(String redirecturl) {
        this.redirectUrl = redirecturl;
    }

    public void setCancelurl(String cancelurl) {
        this.cancelUrl = cancelurl;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setObj(Content obj) {
        this.content = obj;
    }

    protected void resetVars() {
        linkId = null;
        cssclass = null;
        redirectUrl = null;
        collection = null;
        content = null;
    }
}
