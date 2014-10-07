package no.kantega.openaksess.search.provider;

import no.kantega.openaksess.search.provider.transformer.MultimediaTransformer;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
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
public class IndexableMultimediaProvider implements IndexableDocumentProvider {

    private static final String FROM_CLAUSE = " FROM multimedia";

    private ProgressReporter progressReporter;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private MultimediaTransformer transformer;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public ProgressReporter getProgressReporter() {
        if (progressReporter == null) {
            progressReporter = new ProgressReporter(MultimediaTransformer.HANDLED_DOCUMENT_TYPE, getNumberOfDocuments());
        }
        return progressReporter;
    }

    @Override
    public void provideDocuments(BlockingQueue<IndexableDocument> indexableDocumentQueue) {
        try {
            MultimediaService multimediaService = new MultimediaService(SecuritySession.createNewAdminInstance());
            LinkedBlockingQueue<Integer> ids = new LinkedBlockingQueue<>(100);
            taskExecutor.execute(new IDProducer(dataSource, ids));
            progressReporter.setStarted();
            while (!progressReporter.isFinished()) {
                try {
                    Integer id = ids.poll(10L, TimeUnit.SECONDS);
                    if (id != null) {
                        Multimedia multimedia = multimediaService.getMultimedia(id);
                        if (multimedia != null) {
                            IndexableDocument indexableDocument = transformer.transform(multimedia);
                            log.debug("Transformed multimedia {} {}", multimedia.getName(), multimedia.getId());
                            indexableDocumentQueue.put(indexableDocument);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error transforming multimedia", e);
                } finally {
                    progressReporter.reportProgress();
                }
            }
        } finally {
            progressReporter = null;
        }
    }

    private long getNumberOfDocuments() {
        String sql = "SELECT count(*) " + FROM_CLAUSE;

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    private class IDProducer implements Runnable {
        private final DataSource dataSource;
        private final LinkedBlockingQueue<Integer> ids;

        private IDProducer(DataSource dataSource, LinkedBlockingQueue<Integer> ids) {
            this.dataSource = dataSource;
            this.ids = ids;
        }

        @Override
        public void run() {
            String sql = "SELECT m.id " + FROM_CLAUSE;
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    ids.put(resultSet.getInt("id"));
                }
            } catch (Exception e) {
                log.error("Error getting IDs", e);
            }
        }
    }
}
