package no.kantega.openaksess.search.index;

import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

@Component
public class IndexRebuilder {

    @Autowired
    private List<IndexableDocumentProvider> indexableDocumentProviders;

    @Autowired
    private DocumentIndexer documentIndexer;

    @PostConstruct
    public void doReindex(){
        for (IndexableDocumentProvider indexableDocumentProvider : indexableDocumentProviders) {
            Iterator<IndexableDocument> indexableDocumentIterator = indexableDocumentProvider.provideDocuments();
            while (indexableDocumentIterator.hasNext()){
                IndexableDocument next = indexableDocumentIterator.next();
                if (next.shouldIndex()) {
                    documentIndexer.indexDocument(next);
                }
            }
        }
        documentIndexer.commit();
    }
}
