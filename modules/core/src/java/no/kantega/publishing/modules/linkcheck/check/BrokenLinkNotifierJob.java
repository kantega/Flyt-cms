package no.kantega.publishing.modules.linkcheck.check;

import no.kantega.publishing.common.ao.LinkDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class BrokenLinkNotifierJob {

	@Autowired LinkDao linkDao;
	private List<BrokenLinkEventListener> listeners = new ArrayList<BrokenLinkEventListener>();
    private String sortBy = "";

	public void execute() {
        List<LinkOccurrence> brokenlinks = linkDao.getAllBrokenLinks(sortBy);
        for(BrokenLinkEventListener listener : listeners){
            listener.process(brokenlinks);
        }
	}

    public void setLinkDao(LinkDao linkDao){
        this.linkDao = linkDao;
    }

    public void setListeners(List<BrokenLinkEventListener> listeners){
        this.listeners = listeners;
    }

    public void setSortBy(String sortBy){
        this.sortBy = sortBy;
    }
}
