package no.kantega.openaksess.search.index.rebuild;

import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import no.kantega.search.api.index.ProgressReporter;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static no.kantega.openaksess.search.index.rebuild.ProgressReporterUtils.notAllProgressReportersAreMarkedAsFinished;

@Component
public class IndexRebuilder {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<IndexableDocumentProvider> indexableDocumentProviders;

    @Autowired
    private DocumentIndexer documentIndexer;

    @Value("${IndexRebuilder.queueLength:1}")
    private int queueLength = 1;

    @Value("${IndexRebuilder.maxPolling:5}")
    private int maxPolling;

    private List<ProgressReporter> progressReporters;

    public synchronized void stopIndexing(){
        for (ProgressReporter progressReporter : progressReporters) {
            progressReporter.setIsFinished(true);
        }
    }

    public List<ProgressReporter> startIndexing(List<String> providersToInclude) {
        BlockingQueue<IndexableDocument> indexableDocuments = new LinkedBlockingQueue<>(queueLength);
        Collection<IndexableDocumentProvider> providers = filterProviders(providersToInclude);
        progressReporters = getProgressReporters(providers);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        startConsumer(executorService, indexableDocuments, progressReporters);
        startProducer(executorService, indexableDocuments, providers);

        return progressReporters;
    }

    private void startProducer(ExecutorService executorService, final BlockingQueue<IndexableDocument> indexableDocuments, final Collection<IndexableDocumentProvider> indexableDocumentProviders) {
        executorService.execute(() -> {
            for (IndexableDocumentProvider provider : indexableDocumentProviders) {
                log.info("Starting IndexableDocumentProvider {}", provider.getName());
                provider.provideDocuments(indexableDocuments);
                log.info("Finished IndexableDocumentProvider {}", provider.getName());
            }
        });
    }

    private void startConsumer(ExecutorService executorService, final BlockingQueue<IndexableDocument> indexableDocuments, final List<ProgressReporter> progressReporters) {
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
                    int pollMistakes = 0;
                    while (hasNotReachedMaxRetries(pollMistakes) && notAllProgressReportersAreMarkedAsFinished(progressReporters)) {
                        log.debug("indexableDocuments size: {}", indexableDocuments.size());
                        IndexableDocument poll = indexableDocuments.poll(60, TimeUnit.SECONDS);
                        if (poll != null) {
                            log.debug("Got document {}, size: {}", poll.getId(), indexableDocuments.size());
                            if (poll.shouldIndex()) {
                                log.info("Indexing document {} {}", poll.getUId(), poll.getTitle());
                                documentIndexer.indexDocument(poll);
                            } else {
                                log.info("Should not index document {} {}", poll.getUId(), poll.getTitle());
                            }
                        } else {
                            pollMistakes++;
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
                    executorService.shutdown();
                }
            }

            private boolean hasNotReachedMaxRetries(int pollMistakes) {
                boolean hasReachedMaxRetries = pollMistakes >= maxPolling;
                if (hasReachedMaxRetries){
                    log.error("Polling IndexableDocumentQueue used " + maxPolling + " attempts and is terminated!");
                }
                return !hasReachedMaxRetries;
            }
        });
    }

    private Collection<IndexableDocumentProvider> filterProviders(final List<String> providersToInclude) {
        return indexableDocumentProviders.stream()
                .filter(indexableDocumentProvider -> providersToInclude.contains(indexableDocumentProvider.getClass().getSimpleName()))
                .collect(Collectors.toList());
    }

    private List<ProgressReporter> getProgressReporters(Collection<IndexableDocumentProvider> providers) {
        return providers.stream().map(IndexableDocumentProvider::getProgressReporter).collect(Collectors.toList());
    }

    public void deleteIndex() {
        documentIndexer.deleteAllDocuments();
        documentIndexer.commit();
        documentIndexer.optimize();
    }
}
