package no.kantega.openaksess.search.provider;

import no.kantega.commons.log.Log;
import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.ProgressReporter;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class IndexableAttachmentProvider implements IndexableDocumentProvider {

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    @Autowired
    private AttachmentTransformer transformer;

    @Autowired
    private TaskExecutor executorService;

    public long getNumberOfDocuments() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForInt("SELECT count(attachments.Id) FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
    }

    public ProgressReporter provideDocuments(BlockingQueue<IndexableDocument> indexableDocumentQueue) {
        LinkedBlockingQueue<Integer> ids = new LinkedBlockingQueue<>();
        executorService.execute(new IDProducer(dataSource, ids));
        ProgressReporter progressReporter = new ProgressReporter(AttachmentTransformer.HANDLED_DOCUMENT_TYPE, getNumberOfDocuments());

        executorService.execute(new AttachmentProducer(progressReporter, ids, indexableDocumentQueue));

        return progressReporter;
    }

    private class IDProducer implements Runnable {
        private final DataSource dataSource;
        private final LinkedBlockingQueue<Integer> ids;

        private IDProducer(DataSource dataSource, LinkedBlockingQueue<Integer> ids) {
            this.dataSource = dataSource;
            this.ids = ids;
        }

        public void run() {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT attachments.Id FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    ids.put(resultSet.getInt("Id"));
                }
            } catch (Exception e) {
                Log.error(getClass().getName(), e);
            } finally {
                if(connection != null){
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.error(getClass().getName(), e);
                    }
                }
            }
        }
    }

    private class AttachmentProducer implements Runnable {

        private final ProgressReporter progressReporter;
        private final LinkedBlockingQueue<Integer> ids;
        private final BlockingQueue<IndexableDocument> indexableDocuments;

        public AttachmentProducer(ProgressReporter progressReporter, LinkedBlockingQueue<Integer> ids, BlockingQueue<IndexableDocument> indexableDocuments) {
            this.progressReporter = progressReporter;
            this.ids = ids;
            this.indexableDocuments = indexableDocuments;
        }

        public void run() {
            while (!progressReporter.isFinished()){
                try {
                    Integer id = ids.poll(10, TimeUnit.SECONDS);

                    if (id != null) {
                        Attachment attachment = AttachmentAO.getAttachment(id);
                        if (attachment != null) {
                            IndexableDocument indexableDocument = transformer.transform(attachment);
                            indexableDocuments.put(indexableDocument);
                            waitSome();
                        }
                        progressReporter.reportProgress();
                    }
                } catch (InterruptedException e) {
                    Log.error(getClass().getName(), e);
                }
            }
        }

        /*
         * // TODO remove hack.
         * Most of the processing of attachments are done in the indexer, so to not overload the machine.
         */
        private void waitSome() throws InterruptedException {
            Thread.sleep(250);
        }
    }


    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}