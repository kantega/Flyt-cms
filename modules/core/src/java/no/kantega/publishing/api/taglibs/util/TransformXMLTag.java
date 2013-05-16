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

package no.kantega.publishing.api.taglibs.util;

import no.kantega.commons.log.Log;
import no.kantega.publishing.api.xmlcache.XMLCacheEntry;
import no.kantega.publishing.api.xmlcache.XmlCache;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringWriter;

public class TransformXMLTag extends TagSupport {
    private static final String SOURCE = "aksess.TransformXMLTag";
    
    private String cacheId = null;
    private String xslt = null;
    private XmlCache xmlCache;


    public void setCacheid(String cacheId) {
        this.cacheId = cacheId;
    }

    public void setXslt(String xslt) {
        this.xslt = xslt;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();

            if (cacheId != null && xslt != null) {
                initXmlCacheIfNull();
                XMLCacheEntry cacheEntry = xmlCache.getXMLFromCache(cacheId);
                if (cacheEntry != null) {
                    Document xml = cacheEntry.getXml();
                    if (xml != null) {
                        TransformerFactory fac = TransformerFactory.newInstance();
                        Transformer transformer = fac.newTransformer(new StreamSource(pageContext.getServletContext().getRealPath(xslt)));

                        transformer.setOutputProperty(OutputKeys.METHOD, "html");

                        StringWriter sw = new StringWriter();
                        try {
                            transformer.transform(new DOMSource(xml), new StreamResult(sw));
                            out.write(sw.toString());
                        } catch (TransformerException e) {
                            Log.error(SOURCE, e, null, null);
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new JspException("ERROR: TransformXMLTag", e);
        }

        cacheId = null;
        xslt = null;

        return SKIP_BODY;
    }

    private void initXmlCacheIfNull() {
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
        xmlCache = context.getBean(XmlCache.class);

    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }

}

