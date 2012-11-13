/*
 * Copyright 2011 Kantega AS
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
package no.kantega.publishing.modules.linkcheck.check;

import no.kantega.publishing.common.ao.LinkDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class BrokenLinkNotifierJob {

	@Autowired
    private LinkDao linkDao;
    @Autowired
	private List<BrokenLinkEventListener> listeners = Collections.emptyList();

    private String sortBy = "";

	public void execute() {
        List<LinkOccurrence> brokenlinks = linkDao.getAllBrokenLinks(sortBy);
        for(BrokenLinkEventListener listener : listeners){
            listener.process(brokenlinks);
        }
	}

    public void setSortBy(String sortBy){
        this.sortBy = sortBy;
    }

    public void setLinkDao(LinkDao linkDao) {
        this.linkDao = linkDao;
    }

    public void setListeners(List<BrokenLinkEventListener> listeners) {
        this.listeners = listeners;
    }
}
