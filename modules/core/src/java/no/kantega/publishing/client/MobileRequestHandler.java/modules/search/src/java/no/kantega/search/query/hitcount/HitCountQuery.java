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

package no.kantega.search.query.hitcount;

import org.apache.lucene.index.IndexReader;

import java.io.IOException;

/**
 * Date: Jan 23, 2009
 * Time: 9:01:36 AM
 *
 * @author Tarje Killingberg
 */
public interface HitCountQuery {


    /**
     *
     * @return
     */
    public String getField();

    /**
     *
     * @return
     */
    public String[] getTerms();

    /**
     *
     * @return
     */
    public boolean isIgnoreOther();

    /**
     *
     * @param reader
     * @return
     * @throws IOException
     */
    public QueryEnumeration getQueryEnumeration(IndexReader reader) throws IOException;

}
