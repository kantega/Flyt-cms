package no.kantega.publishing.search.index.provider;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.log.Log;
import no.kantega.commons.media.MimeTypes;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.MultimediaDao;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.PathEntry;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.service.impl.PathWorker;
import no.kantega.publishing.common.util.Counter;
import no.kantega.publishing.search.extraction.TextExtractor;
import no.kantega.publishing.search.extraction.TextExtractorSelector;
import no.kantega.publishing.search.index.jobs.RebuildIndexJob;
import no.kantega.publishing.search.model.AksessSearchHit;
import no.kantega.publishing.search.model.AksessSearchHitContext;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.search.index.Fields;
import no.kantega.search.index.provider.DocumentProvider;
import no.kantega.search.index.provider.DocumentProviderHandler;
import no.kantega.search.index.rebuild.ProgressReporter;
import no.kantega.search.result.SearchHit;
import no.kantega.search.result.SearchHitContext;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Lists.partition;

public class DefaultMultimediaContentProvider implements DocumentProvider {
    private String SOURCE = getClass().getSimpleName();
    private MultimediaDao multimediaDao;
    private TextExtractorSelector textExtractorSelector;

    private String sourceId;

    public String getDocumentType() {
        return Fields.TYPE_MULTIMEDIA;
    }

    public void provideDocuments(DocumentProviderHandler handler, ProgressReporter reporter) {
        provideDocuments(handler, reporter, Collections.emptyMap());
    }

    public void provideDocuments(DocumentProviderHandler handler, ProgressReporter reporter, Map options) {
        final Counter c = new Counter();

        Integer numberOfConcurrentHandlers = 1;
        if(options.containsKey(RebuildIndexJob.NUMBEROFCONCURRENTHANDLERS)){
            numberOfConcurrentHandlers = (Integer) options.get(RebuildIndexJob.NUMBEROFCONCURRENTHANDLERS);
        }

        try {
            List<Integer> multimediaIds = multimediaDao.getAllMultimediaIds();
            int partitionSize = (multimediaIds.size() / numberOfConcurrentHandlers) + 1;
            List<List<Integer>> attachmentIdsPartition = partition(multimediaIds, partitionSize);
            CyclicBarrier cyclicBarrier = new CyclicBarrier(multimediaIds.size() + 1);

            Log.info(SOURCE, "Starting provideDocuments, number of concurrent handlers: " + numberOfConcurrentHandlers);
            ExecutorService pool = Executors.newFixedThreadPool(numberOfConcurrentHandlers);

            for(List<Integer> identifiers : attachmentIdsPartition){
                pool.submit(new worker(handler, reporter, c, cyclicBarrier, identifiers, multimediaIds.size()));
            }
            cyclicBarrier.await();
            Log.info(SOURCE, "All threads done executing handlers");
        } catch (InterruptedException e) {
            Log.error(SOURCE, e.getMessage());
        } catch (BrokenBarrierException e) {
            Log.error(SOURCE, e.getMessage());
        }

    }

    public Document provideDocument(String id) {
        Multimedia multimedia = multimediaDao.getMultimedia(Integer.parseInt(id));

        Document document = new Document();
        document.add(new Field(Fields.DOCTYPE, Fields.TYPE_MULTIMEDIA, Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field(Fields.CONTENT_ID, id, Field.Store.YES, Field.Index.NOT_ANALYZED));
        String title = multimedia.getName();
        Field fTitle = new Field(Fields.TITLE, title, Field.Store.YES, Field.Index.ANALYZED);
        fTitle.setBoost(2);
        document.add(fTitle);

        Field fAltTitle = new Field(Fields.ALT_TITLE, multimedia.getAltname(), Field.Store.YES, Field.Index.ANALYZED);
        fAltTitle.setBoost(2);
        document.add(fAltTitle);

        document.add(new Field(Fields.TITLE_UNANALYZED, title, Field.Store.NO, Field.Index.NOT_ANALYZED));
        document.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(multimedia.getLastModified(), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field(Fields.SUMMARY, multimedia.getDescription(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field(Fields.URL, multimedia.getUrl(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field(Fields.CONTENT_PARENTS, getParents(multimedia), Field.Store.YES, Field.Index.ANALYZED));
        if (multimedia.getType() != MultimediaType.FOLDER) {
            document.add(new Field(Fields.FILE_TYPE, getSuffix(multimedia.getFilename()).toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));

            String filename = multimedia.getFilename();
            TextExtractor te = textExtractorSelector.select(filename);
            if(te != null){
                InputStream data = multimediaDao.getDataForMultimedia(multimedia.getId());
                String content = te.extractText(data, filename);
                document.add(new Field(Fields.CONTENT, content, Field.Store.YES, Field.Index.ANALYZED));

            }
        }

        return document;
    }

    private String getParents(Multimedia multimedia) {
        int parentId = multimedia.getParentId();
        StringBuilder parents = new StringBuilder();
        while (parentId != 0){
            parents.append(parentId);
            parents.append(' ');
            Multimedia parent = multimediaDao.getMultimedia(parentId);
            parentId = parent.getParentId();
        }

        return parents.toString();
    }

    public Term getDeleteTerm(String id) {
        return new Term(Fields.CONTENT_ID, id);
    }

    public Term getDeleteAllTerm() {
        return new Term(Fields.DOCTYPE, Fields.TYPE_MULTIMEDIA);
    }

    public SearchHit createSearchHit() {
        return new AksessSearchHit();
    }

    public void processSearchHit(SearchHit searchHit, SearchHitContext searchHitContext, Document doc) throws NotAuthorizedException {
        AksessSearchHitContext aContext = null;
        if (searchHitContext instanceof AksessSearchHitContext) {
            aContext = (AksessSearchHitContext)searchHitContext;
        }
        int multimediaId = Integer.parseInt(doc.get(Fields.CONTENT_ID));
        Multimedia multimedia = multimediaDao.getMultimedia(multimediaId);

        throwNotAuthorizedExceptionIfNotAuthorized(aContext, multimedia);

        AksessSearchHit aksessSearchHit = (AksessSearchHit)searchHit;
        aksessSearchHit.setDocument(doc);
        aksessSearchHit.setTitle(multimedia.getName());
        aksessSearchHit.setId(multimediaId);
        String filename = multimedia.getFilename();
        aksessSearchHit.setFileExtension(getSuffix(filename));
        aksessSearchHit.setFileSize(multimedia.getSize());
        aksessSearchHit.setFileName(filename);
        aksessSearchHit.setLastModified(multimedia.getLastModified());
        aksessSearchHit.setMimeType(MimeTypes.getMimeType(multimedia.getFilename()));
        aksessSearchHit.setSummary(multimedia.getDescription());
        aksessSearchHit.setUrl(multimedia.getUrl());

        List<PathEntry> path = PathWorker.getMultimediaPath(multimedia);
        aksessSearchHit.setPathElements(path);
        ((AksessSearchHit) searchHit).setDoOpenInNewWindow(Aksess.doOpenLinksInNewWindow());
    }

    private void throwNotAuthorizedExceptionIfNotAuthorized(AksessSearchHitContext aContext, Multimedia multimedia) throws NotAuthorizedException {
        SecuritySession session = aContext != null ? aContext.getSecuritySession() : null;
        if (session != null) {
            if (!session.isAuthorized(multimedia, Privilege.VIEW_CONTENT)) {
                throw new NotAuthorizedException("", getClass().getName());
            }
        }
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    private String getSuffix(String filename) {

        if(filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") +(filename.endsWith(".") ? 0 : 1));
        } else {
            return "";
        }

    }

    public void setMultimediaDao(MultimediaDao multimediaDao) {
        this.multimediaDao = multimediaDao;
    }

    public void setTextExtractorSelector(TextExtractorSelector textExtractorSelector) {
        this.textExtractorSelector = textExtractorSelector;
    }

    private class worker implements Runnable {

        private DocumentProviderHandler handler;
        private ProgressReporter reporter;
        private Counter counter;
        private CyclicBarrier cyclicBarrier;
        private List<Integer> identifiers;
        private int totalNumberMultimedia;

        public worker(DocumentProviderHandler handler, ProgressReporter reporter, Counter c, CyclicBarrier cyclicBarrier, List<Integer> identifiers, int size) {
            this.handler = handler;
            this.reporter = reporter;
            counter = c;
            this.cyclicBarrier = cyclicBarrier;
            this.identifiers = identifiers;
            totalNumberMultimedia = size;
        }

        public void run() {
            for (Integer identifier : identifiers) {
                if(handler.isStopRequested()) {
                    Log.info(SOURCE, "provideDocuments returning since stop where requested", null, null);
                    return;
                }

                try {
                    Document d = provideDocument(identifier.toString());
                    if (d != null) {
                        handler.handleDocument(d);
                    }
                    counter.increment();
                    reporter.reportProgress(counter.getI(), "aksess-multimedia", totalNumberMultimedia);

                } catch (Throwable e) {
                    Log.error(SOURCE, "Caught throwable during indexing of multimedia " + identifier, null, null);
                    Log.error(SOURCE, e, null, null);
                }
            }
            try {
                Log.info(SOURCE, "Thread done handling content");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                Log.error("Worker interupted", e);
            } catch (BrokenBarrierException e) {
                Log.error("Barrier error", e);
            }
        }
    }
}
