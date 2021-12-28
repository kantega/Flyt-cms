package no.kantega.openaksess.search.index.optimize;

import no.kantega.search.api.index.DocumentIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OptimizeIndexJob {
    @Autowired
    private DocumentIndexer documentIndexer;

    @Scheduled(cron = "${search.optimizeIndexCron:0 0 2 ? * SAT}")
    public void optimizeIndex(){
        documentIndexer.optimize();
    }
}
