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

package no.kantega.search.result;

import java.util.ArrayList;
import java.util.List;


/**
 * Date: Dec 2, 2008
 * Time: 12:07:04 PM
 *
 * @author Tarje Killingberg
 */
public class SearchResultDefaultImpl implements SearchResult {

    private static final String SOURCE = SearchResultDefaultImpl.class.getName();

    private List<DocumentHit> documentHits;
    private int numberOfHits;
    private long time;
    private QueryInfo queryInfo;


    public SearchResultDefaultImpl() {
        documentHits = new ArrayList<DocumentHit>();
    }

    /**
     * {@inheritDoc}
     */
    public List<DocumentHit> getDocumentHits() {
        return documentHits;
    }

    public void setDocumentHits(List<DocumentHit> searchHits) {
        this.documentHits = searchHits;
    }

    public void addDocumentHit(DocumentHit searchHit) {
        documentHits.add(searchHit);
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfHits() {
        return numberOfHits;
    }

    public void setNumberOfHits(int numberOfHits) {
        this.numberOfHits = numberOfHits;
    }

    /**
     * {@inheritDoc}
     */
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**
     * {@inheritDoc}
     */
    public QueryInfo getQueryInfo() {
        return queryInfo;
    }

    public void setQueryInfo(QueryInfo queryInfo) {
        this.queryInfo = queryInfo;
    }

}
