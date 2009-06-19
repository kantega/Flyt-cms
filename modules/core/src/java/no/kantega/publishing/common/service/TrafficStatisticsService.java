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

package no.kantega.publishing.common.service;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.ao.TrafficLogAO;
import no.kantega.publishing.common.data.TrafficLogQuery;

import java.util.List;

public class TrafficStatisticsService {
    /*
     *  Statistikk
     */

    public int getNumberOfVisitsInPeriod(TrafficLogQuery query) throws SystemException {
        return TrafficLogAO.getNumberOfHitsOrSessionsInPeriod(query, false);
    }

    public int getNumberOfSessionsInPeriod(TrafficLogQuery query) throws SystemException {
        return TrafficLogAO.getNumberOfHitsOrSessionsInPeriod(query, true);
    }

    public List getReferersInPeriod(TrafficLogQuery query) throws SystemException {
        return TrafficLogAO.getReferersInPeriod(query);
    }
    public List getReferingHostsInPeriod(TrafficLogQuery query) throws SystemException {
        return TrafficLogAO.getReferingHostsInPeriod(query);
    }

    public List getReferingQueriesInPeriod(TrafficLogQuery query) throws SystemException {
        return TrafficLogAO.getReferingQueriesInPeriod(query);
    }
}
