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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * //TODO
 */
public abstract class BooleanCriterion extends Criterion {

    private List<Criterion> criteria;


    public BooleanCriterion(List<Criterion> criteria) {
        this.criteria = criteria;
    }

    public BooleanCriterion(Criterion left, Criterion right) {
        this.criteria = new ArrayList<Criterion>();
        criteria.add(left);
        criteria.add(right);
    }


    /**
     * Returnerer operatoren som skal benyttes for å kombinere Criterion-objektetene i dette BooleanCriterion-objektet.
     * @return operatoren som skal benyttes for å kombinere Criterion-objektetene i dette BooleanCriterion-objektet
     */
    protected abstract String getInnerOperator();

    public void add(Criterion criterion) {
        criteria.add(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public String getCriterionAsString() {
        StringBuilder queryBuilder = new StringBuilder();

        for (Iterator<Criterion> iterator = criteria.iterator(); iterator.hasNext(); ) {
            Criterion c = iterator.next();
            queryBuilder.append(c.getCriterionAsString());
            if(iterator.hasNext()){
                queryBuilder.append(' ');
                queryBuilder.append(getInnerOperator());
                queryBuilder.append(' ');
            }
        }
        return queryBuilder.toString();
    }

}
