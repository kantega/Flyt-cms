package no.kantega.openaksess.search.provider.transformer;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.DocumentTransformerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MultimediaTransformer extends DocumentTransformerAdapter<Multimedia> {
    public static final String HANDLED_DOCUMENT_TYPE = "aksess-multimedia";

    private final Logger log = LoggerFactory.getLogger(getClass());

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

        indexableDocument.addAttribute("altname", document.getAltname());
        indexableDocument.addAttribute("author", document.getAuthor());
        indexableDocument.addAttribute("date", document.getOriginalDate());
        indexableDocument.addAttribute("cameraMake", document.getCameraMake());
        indexableDocument.addAttribute("cameraModel", document.getCameraModel());
        indexableDocument.addAttribute("gpsLatitudeRef", document.getGpsLatitudeRef());
        indexableDocument.addAttribute("gpsLatitude", document.getGpsLatitude());
        indexableDocument.addAttribute("gpsLongitudeRef", document.getGpsLongitudeRef());
        indexableDocument.addAttribute("gpsLongitude", document.getGpsLongitude());

        return indexableDocument;
    }

    @Override
    public String generateUniqueID(Multimedia document) {
        return HANDLED_DOCUMENT_TYPE + "-" + document.getId();
    }
}
