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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Date: Jan 14, 2009
 * Time: 12:16:01 PM
 *
 * @author Tarje Killingberg
 */
public class ContentParentCriterion extends AbstractCriterion {

    private static final String SOURCE = ContentParentCriterion.class.getName();

    private int parentId;

    /**
     * Oppretter et Criterion-objekt som begrenser søkeresultatet til kun å inneholde undersider til den gitte ID'en.
     *
     * @param parentId ID'en til roten på treet som det skal søkes i
     */
    public ContentParentCriterion(int parentId) {
        this.parentId = parentId;
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery() {
        Query query;
        String parentStr = Integer.toString(parentId);
        query = new TermQuery(new Term(Fields.CONTENT_PARENTS, parentStr));
        return query;
    }

}
