package no.kantega.openaksess.search.provider.transformer;

import no.kantega.commons.log.Log;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
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

import static no.kantega.openaksess.search.provider.transformer.LocationUtil.locationWithoutTrailingSlash;
import static no.kantega.publishing.api.content.Language.getLanguageAsISOCode;

@Component
public class AttachmentTransformer implements DocumentTransformer<Attachment> {
    public static final String HANDLED_DOCUMENT_TYPE = "aksess-attachment";

    public IndexableDocument transform(Attachment attachment) {
        IndexableDocument indexableDocument = new IndexableDocument(generateUniqueID(attachment));

        ContentIdentifier contentIdentifier =  ContentIdentifier.fromContentId(attachment.getContentId());
        Content content = ContentAO.getContent(contentIdentifier, true);

        if (content != null && content.isSearchable()) {
            indexableDocument.setLanguage(getLanguageAsISOCode(content.getLanguage()));
            indexableDocument.setSecurityId(content.getSecurityId());
            indexableDocument.setContentType(HANDLED_DOCUMENT_TYPE);
            indexableDocument.setId(String.valueOf(attachment.getId()));

            indexableDocument.setShouldIndex(true);
            indexableDocument.setTitle(attachment.getFilename());
            indexableDocument.setContentStatus(ContentStatus.getContentStatusAsString(ContentStatus.PUBLISHED));
            indexableDocument.setVisibility(ContentVisibilityStatus.getName(ContentVisibilityStatus.ACTIVE));
            indexableDocument.addAttribute("publishDate", attachment.getLastModified());
            indexableDocument.addAttribute("url", attachment.getUrl());

            Association association = content.getAssociation();
            int siteId = association.getSiteId();
            indexableDocument.setParentId(association.getAssociationId());
            indexableDocument.setSiteId(siteId);
            indexableDocument.addAttribute("location",
                    String.format("%s/%s", locationWithoutTrailingSlash(association), association.getId()));

            OutputStream fileStream = null;
            try {
                File attachmentFile = File.createTempFile(attachment.getFilename(), "indexer");
                AttachmentAO.streamAttachmentData(attachment.getId(), new InputStreamHandler(new FileOutputStream(attachmentFile)));
                indexableDocument.setFileContent(attachmentFile);
            } catch (IOException e) {
                Log.error(getClass().getSimpleName(), e);
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
