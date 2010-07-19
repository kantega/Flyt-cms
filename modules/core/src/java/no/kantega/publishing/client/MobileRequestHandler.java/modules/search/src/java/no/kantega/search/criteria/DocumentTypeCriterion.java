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

package no.kantega.search.criteria;

import no.kantega.search.index.Fields;

/**
 * Date: Jan 7, 2009
 * Time: 1:04:37 PM
 *
 * @author Tarje Killingberg
 */
public class DocumentTypeCriterion extends IntTermArrayCriterion {

    private static final String SOURCE = DocumentTypeCriterion.class.getName();


    public DocumentTypeCriterion(int documentType) {
        super(documentType);
    }

    public DocumentTypeCriterion(int[] documentTypeArray) {
        super(documentTypeArray);
    }

    @Override
    protected String getField() {
        return Fields.DOCUMENT_TYPE_ID;
    }

}
