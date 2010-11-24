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

package no.kantega.formadmin.presentation.util;

import no.kantega.formadmin.plugin.FormTypeConfigurationAccessor;
import no.kantega.formadmin.presentation.taglib.FormadminMapEntry;
import no.kantega.formadmin.presentation.taglib.FormadminObjectType;
import no.kantega.formengine.model.FormTypeInstance;
import no.kantega.formengine.state.State;
import no.kantega.publishing.common.Aksess;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class FormTypeNavigatorMapper {

    private FormTypeConfigurationAccessor configurationAccessor;

    @Autowired
    public FormTypeNavigatorMapper(FormTypeConfigurationAccessor configurationAccessor) {
        this.configurationAccessor = configurationAccessor;
    }

    /**
     * Creates a navigator tree strucure from the list of form instances.
     *
     * @param instances All form instances
     * @param currentInstance
     * @param currentState
     * @param openInstances
     * @return Navigator tree
     */
    public FormadminMapEntry mapInstancesToNavigatorMapEntries(List<FormTypeInstance> instances, int currentInstance, String currentState, int[] openInstances) {
        FormadminMapEntry root = new FormadminMapEntry();
        root.setName("Skjemaarkiv");
        root.setObjectType(FormadminObjectType.ROOT);
        root.setUrl("root");
        if (instances != null && !instances.isEmpty()) {
            root.setHasChildren(true);
        } else {
            root.setHasChildren(false);
            return root;
        }

        for (FormTypeInstance instance : instances) {
            FormadminMapEntry instanceEntry = new FormadminMapEntry();
            instanceEntry.setName(instance.getName());
            instanceEntry.setObjectType(FormadminObjectType.FORM_TYPE_INSTANCE);
            int instanceId = instance.getFormTypeInstanceIdentifier().getId();
            instanceEntry.setId(instanceId);
            if (instanceId == currentInstance) {
                instanceEntry.setOpen(true);
                instanceEntry.setSelected(true);
            } else {
                instanceEntry.setOpen(false);
                instanceEntry.setSelected(false);
            }
            if (!instanceEntry.isOpen() && openInstances != null && openInstances.length > 0) {
                for (Integer openInstance : openInstances) {
                    if (instanceId == openInstance) {
                        instanceEntry.setOpen(true);
                    }
                }
            }
            instanceEntry.setUrl("instanceId="+instanceId);

            if (instance.getFormType() != null) {
                List<State> states = configurationAccessor.getStates(instance.getFormType().getFormTypeIdentifier());
                if (states != null && !states.isEmpty()) {
                    instanceEntry.setHasChildren(true);
                    for (State state : states) {
                        FormadminMapEntry stateEntry = new FormadminMapEntry();
                        stateEntry.setName(state.getDisplayName(Aksess.getDefaultAdminLocale()));
                        stateEntry.setObjectType(FormadminObjectType.STATE);
                        String stateId = state.getStateIdentifier().getId();
                        stateEntry.setId(stateId.hashCode());
                        if (stateId.equals(currentState)) {
                            stateEntry.setSelected(true);
                        } else {
                            stateEntry.setSelected(false);
                        }
                        stateEntry.setHasChildren(false);
                        stateEntry.setOpen(false);
                        stateEntry.setUrl("instanceId="+instanceId +"&stateId="+stateId);
                        instanceEntry.addChild(stateEntry);
                    }
                }
            }
            root.addChild(instanceEntry);
        }
        return root;
    }
}
