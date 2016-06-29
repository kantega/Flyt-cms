package no.kantega.openaksess.search.provider.transformer;

import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.common.ao.MultimediaDao;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.impl.PathWorker;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.IndexableDocumentCustomizer;
import no.kantega.search.api.provider.DocumentTransformerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import static java.util.Collections.emptyList;

@Component
public class MultimediaTransformer extends DocumentTransformerAdapter<Multimedia> {
    public static final String HANDLED_DOCUMENT_TYPE = "aksess-multimedia";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MultimediaDao multimediaDao;

    protected MultimediaTransformer() {
        super(Multimedia.class);
    }

    private ExecutorService executor;

    @PostConstruct
    public void init(){
        executor = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    public void shutdown(){
        executor.shutdown();
    }

    @Override
    public IndexableDocument transform(Multimedia document) {
        IndexableDocument indexableDocument = new IndexableDocument(generateUniqueID(document));
        indexableDocument.setShouldIndex(true);
        indexableDocument.setId(String.valueOf(document.getId()));
        indexableDocument.setContentType(HANDLED_DOCUMENT_TYPE);
        indexableDocument.setTitle(document.getName());
        indexableDocument.setDescription(document.getDescription());
        indexableDocument.setParentId(document.getParentId());
        indexableDocument.setSecurityId(document.getSecurityId());

        indexableDocument.setLanguage(Language.getLanguageAsISOCode(Language.NORWEGIAN_BO));

        indexableDocument.addAttribute("altname", document.getAltname());
        indexableDocument.addAttribute("author", document.getAuthor());
        indexableDocument.addAttribute("publishDate", document.getLastModified());
        indexableDocument.addAttribute("filename_str", document.getFilename());
        indexableDocument.addAttribute("filesize_i", document.getSize());
        indexableDocument.addAttribute("filetype_str", document.getMimeType().getType());

        indexableDocument.addAttribute("url", document.getUrl());

        List<PathEntry> path = getMultimediaPath(document);
        indexableDocument.addAttribute("location", getPathString(path));
        indexableDocument.addAttribute("location_depth", path.size());

        try {
            File attachmentFile = File.createTempFile(document.getFilename(), "indexer");
            try (FileOutputStream out = new FileOutputStream(attachmentFile)){
                multimediaDao.streamMultimediaData(document.getId(), new InputStreamHandler(out));
                indexableDocument.setFileContent(attachmentFile);
            }
        } catch (IOException e) {
            log.error("Error streaming file", e);
        }

        for (IndexableDocumentCustomizer<Multimedia> customizer : getIndexableDocumentCustomizers()) {
            indexableDocument = customizer.customizeIndexableDocument(document, indexableDocument);
        }
        return indexableDocument;
    }

    private String getPathString(List<PathEntry> path) {
        StringBuilder pathBuilder = new StringBuilder();
        for (PathEntry entry : path) {
            pathBuilder.append('/');
            pathBuilder.append(entry.getId());
        }
        return pathBuilder.toString();
    }

    private List<PathEntry> getMultimediaPath(final Multimedia document) {
        // Some times MultimediaTransformer.getMultimediaPath(...) hangs with stacktrace ending with java.net.SocketInputStream.socketRead0(Native Method)
        try {
            Future<List<PathEntry>> listFuture = executor.submit(new Callable<List<PathEntry>>() {
                @Override
                public List<PathEntry> call() throws Exception {
                    return PathWorker.getMultimediaPath(document);
                }
            });
            return listFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Timedout getting path for multimedia {}", document.getId());
            return emptyList();
        }
    }

    @Override
    public String generateUniqueID(Multimedia document) {
        return HANDLED_DOCUMENT_TYPE + "-" + document.getId();
    }
}
