package no.kantega.openaksess.search.index.rebuild;

import no.kantega.commons.log.Log;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import no.kantega.search.api.index.ProgressReporter;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static no.kantega.openaksess.search.index.rebuild.ProgressReporterUtils.notAllProgressReportersAreMarkedAsFinished;

@Component
public class IndexRebuilder {

    @Autowired
    private List<IndexableDocumentProvider> indexableDocumentProviders;

    @Autowired
    private DocumentIndexer documentIndexer;
    private final String category = getClass().getName();

    public List<ProgressReporter> startIndexing(int nThreads, List<String> providersToExclude) {
        documentIndexer.deleteAllDocuments();

        final List<ProgressReporter> progressReporters = new ArrayList<ProgressReporter>();
        final BlockingQueue<IndexableDocument> indexableDocuments = new LinkedBlockingQueue<IndexableDocument>();
        for (IndexableDocumentProvider indexableDocumentProvider : indexableDocumentProviders) {
            boolean providerIsNotExcluded = !providersToExclude.contains(indexableDocumentProvider.getClass().getSimpleName());
            if (providerIsNotExcluded) {
                ProgressReporter progressReporter = indexableDocumentProvider.provideDocuments(indexableDocuments, nThreads);
                progressReporters.add(progressReporter);
            }
        }
        Executors.newSingleThreadExecutor().execute((new Runnable() {
            public void run() {
                Log.info(category, "Starting reindex");
                StopWatch stopWatch = new StopWatch(category);
                stopWatch.start();
                try {
                    while (notAllProgressReportersAreMarkedAsFinished(progressReporters)) {
                        IndexableDocument poll = indexableDocuments.poll(10, TimeUnit.SECONDS);
                        documentIndexer.indexDocument(poll);
                    }
                } catch (InterruptedException e) {
                    Log.error(category, e);
                }finally {
                    documentIndexer.commit();
                    documentIndexer.optimize();
                    stopWatch.stop();
                    double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
                    Log.info(category, String.format("Finished reindex. Used %s seconds ", totalTimeSeconds));
                }
            }
        }));
        return progressReporters;
    }
}
