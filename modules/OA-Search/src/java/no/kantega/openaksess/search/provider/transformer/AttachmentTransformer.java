package no.kantega.openaksess.search.provider.transformer;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.DocumentTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static no.kantega.openaksess.search.provider.transformer.LocationUtil.locationWithoutTrailingSlash;
import static no.kantega.publishing.api.content.Language.getLanguageAsISOCode;

@Component
public class AttachmentTransformer implements DocumentTransformer<Attachment> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    public static final String HANDLED_DOCUMENT_TYPE = "aksess-attachment";

    @Autowired
    private ContentAO contentAO;

    public IndexableDocument transform(Attachment attachment) {
        IndexableDocument indexableDocument = new IndexableDocument(generateUniqueID(attachment));

        ContentIdentifier contentIdentifier = ContentIdentifier.fromContentId(attachment.getContentId());
        Content content = contentAO.getContent(contentIdentifier, true);

        if (content != null && content.isSearchable()) {
            indexableDocument.setLanguage(getLanguageAsISOCode(content.getLanguage()));
            indexableDocument.setSecurityId(content.getSecurityId());
            indexableDocument.setContentType(HANDLED_DOCUMENT_TYPE);
            indexableDocument.setId(String.valueOf(attachment.getId()));

            indexableDocument.setShouldIndex(true);
            indexableDocument.setTitle(attachment.getFilename());
            indexableDocument.setContentStatus(ContentStatus.PUBLISHED.name());
            indexableDocument.setVisibility(ContentVisibilityStatus.getName(ContentVisibilityStatus.ACTIVE));
            indexableDocument.addAttribute("publishDate", attachment.getLastModified());
            indexableDocument.addAttribute("url", attachment.getUrl());

            Association association = content.getAssociation();
            int siteId = association.getSiteId();
            indexableDocument.setParentId(association.getAssociationId());
            indexableDocument.setSiteId(siteId);
            indexableDocument.addAttribute("location", locationWithoutTrailingSlash(association) + "/" + association.getId());
            indexableDocument.addAttribute("location_depth", association.getDepth() + 1);

            try {
                File attachmentFile = File.createTempFile(attachment.getFilename(), "indexer");
                try (FileOutputStream out = new FileOutputStream(attachmentFile)){
                    AttachmentAO.streamAttachmentData(attachment.getId(), new InputStreamHandler(out));
                    indexableDocument.setFileContent(attachmentFile);
                }
            } catch (IOException e) {
                log.error("Error streaming file", e);
            }
        }

        return indexableDocument;
    }

    public String generateUniqueID(Attachment document) {
        return HANDLED_DOCUMENT_TYPE + "-" + document.getId();
    }
}
