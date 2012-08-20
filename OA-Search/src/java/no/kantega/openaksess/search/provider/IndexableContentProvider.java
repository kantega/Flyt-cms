package no.kantega.openaksess.search.provider;

import no.kantega.commons.log.Log;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
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
public class IndexableContentProvider implements IndexableDocumentProvider {

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    @Autowired
    private ContentTransformer transformer;

    public ProgressReporter provideDocuments(BlockingQueue<IndexableDocument> indexableDocuments, int numberOfThreads) {
        ContentManagementService contentManagementService = new ContentManagementService(SecuritySession.createNewAdminInstance());
        LinkedBlockingQueue<Integer> ids = new LinkedBlockingQueue<Integer>();
        new Thread(new IDProducer(dataSource, ids)).start();
        ProgressReporter progressReporter = new ProgressReporter(ContentTransformer.HANDLED_DOCUMENT_TYPE, getNumberOfDocuments());
        for (int i = 0; i < numberOfThreads; i++){
            new Thread(new ContentProducer(progressReporter, contentManagementService, ids, indexableDocuments)).start();
        }

        return progressReporter;
    }

    private long getNumberOfDocuments() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForInt("SELECT count(*) FROM content, associations WHERE content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
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
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT content.ContentId FROM content, associations WHERE content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    ids.put(resultSet.getInt("ContentId"));
                }
            } catch (Exception e) {
                Log.error(getClass().getName(), e);
            }
        }
    }

    private class ContentProducer implements Runnable {

        private final ProgressReporter progressReporter;
        private final ContentManagementService contentManagementService;
        private final LinkedBlockingQueue<Integer> ids;
        private final BlockingQueue<IndexableDocument> indexableDocuments;

        public ContentProducer(ProgressReporter progressReporter, ContentManagementService contentManagementService, LinkedBlockingQueue<Integer> ids, BlockingQueue<IndexableDocument> indexableDocuments) {
            this.progressReporter = progressReporter;
            this.contentManagementService = contentManagementService;
            this.ids = ids;
            this.indexableDocuments = indexableDocuments;
        }

        public void run() {
            while (!progressReporter.isFinished()){
                try {
                    Integer id = ids.poll(10, TimeUnit.SECONDS);
                    ContentIdentifier contentIdentifier = new ContentIdentifier();
                    contentIdentifier.setContentId(id);
                    progressReporter.reportProgress();
                    IndexableDocument indexableDocument = transformer.transform(contentManagementService.getContent(contentIdentifier));
                    indexableDocuments.put(indexableDocument);
                } catch (Exception e) {
                    Log.error(getClass().getName(), e);
                }

            }
        }
    }
}