package no.kantega.publishing.modules.linkcheck.check;

import java.util.List;

public interface BrokenLinkEventListener {

	void process(List<LinkOccurrence> links);

}
