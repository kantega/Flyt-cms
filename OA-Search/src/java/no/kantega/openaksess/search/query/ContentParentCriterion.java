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

package no.kantega.openaksess.search.query;

/**
 *
 */
public class ContentParentCriterion extends FieldCriterion {

    /**
     * Oppretter et Criterion-objekt som begrenser søkeresultatet til kun å inneholde undersider til den gitte ID'en.
     *
     * @param parentId ID'en til roten på treet som det skal søkes i
     */
    public ContentParentCriterion(int parentId) {
        super(Fields.CONTENT_PARENTS, String.valueOf(parentId));
    }
}
