package no.kantega.openaksess.search.attachment.index;

import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

@Component
public class IndexableAttachmentProvider implements IndexableDocumentProvider {

    private static final String HANDLED_DOCUMENT_TYPE = "aksess-attachment";
    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    public Iterator<IndexableDocument> provideDocuments() {
        return new IndexableAttachemntIterator(dataSource);
    }

    private IndexableDocument getIndexableDocument(Attachment attachment){
        IndexableDocument indexableDocument = new IndexableDocument();
        indexableDocument.setContentType(HANDLED_DOCUMENT_TYPE);
        indexableDocument.setId(String.valueOf(attachment.getId()));
        indexableDocument.setTitle(attachment.getFilename());
        indexableDocument.setContentStatus(ContentStatus.getContentStatusAsString(ContentStatus.PUBLISHED));
        indexableDocument.setVisibility(ContentVisibilityStatus.getName(ContentVisibilityStatus.ACTIVE));
        indexableDocument.addAttribute("publishDate", attachment.getLastModified());

        ContentIdentifier contentIdentifier = new ContentIdentifier();
        contentIdentifier.setContentId(attachment.getContentId());
        Content content = ContentAO.getContent(contentIdentifier, true);
        indexableDocument.addAttribute("location", content.getAssociation().getPath() + "/" + content.getAssociation().getId());
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

        return indexableDocument;
    }

    private class IndexableAttachemntIterator implements Iterator<IndexableDocument> {
        private final ResultSet resultSet;

        public IndexableAttachemntIterator(DataSource dataSource) {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT attachments.Id FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0 ORDER BY attachments.id");
                resultSet = preparedStatement.executeQuery();
            } catch (SQLException e) {
                throw new IllegalStateException("Could not connect to database", e);
            }
        }

        public boolean hasNext() {
            try {
                return resultSet.next();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        public IndexableDocument next() {
            try {
                return getIndexableDocument(AttachmentAO.getAttachment(resultSet.getInt("Id")));
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported");
        }
    }
}
