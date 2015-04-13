/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.ContentViewStatistics;
import no.kantega.publishing.common.data.PeriodViewStatistics;
import no.kantega.publishing.common.data.RefererOccurrence;
import no.kantega.publishing.common.data.TrafficLogQuery;

import java.util.List;

/**
 *
 */
public interface TrafficLogDao {
    public int getNumberOfHitsOrSessionsInPeriod(TrafficLogQuery query, boolean sessions) throws SystemException;
    public List<ContentViewStatistics> getMostVisitedContentStatistics(TrafficLogQuery trafficQuery, int limit) throws SystemException;
    public List<PeriodViewStatistics> getPeriodViewStatistics(TrafficLogQuery trafficQuery, int period) throws SystemException;
    public List<RefererOccurrence> getReferersInPeriod(TrafficLogQuery query);
    public List<RefererOccurrence> getReferingHostsInPeriod(TrafficLogQuery query);
    public List<RefererOccurrence> getReferingQueriesInPeriod(TrafficLogQuery query);    
}
