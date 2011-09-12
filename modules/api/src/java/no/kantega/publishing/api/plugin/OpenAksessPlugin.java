
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
import no.kantega.publishing.api.ui.UIContribution;
import org.quartz.Scheduler;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.Filter;
import java.util.List;


public interface OpenAksessPlugin {


    List<HandlerMapping> getHandlerMappings();

    List<ContentRequestListener> getContentRequestListeners();

    List<FormDeliveryService> getFormDeliveryServices();

    List<MessageSource> getMessageSources();

    List<Filter> getRequestFilters();

    List<UIContribution> getUIContributions();

    Scheduler getScheduler();

    String getPluginUid();
}
