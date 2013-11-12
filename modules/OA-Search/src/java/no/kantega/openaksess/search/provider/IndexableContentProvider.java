package no.kantega.openaksess.search.provider;

import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
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
public class IndexableContentProvider implements IndexableDocumentProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    @Autowired
    private ContentTransformer transformer;

    @Autowired
    private TaskExecutor executorService;

    private ProgressReporter progressReporter;

    public void provideDocuments(BlockingQueue<IndexableDocument> indexableDocuments) {
        try {
            ContentManagementService contentManagementService = new ContentManagementService(SecuritySession.createNewAdminInstance());
            LinkedBlockingQueue<Integer> ids = new LinkedBlockingQueue<>(100);
            executorService.execute(new IDProducer(dataSource, ids));
            progressReporter.setStarted();
            while (!progressReporter.isFinished()){
                try {
                    Integer id = ids.poll(10L, TimeUnit.SECONDS);
                    if (id != null) {
                        ContentIdentifier contentIdentifier =  ContentIdentifier.fromAssociationId(id);

                        Content content = contentManagementService.getContent(contentIdentifier);
                        if (content != null) {
                            IndexableDocument indexableDocument = transformer.transform(content);
                            log.debug("Transformed Content {} {}", content.getTitle(), content.getId());
                            indexableDocuments.put(indexableDocument);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error transforming Content", e);
                } finally {
                    progressReporter.reportProgress();
                }

            }
        } finally {
            progressReporter = null;
        }

    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public ProgressReporter getProgressReporter() {
        if(progressReporter == null){
            progressReporter = new ProgressReporter(ContentTransformer.HANDLED_DOCUMENT_TYPE, getNumberOfDocuments());
        }
        return  progressReporter;
    }

    private long getNumberOfDocuments() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForObject("SELECT count(*) FROM content, associations WHERE content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0", Long.class);
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
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT associations.associationId FROM content, associations WHERE content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    ids.put(resultSet.getInt("associationId"));
                }
            } catch (Exception e) {
                log.error("Error getting IDs", e);
            }
        }
    }
}
