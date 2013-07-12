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

package no.kantega.publishing.client.filter;

import no.kantega.commons.exception.ConfigurationException;

import javax.servlet.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import static no.kantega.publishing.common.Aksess.getConfiguration;

/**
 * The class allows for configuring a filters init-params from aksess.conf.
 *
 * Example use:
 * Replace the filter class with no.kantega.publishing.client.filter.OpenAksessConfiguredFilter.
 * Set the init-param param name <i>wrappedFilerClass</i> with a value of the filters original class.
 *
 *     <filter>
 *       <filter-name>wro4j</filter-name>
 *       <filter-class>no.kantega.publishing.client.filter.OpenAksessConfiguredFilter</filter-class>
 *       <init-param>
 *           <param-name>wrappedFilterClass</param-name>
 *           <param-value>ro.isdc.wro.http.WroFilter</param-value>
 *       </init-param>
 *   </filter>
 * The filters original init-params kan now be configured in aksess.conf using the filter name as namespace.
 *
 * Example: wro4j.gZipResources = true
 *
 */
public class OpenAksessConfiguredFilter implements Filter {

    private Filter wrappedFilter;

    public void init(final FilterConfig filterConfig) throws ServletException {
        final String filterClass = filterConfig.getInitParameter("wrappedFilterClass");

        try {
            final Class<?> clazz = getClass().getClassLoader().loadClass(filterClass);
            wrappedFilter = (Filter) clazz.newInstance();

            final Properties filterProperties = new Properties();

            final Properties aksessProperties = getConfiguration().getProperties();

            Enumeration<String> names = (Enumeration<String>) aksessProperties.propertyNames();

            while(names.hasMoreElements()) {
                String name = names.nextElement();
                if (name.startsWith(filterConfig.getFilterName() + ".")) {
                    filterProperties.setProperty(name.substring(filterConfig.getFilterName().length()+".".length()), aksessProperties.getProperty(name));
                }
            }

            wrappedFilter.init(new FilterConfig() {
                public String getFilterName() {
                    return filterConfig.getFilterName();
                }

                public ServletContext getServletContext() {
                    return filterConfig.getServletContext();
                }

                public String getInitParameter(String name) {
                    return filterProperties.getProperty(name);
                }

                public Enumeration getInitParameterNames() {
                    return filterProperties.propertyNames();
                }
            });
            
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | ConfigurationException e) {
            throw new ServletException(e);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse
            response, FilterChain chain) throws IOException, ServletException {
        wrappedFilter.doFilter(request, response, chain);
    }

    public void destroy() {
        wrappedFilter.destroy();
    }
}
