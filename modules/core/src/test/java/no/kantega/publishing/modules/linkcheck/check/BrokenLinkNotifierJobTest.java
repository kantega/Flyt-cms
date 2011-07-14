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

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import no.kantega.publishing.common.ao.LinkDao;

import org.junit.Before;
import org.junit.Test;

public class BrokenLinkNotifierJobTest {

	private LinkDao linkDao;
	private List<BrokenLinkEventListener> listeners;
    BrokenLinkEventListener listener1;
    BrokenLinkEventListener listener2;
    private String sortBy = "url";


    @Before
	public void setUp() {
		linkDao = mock(LinkDao.class);
        listener1 = mock(BrokenLinkEventListener.class);
        listener2 = mock(BrokenLinkEventListener.class);
        listeners = new ArrayList<BrokenLinkEventListener>();
        listeners.add(listener1);
        listeners.add(listener2);
    }

    @Test
	public void shouldPassBrokenLinksToLinkEventListeners() throws Exception {
		List<LinkOccurrence> links = new ArrayList<LinkOccurrence >();
		when(linkDao.getAllBrokenLinks(sortBy)).thenReturn(links);

        BrokenLinkNotifierJob notifierJob = getBrokenLinkNotifierJob();
        notifierJob.execute();
		verify(listener1).process(links);
        verify(listener2).process(links);
	}


    private BrokenLinkNotifierJob getBrokenLinkNotifierJob() {
        BrokenLinkNotifierJob notifierJob = new BrokenLinkNotifierJob();
        notifierJob.setListeners(listeners);
        notifierJob.setLinkDao(linkDao);
        notifierJob.setSortBy(sortBy);
        return notifierJob;
    }
}
