package no.kantega.openaksess.search.dummy;

import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DummyDocumentIndexer implements DocumentIndexer {
    private static final Logger log = LoggerFactory.getLogger(DummyDocumentIndexer.class);

    @Override
    public void indexDocument(IndexableDocument document) {
        log.warn("noop indexDocument({})", document);
    }

    @Override
    public void indexDocumentAndCommit(IndexableDocument document) {
        log.warn("noop indexDocumentAndCommit({})", document);
    }

    @Override
    public void commit() {
        log.warn("noop commit()");
    }

    @Override
    public void deleteByUid(List<String> Uids) {
        log.warn("noop deleteByUid({})", Uids);
    }

    @Override
    public void deleteAllDocuments() {
        log.warn("noop deleteAllDocuments()");
    }

    @Override
    public void optimize() {
        log.warn("noop optimize()");
    }

    @Override
    public void deleteByDocType(String docType) {
        log.warn("noop deleteByDocType({})", docType);
    }
}
