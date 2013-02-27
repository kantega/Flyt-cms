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
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class IndexableAttachmentProvider implements IndexableDocumentProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    @Autowired
    private AttachmentTransformer transformer;

    @Autowired
    private TaskExecutor executorService;

    private ProgressReporter progressReporter;

    public void provideDocuments(BlockingQueue<IndexableDocument> indexableDocumentQueue) {
        try {
            LinkedBlockingQueue<Integer> ids = new LinkedBlockingQueue<>();
            executorService.execute(new IDProducer(dataSource, ids));

            progressReporter.setStarted();
            while (!progressReporter.isFinished()){
                try {
                    Integer id = ids.poll(10, TimeUnit.SECONDS);

                    if (id != null) {
                        Attachment attachment = AttachmentAO.getAttachment(id);
                        if (attachment != null) {
                            IndexableDocument indexableDocument = transformer.transform(attachment);
                            indexableDocumentQueue.put(indexableDocument);
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("Interrupted!", e);
                } finally {
                    progressReporter.reportProgress();
                }
            }
        } finally {
            progressReporter = null;
        }
    }

    private class IDProducer implements Runnable {
        private final DataSource dataSource;
        private final LinkedBlockingQueue<Integer> ids;

        private IDProducer(DataSource dataSource, LinkedBlockingQueue<Integer> ids) {
            this.dataSource = dataSource;
            this.ids = ids;
        }

        public void run() {
            try (Connection connection = dataSource.getConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT attachments.Id FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    ids.put(resultSet.getInt("Id"));
                }
            } catch (Exception e) {
                log.error("Error getting IDs", e);
            }
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
        return jdbcTemplate.queryForInt("SELECT count(attachments.Id) FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
    }
}