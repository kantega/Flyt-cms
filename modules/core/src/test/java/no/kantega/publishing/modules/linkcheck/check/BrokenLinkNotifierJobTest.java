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
