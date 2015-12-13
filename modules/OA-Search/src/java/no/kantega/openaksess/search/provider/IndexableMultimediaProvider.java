package no.kantega.openaksess.search.provider;

import no.kantega.openaksess.search.provider.transformer.MultimediaTransformer;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
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
            //Progressreporters is set to finish to stop reindexing, this if test is needed to hinder creation of IDProducers after the canceled reindex
            if(progressReporter!=null && !progressReporter.isFinished()){
                log.info("creating IDProducer " + this.getClass());

                taskExecutor.execute(new IDProducer(dataSource, ids));
                while (!progressReporter.isFinished()) {
                    try {
                        log.debug("Polling ids, size: {}", ids.size());
                        Integer id = ids.poll(10L, TimeUnit.SECONDS);
                        log.debug("Got multimediaid {}", id);
                        if (id != null) {
                            Multimedia multimedia = multimediaService.getMultimedia(id);
                            if (multimedia != null && multimedia.getType() != MultimediaType.FOLDER) {
                                IndexableDocument indexableDocument = transformer.transform(multimedia);
                                log.debug("Transformed multimedia {} {}", multimedia.getName(), multimedia.getId());
                                indexableDocumentQueue.put(indexableDocument);
                            }
                        } else {
                            log.info("Multimedia poll returned null");
                        }
                    } catch (Exception e) {
                        log.error("Error transforming multimedia", e);
                    } finally {
                        progressReporter.reportProgress();
                    }
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
            log.info("starting IDProducer for " + this.getClass());
            String sql = "SELECT id " + FROM_CLAUSE;
            try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()  && (progressReporter != null) && !progressReporter.isFinished()) {
                    int id = resultSet.getInt("id");
                    log.trace("Got Id {}, queue size: {}", id, ids.size());
                    if(!ids.offer(id, 5, TimeUnit.SECONDS)){
                        log.info("Timed out offering id " + id);
                    }
                    log.trace("Put Id {}, queue size: {}", id, ids.size());
                }
            } catch (Exception e) {
                log.error("Error getting IDs", e);
            }
            log.info("stopped IDProducer for " + this.getClass());
        }
    }
}
