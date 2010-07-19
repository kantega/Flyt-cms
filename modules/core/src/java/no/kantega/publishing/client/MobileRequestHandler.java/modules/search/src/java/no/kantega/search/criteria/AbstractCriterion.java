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

import org.apache.lucene.search.BooleanClause;

/**
 * Date: Jan 14, 2009
 * Time: 1:10:47 PM
 *
 * @author Tarje Killingberg
 */
public abstract class AbstractCriterion implements Criterion {

    private static final String SOURCE = AbstractCriterion.class.getName();

    private BooleanClause.Occur operator = BooleanClause.Occur.MUST;


    /**
     * {@inheritDoc}
     */
    public BooleanClause.Occur getOperator() {
        return operator;
    }

    public void setOperator(BooleanClause.Occur operator) {
        this.operator = operator;
    }

}
