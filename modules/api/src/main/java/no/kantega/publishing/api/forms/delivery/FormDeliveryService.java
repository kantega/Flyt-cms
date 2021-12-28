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

package no.kantega.publishing.api.forms.delivery;

import no.kantega.publishing.api.forms.model.FormSubmission;

/**
 * FormDeliveryServices provide a plugin mechanism for Flyt CMS to send the result of submitted forms to a backend system
 * e.g via email, web services
 */
public interface FormDeliveryService {
    /**
     *
     * @return - A unique id for this FormDeliveryService
     */
    String getId();

    /**
     * Send a form to backend system
     * @param formSubmission - An object containing the result of a form submission by a user
     */
    void deliverForm(FormSubmission formSubmission);
}
