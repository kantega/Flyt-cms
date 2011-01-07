
/*
 * Copyright 2010 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.api.plugin;

import no.kantega.publishing.api.forms.delivery.FormDeliveryService;
import no.kantega.publishing.api.requestlisteners.ContentRequestListener;
import org.kantega.jexmec.AbstractPlugin;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

public class OpenAksessPluginAdapter extends AbstractPlugin implements OpenAksessPlugin, ApplicationContextAware {

    private List<HandlerMapping> handlerMappings = Collections.emptyList();

    private List<ContentRequestListener> contentRequestListeners = Collections.emptyList();

    private List<FormDeliveryService> formDeliveryServices = Collections.emptyList();
    private ApplicationContext applicationContext;

    private List<Filter> requestFilters = Collections.emptyList();

    public OpenAksessPluginAdapter(String pluginId) {
        super(pluginId);
    }

    public void setHandlerMappings(List<HandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    public List<HandlerMapping> getHandlerMappings() {
        return handlerMappings;
    }

    public List<ContentRequestListener> getContentRequestListeners() {
        return contentRequestListeners;
    }

    public void setContentRequestListeners(List<ContentRequestListener> contentRequestListeners) {
        this.contentRequestListeners = contentRequestListeners;
    }

    public List<FormDeliveryService> getFormDeliveryServices() {
        return formDeliveryServices;
    }

    public void setFormDeliveryServices(List<FormDeliveryService> formDeliveryServices) {
        this.formDeliveryServices = formDeliveryServices;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public List<MessageSource> getMessageSources() {
        return Collections.singletonList((MessageSource) applicationContext);
    }

    public List<Filter> getRequestFilters() {
        return requestFilters;
    }

    public void setRequestFilters(List<Filter> requestFilters) {
        this.requestFilters = requestFilters;
    }
    
}
