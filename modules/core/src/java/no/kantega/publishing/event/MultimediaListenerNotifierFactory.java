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

import no.kantega.publishing.api.multimedia.event.MultimediaEventListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Creates a MultimediaEventListener that notifies all other MultimediaEventListeners.
 */
public class MultimediaListenerNotifierFactory implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private String multimediaListenerNotifierId;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setMultimediaListenerNotifierId(String multimediaListenerNotifierId) {
        this.multimediaListenerNotifierId = multimediaListenerNotifierId;
    }

    public MultimediaEventListener createInstance() {
        return (MultimediaEventListener) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {MultimediaEventListener.class}, new InvocationHandler() {
            public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
                Map<String, MultimediaEventListener> eventListeners = applicationContext.getBeansOfType(MultimediaEventListener.class);

                for (Map.Entry<String, MultimediaEventListener> listener : eventListeners.entrySet()) {
                    if (!listener.getKey().equals(multimediaListenerNotifierId)) {
                        MultimediaEventListener contentEventListener = listener.getValue();
                        method.invoke(contentEventListener, objects);
                    }
                }
                return null;
            }
        });
    }
}
