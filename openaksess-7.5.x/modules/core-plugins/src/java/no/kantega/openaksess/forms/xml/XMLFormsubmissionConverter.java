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

package no.kantega.openaksess.forms.xml;

import no.kantega.publishing.api.forms.model.FormSubmission;

/**
 * Create a XML document from a form submissions and return it as a string
 *
 * Format of document should be like this:
 *
 * <formsubmission>
 *      <metadata>
 *          <formname>form name</formname>
 *          <date>date of form submission creation</date>
 *      </metadata>
 *      <formvalues>
 *          <formvalue>
 *              <name>Name</name>
 *              <value>Test</value>
 *          </formvalue>
 *          <formvalue>
 *              <name>Email</name>
 *              <value>test@test.com</value>
 *          </formvalue>
 *      </formvalues>
 * </formsubmission>
 *
 *
 */
public interface XMLFormsubmissionConverter {
    public String createXMLFromFormSubmission(FormSubmission formSubmission);
}