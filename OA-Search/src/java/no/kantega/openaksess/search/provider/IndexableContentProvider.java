package no.kantega.openaksess.search.provider;

import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

@Component
public class IndexableContentProvider implements IndexableDocumentProvider {

    @Autowired
    private ContentManagementService contentManagementService;

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    @Autowired
    private ContentTransformer transformer;

    public Iterator<IndexableDocument> provideDocuments() {

        return new IndexableContentDocumentIterator(dataSource, contentManagementService);
    }

    private class IndexableContentDocumentIterator implements Iterator<IndexableDocument>{
        private final ResultSet resultSet;
        private final ContentManagementService cms;

        private IndexableContentDocumentIterator(DataSource dataSource, ContentManagementService cms) {
            this.cms = cms;
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT ContentId FROM content");
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
                ContentIdentifier contentIdentifier = new ContentIdentifier();
                contentIdentifier.setContentId(resultSet.getInt("ContentId"));
                return transformer.transform(cms.getContent(contentIdentifier));
            } catch (Exception e) {
              throw new IllegalStateException(e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported");
        }
    }
}
