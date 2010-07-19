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
 * SearchResult-implementasjon som, i tillegg til å inneholde det samme som SearchResultDefaultImpl, inneholder
 * antall treff per term for en eller flere kategorier.
 *
 * Date: Dec 1, 2008
 * Time: 3:52:48 PM
 *
 * @author Tarje Killingberg
 */
public class SearchResultExtendedImpl extends SearchResultDefaultImpl {

    private List<HitCount> hitCounts;
    private long extendedTime;


    public SearchResultExtendedImpl() {
        hitCounts = new ArrayList<HitCount>();
    }

    public void addHitCount(HitCount hitCount) {
        hitCounts.add(hitCount);
    }

    public List<HitCount> getHitCounts() {
        return hitCounts;
    }

    public void setExtendedTime(long extendedTime) {
        this.extendedTime = extendedTime;
    }

    public long getExtendedTime() {
        return extendedTime;
    }

}
