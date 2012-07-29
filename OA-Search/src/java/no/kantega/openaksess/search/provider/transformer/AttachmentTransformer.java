package no.kantega.openaksess.search.provider.transformer;

import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.DocumentTransformer;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class AttachmentTransformer implements DocumentTransformer<Attachment> {
    private static final String HANDLED_DOCUMENT_TYPE = "aksess-attachment";

    public IndexableDocument transform(Attachment attachment) {
        IndexableDocument indexableDocument = new IndexableDocument(generateUniqueID(attachment));

        ContentIdentifier contentIdentifier = new ContentIdentifier();
        contentIdentifier.setContentId(attachment.getContentId());
        Content content = ContentAO.getContent(contentIdentifier, true);

        if (content.isSearchable()) {
            indexableDocument.setContentType(HANDLED_DOCUMENT_TYPE);
            indexableDocument.setId(String.valueOf(attachment.getId()));
            indexableDocument.setShouldIndex(true);
            indexableDocument.setTitle(attachment.getFilename());
            indexableDocument.setContentStatus(ContentStatus.getContentStatusAsString(ContentStatus.PUBLISHED));
            indexableDocument.setVisibility(ContentVisibilityStatus.getName(ContentVisibilityStatus.ACTIVE));
            indexableDocument.addAttribute("publishDate", attachment.getLastModified());


            indexableDocument.addAttribute("location", content.getAssociation().getPath() + content.getAssociation().getId());
            indexableDocument.setSiteId(content.getAssociation().getSiteId());

            OutputStream fileStream = null;
            try {
                File attachmentFile = File.createTempFile(attachment.getFilename(), "indexer");
                AttachmentAO.streamAttachmentData(attachment.getId(), new InputStreamHandler(new FileOutputStream(attachmentFile)));
                indexableDocument.setFileContent(attachmentFile);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                IOUtils.closeQuietly(fileStream);
            }
        }

        return indexableDocument;
    }

    public String generateUniqueID(Attachment document) {
        return String.format("%s-%s", HANDLED_DOCUMENT_TYPE, document.getId());
    }
}
