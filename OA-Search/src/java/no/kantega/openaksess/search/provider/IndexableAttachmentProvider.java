package no.kantega.openaksess.search.provider;

import no.kantega.commons.log.Log;
import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.ProgressReporter;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    @Autowired
    private AttachmentTransformer transformer;

    public long getNumberOfDocuments() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForInt("SELECT count(attachments.Id) FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
    }

    public ProgressReporter provideDocuments(BlockingQueue<IndexableDocument> indexableDocumentQueue, int numberOfThreadsToUse) {
        LinkedBlockingQueue<Integer> ids = new LinkedBlockingQueue<Integer>();
        new Thread(new IDProducer(dataSource, ids)).start();
        ProgressReporter progressReporter = new ProgressReporter(AttachmentTransformer.HANDLED_DOCUMENT_TYPE, getNumberOfDocuments());

        for (int i = 0; i < numberOfThreadsToUse; i++){
            new Thread(new AttachmentProducer(progressReporter, ids, indexableDocumentQueue)).start();
        }

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
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT attachments.Id FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    ids.put(resultSet.getInt("Id"));
                }
            } catch (Exception e) {
                Log.error(getClass().getName(), e);
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
                    Integer id = ids.poll(1000, TimeUnit.SECONDS);

                    progressReporter.reportProgress();
                    IndexableDocument indexableDocument = transformer.transform(AttachmentAO.getAttachment(id));
                    indexableDocuments.put(indexableDocument);
                } catch (InterruptedException e) {
                    Log.error(getClass().getName(), e);
                }
            }
        }
    }
}