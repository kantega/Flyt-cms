package no.kantega.openaksess.search.index.update;

import no.kantega.openaksess.search.provider.transformer.MultimediaTransformer;
import no.kantega.publishing.api.multimedia.event.MultimediaEvent;
import no.kantega.publishing.api.multimedia.event.MultimediaEventListenerAdapter;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MultimediaIndexUpdater extends MultimediaEventListenerAdapter {

    @Autowired
    DocumentIndexer documentIndexer;

    @Autowired
    MultimediaTransformer transformer;

    @Override
    public void afterSetMultimedia(MultimediaEvent event) {
        IndexableDocument indexableDocument = transformer.transform(event.getMultimedia());
        documentIndexer.indexDocumentAndCommit(indexableDocument);
    }

    @Override
    public void afterDeleteMultimedia(MultimediaEvent event) {
        List<String> uids = Arrays.asList(transformer.generateUniqueID(event.getMultimedia()));
        documentIndexer.deleteByUid(uids);
    }
}
