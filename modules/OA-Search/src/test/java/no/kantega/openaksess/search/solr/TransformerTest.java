package no.kantega.openaksess.search.solr;

import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.search.api.IndexableDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-solrSearch-test.xml"})
public class TransformerTest {

    @Autowired
    private ContentTransformer contentTransformer;

    @Autowired
    private AttachmentTransformer attachmentTransformer;

    @Autowired
    private ContentAO contentAO;
    private Content content;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException, SQLException {
        content = new Content();
        content.setSearchable(true);
        content.setAssociations(Collections.singletonList(new Association()));
        when(contentAO.getContent(any(ContentIdentifier.class), anyBoolean())).thenReturn(content);

        Field ds = dbConnectionFactory.class.getDeclaredField("ds");
        ds.setAccessible(true);
        DataSource mock = mock(DataSource.class);
        ds.set(null, mock);

        Connection connection = mock(Connection.class);
        when(mock.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenReturn(mock(ResultSet.class));
    }

    @Test
    public void contentTransformerShouldUseCustomizer(){
        IndexableDocument document = contentTransformer.transform(content);
        assertEquals("Thar be cuztomize!", document.getAttributes().get("ContentAttribute"));
    }

    @Test
    public void attachmentTransformerShouldUseCustomizer(){
        Attachment attachment = new Attachment();
        attachment.setFilename("Attachment");
        IndexableDocument document = attachmentTransformer.transform(attachment);
        assertEquals("wow. such customize. much attachment.", document.getAttributes().get("AttachmentAttribute"));
    }
}
