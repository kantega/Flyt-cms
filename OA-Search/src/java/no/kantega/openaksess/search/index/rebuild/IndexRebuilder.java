package no.kantega.openaksess.search.index.rebuild;

import no.kantega.commons.log.Log;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class IndexRebuilder {

    @Autowired
    private List<IndexableDocumentProvider> indexableDocumentProviders;

    @Autowired
    private DocumentIndexer documentIndexer;
    private final String category = getClass().getName();

    @PostConstruct
    public void reindex(){
        doReindex(new ProgressReporter() {
            public void reportProgress(long current, String docType, long total) {
                System.out.println(String.format("%s %s/%s", docType, current, total));
            }

            public void reportFinished() {
                System.out.println("Finished");
            }
        });
    }


    public void doReindex(ProgressReporter progressReporter){
        int nThreads = 15;
        Log.info(category, String.format("Starting reindex with a threadpool of size %s ", 15));
        StopWatch stopWatch = new StopWatch(category);
        stopWatch.start();

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        for (IndexableDocumentProvider indexableDocumentProvider : indexableDocumentProviders) {
            long numberOfDocuments = indexableDocumentProvider.getNumberOfDocuments();
            Iterator<IndexableDocument> indexableDocumentIterator = indexableDocumentProvider.provideDocuments();
            long progressCounter = 0L;
            while (indexableDocumentIterator.hasNext()){
                final IndexableDocument next = indexableDocumentIterator.next();
                if (next.shouldIndex()) {
                    progressReporter.reportProgress(progressCounter++, next.getContentType(), numberOfDocuments);
                    executorService.execute(new Runnable() {
                        public void run() {
                            documentIndexer.indexDocument(next);
                        }
                    });

                }
            }
        }
        documentIndexer.commit();
        documentIndexer.optimize();
        stopWatch.stop();
        double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
        Log.info(category, String.format("Finished reindex. Used %s seconds ", totalTimeSeconds));
    }
}
