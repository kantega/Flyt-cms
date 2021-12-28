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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Singleton object for holding a reference to the root ApplicationContext.
 * Should be avoided if WebApplicationContextUtils.getRequiredWebApplicationContext() can be used.
 */
public class RootContext implements ApplicationContextAware {

    private static ApplicationContext instance;

    public void setApplicationContext(ApplicationContext context)  {
        setInstance(context);
    }
    public static ApplicationContext getInstance() {
        if (instance == null) throw new RuntimeException("RootContext.setInstance() has not yet been run! Is your application context properly set up?");
        return instance;
    }

    public static void setInstance(ApplicationContext instance) {
        RootContext.instance = instance;
    }
}
