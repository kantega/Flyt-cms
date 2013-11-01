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

package no.kantega.commons.taglib.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class GetHtmlTag extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(GetHtmlTag.class);

    private String url;


    public int doStartTag() throws JspException {
        HttpGet get = new HttpGet(url);
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        get.setHeader("User-Agent", request.getHeader("User-Agent"));

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try(CloseableHttpResponse response = httpclient.execute(get)){
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                pageContext.getOut().write(IOUtils.toString(entity.getContent()));
            }
        } catch (IOException e) {
            log.error("Error getting html", e);
        }
        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }



    public void setUrl(String url) {
        this.url = url;
    }
}
