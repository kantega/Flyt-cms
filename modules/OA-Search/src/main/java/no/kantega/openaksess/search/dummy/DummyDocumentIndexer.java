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
        log.debug("noop indexDocumentAndCommit({})", document);
    }

    @Override
    public void commit() {
        log.debug("noop commit()");
    }

    @Override
    public void deleteByUid(List<String> Uids) {
        log.debug("noop deleteByUid({})", Uids);
    }

    @Override
    public void deleteAllDocuments() {
        log.debug("noop deleteAllDocuments()");
    }

    @Override
    public void optimize() {
        log.debug("noop optimize()");
    }

    @Override
    public void deleteByDocType(String docType) {
        log.debug("noop deleteByDocType({})", docType);
    }
}
