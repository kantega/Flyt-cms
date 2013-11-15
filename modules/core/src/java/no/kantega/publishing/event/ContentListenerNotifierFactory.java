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

package no.kantega.publishing.event;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Iterator;

/**
 *
 */
public class ContentListenerNotifierFactory implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private String contentListenerNotifierId;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setContentListenerNotifierId(String contentListenerNotifierId) {
        this.contentListenerNotifierId = contentListenerNotifierId;
    }

    public ContentEventListener createInstance() {
        return (ContentEventListener) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {ContentEventListener.class}, new InvocationHandler() {
            public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
                Map eventListeners = applicationContext.getBeansOfType(ContentEventListener.class);

                Iterator it = eventListeners.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String)it.next();
                    if (!key.equals(contentListenerNotifierId)) {
                        ContentEventListener contentEventListener = (ContentEventListener) eventListeners.get(key);
                        method.invoke(contentEventListener, objects);
                    }
                }
                return null;
            }
        });
    }
}
