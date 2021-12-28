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

package no.kantega.publishing.spring;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Created by IntelliJ IDEA.
 * User: bjorsnos
 * Date: May 5, 2009
 * Time: 3:18:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class AksessDispatcherServlet extends DispatcherServlet {

    @Override
    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
        // Set up @Autowired support
        ApplicationContextUtils.addAutowiredSupport(wac);

        // Add appDir property
        ApplicationContextUtils.addAppDirPropertySupport(wac);

    }




}
