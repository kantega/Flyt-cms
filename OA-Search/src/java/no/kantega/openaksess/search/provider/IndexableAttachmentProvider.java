package no.kantega.openaksess.search.provider;

import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

@Component
public class IndexableAttachmentProvider implements IndexableDocumentProvider {

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    @Autowired
    private AttachmentTransformer transformer;

    public Iterator<IndexableDocument> provideDocuments() {
        return new IndexableAttachemntIterator(dataSource);
    }

    public long getNumberOfDocuments() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForInt("SELECT count(attachments.Id) FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
    }

    private class IndexableAttachemntIterator implements Iterator<IndexableDocument> {
        private final ResultSet resultSet;

        public IndexableAttachemntIterator(DataSource dataSource) {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT attachments.Id FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.IsSearchable = 1 AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0");
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
                return transformer.transform(AttachmentAO.getAttachment(resultSet.getInt("Id")));
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported");
        }
    }
}
