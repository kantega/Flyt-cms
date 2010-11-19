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

package no.kantega.formadmin.plugin;

import no.kantega.formengine.model.FormTypeIdentifier;
import no.kantega.formengine.plugin.FormEnginePlugin;
import no.kantega.formengine.plugin.FormTypeConfiguration;
import no.kantega.formengine.state.State;
import org.kantega.jexmec.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Utility for extracting information from form type configurations.
 */
public class FormTypeConfigurationAccessor {

    private PluginManager<FormEnginePlugin> pluginManager;

    @Autowired
    public FormTypeConfigurationAccessor(@Qualifier("formEnginePluginManager") PluginManager<FormEnginePlugin> pluginManager) {
        this.pluginManager = pluginManager;
    }

    /**
     * Gets all the states for a given form type
     *
     * @param formType FormTypeIdentifier
     * @return List of all available states.
     */
    public List<State> getStates(FormTypeIdentifier formType) {
        if (formType == null) {
            return null;
        }
        for (FormEnginePlugin plugin : pluginManager.getPlugins()) {
            for (FormTypeConfiguration configuration : plugin.getFormTypeConfigurations()) {
                if (configuration.supportsFormType(formType)) {
                    return configuration.getStates();
                }
            }
        }
        return null;
    }
}
