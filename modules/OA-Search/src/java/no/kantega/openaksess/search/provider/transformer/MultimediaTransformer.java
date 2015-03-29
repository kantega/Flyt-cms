package no.kantega.openaksess.search.provider.transformer;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class MultimediaTransformer extends DocumentTransformerAdapter<Multimedia> {
    public static final String HANDLED_DOCUMENT_TYPE = "aksess-multimedia";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MultimediaDao multimediaDao;

    protected MultimediaTransformer() {
        super(Multimedia.class);
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
        indexableDocument.setLanguage("nno");
        indexableDocument.addAttribute("url", document.getUrl());

        indexableDocument.addAttribute("altTitle_no", document.getAltname());
        indexableDocument.addAttribute("author", document.getAuthor());
        indexableDocument.addAttribute("publishDate", document.getLastModified());
        indexableDocument.addAttribute("filename_str", document.getFilename());
        indexableDocument.addAttribute("filesize_i", document.getSize());
        indexableDocument.addAttribute("filetype_str", document.getMimeType().getType());

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

    private List<PathEntry> getMultimediaPath(Multimedia document) {
        return PathWorker.getMultimediaPath(document);
    }

    @Override
    public String generateUniqueID(Multimedia document) {
        return HANDLED_DOCUMENT_TYPE + "-" + document.getId();
    }
}
