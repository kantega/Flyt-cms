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

package no.kantega.publishing.common.data;

import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.InputStream;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 14, 2009
 * Time: 2:50:23 PM
 */
public class ServletContextInputStreamSource implements InputStreamSource, ServletContextAware {
    private ServletContext servletContext;
    private String resource;

    public InputStream getInputStream() {
        return servletContext.getResourceAsStream(resource);
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
