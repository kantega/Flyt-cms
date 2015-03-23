package no.kantega.openaksess.search.index.rebuild;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import no.kantega.search.api.index.ProgressReporter;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static no.kantega.openaksess.search.index.rebuild.ProgressReporterUtils.notAllProgressReportersAreMarkedAsFinished;

@Component
public class IndexRebuilder {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<IndexableDocumentProvider> indexableDocumentProviders;

    @Autowired
    private DocumentIndexer documentIndexer;

    @Autowired
    private TaskExecutor executorService;

    @Value("${IndexRebuilder.queueLength:1}")
    private int queueLength = 1;

    private List<ProgressReporter> progressReporters;

    BlockingQueue<IndexableDocument> indexableDocuments;

    BlockingQueue<IndexableDocument> testing;

    public synchronized void stopIndexing(){
        for (ProgressReporter progressReporter : progressReporters) {
            progressReporter.setIsFinished(true);
        }
    }

    public List<ProgressReporter> startIndexing(List<String> providersToInclude) {
        indexableDocuments = new LinkedBlockingQueue<>(queueLength);
        testing = new LinkedBlockingQueue<>(queueLength);

        Collection<IndexableDocumentProvider> providers = filterProviders(providersToInclude);

        progressReporters = getProgressReporters(providers);
        startConsumer(indexableDocuments, progressReporters);
        startProducer(indexableDocuments, providers);

        return progressReporters;
    }

    private void startProducer(final BlockingQueue<IndexableDocument> indexableDocuments, final Collection<IndexableDocumentProvider> indexableDocumentProviders) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (IndexableDocumentProvider provider : indexableDocumentProviders) {
                    log.info("Starting IndexableDocumentProvider {}", provider.getName());
                    provider.provideDocuments(indexableDocuments);
                    log.info("Finished IndexableDocumentProvider {}", provider.getName());
                }
            }
        });
    }

    private void startConsumer(final BlockingQueue<IndexableDocument> indexableDocuments, final List<ProgressReporter> progressReporters) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                log.info("Starting reindex");
                StopWatch stopWatch = new StopWatch("IndexRebuilder");
                stopWatch.start();
                for (ProgressReporter progressReporter : progressReporters) {
                    String docType = progressReporter.getDocType();
                    documentIndexer.deleteByDocType(docType);
                }
                documentIndexer.commit();
                try {
                    while (notAllProgressReportersAreMarkedAsFinished(progressReporters)) {
                        IndexableDocument poll = indexableDocuments.poll(60, TimeUnit.SECONDS);
                        if (poll != null) {
                            //log.info("Indexing document {} {}", poll.getUId(), poll.getTitle());
                            documentIndexer.indexDocument(poll);
                        } else {
                            log.error("Polling IndexableDocumentQueue resulted in null!");
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("Interrupted!", e);
                } finally {
                    documentIndexer.commit();
                    documentIndexer.optimize();
                    stopWatch.stop();
                    double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
                    log.info("Finished reindex. Used {} seconds ", totalTimeSeconds);
                }
            }
        });
    }

    private Collection<IndexableDocumentProvider> filterProviders(final List<String> providersToInclude) {
        return filter(indexableDocumentProviders, new Predicate<IndexableDocumentProvider>() {
            @Override
            public boolean apply(IndexableDocumentProvider input) {
                return providersToInclude.contains(input.getClass().getSimpleName());
            }
        });
    }

    private ArrayList<ProgressReporter> getProgressReporters(Collection<IndexableDocumentProvider> providers) {
        return new ArrayList<>(transform(providers, new Function<IndexableDocumentProvider, ProgressReporter>() {
            @Override
            public ProgressReporter apply(IndexableDocumentProvider input) {
                return input.getProgressReporter();
            }
        }));
    }

    public void deleteIndex() {
        documentIndexer.deleteAllDocuments();
        documentIndexer.commit();
        documentIndexer.optimize();
    }
}
