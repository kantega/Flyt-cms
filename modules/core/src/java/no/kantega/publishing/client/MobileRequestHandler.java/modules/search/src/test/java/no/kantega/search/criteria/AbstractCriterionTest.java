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

import no.kantega.search.AbstractSearchTestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Date: Dec 5, 2008
 * Time: 2:09:42 PM
 *
 * @author Tarje Killingberg
 */
public abstract class AbstractCriterionTest extends AbstractSearchTestCase {

    private static final String SOURCE = AbstractCriterionTest.class.getName();
    protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

}
