package no.kantega.openaksess.search.provider;

import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.ProgressReporter;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.BlockingQueue;

@Component
public class IndexableAttachmentProvider implements IndexableDocumentProvider {
    private final String FROM_CLAUSE = "FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0";
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    @Autowired
    private AttachmentTransformer transformer;

    private ProgressReporter progressReporter;

    public void provideDocuments(BlockingQueue<IndexableDocument> indexableDocumentQueue) {
        try {

            progressReporter.setStarted();
            while (!progressReporter.isFinished()){
                try {
                    provideAttachments(indexableDocumentQueue);

                } finally {
                    progressReporter.reportProgress();
                }
            }
        } finally {
            progressReporter = null;
        }
    }

    private void provideAttachments(BlockingQueue<IndexableDocument> indexableDocumentQueue) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT attachments.Id " + FROM_CLAUSE);
            ResultSet resultSet = preparedStatement.executeQuery()){
            while (resultSet.next()){
                int id = resultSet.getInt("Id");
                provideDocument(indexableDocumentQueue, id);
            }
        } catch (Exception e) {
            log.error("Error getting IDs", e);
        }
    }

    private void provideDocument(BlockingQueue<IndexableDocument> indexableDocumentQueue, int id) throws InterruptedException {
        try {
            Attachment attachment = AttachmentAO.getAttachment(id);
            if (attachment != null) {
                IndexableDocument indexableDocument = transformer.transform(attachment);
                log.info("Transformed Attachment {}", attachment.getFilename());
                indexableDocumentQueue.put(indexableDocument);
            } else {
                log.error("Attachment with id {} was null", id);
            }
        } finally {
            progressReporter.reportProgress();
        }
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public ProgressReporter getProgressReporter() {
        if(progressReporter == null){
            progressReporter = new ProgressReporter(AttachmentTransformer.HANDLED_DOCUMENT_TYPE, getNumberOfDocuments());
        }
        return progressReporter;
    }

    private long getNumberOfDocuments() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForObject("SELECT count( distinct attachments.Id ) " + FROM_CLAUSE, Integer.class);
    }
}