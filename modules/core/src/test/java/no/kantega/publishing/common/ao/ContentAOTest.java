package no.kantega.publishing.common.ao;

import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.topicmaps.data.Topic;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({dbConnectionFactory.class, TopicAO.class})
@PowerMockIgnore( {"javax.management.*"})
public class ContentAOTest {

    private String driverClass    = "org.apache.derby.jdbc.EmbeddedDriver";
    private String jdbcConnection = "jdbc:derby:memory:myDb;create=true";
    private DataSource dataSource;
    private SimpleJdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        dataSource = getDataSource();
        jdbcTemplate = new SimpleJdbcTemplate(dataSource);

        SimpleJdbcTestUtils.executeSqlScript(jdbcTemplate, new ClassPathResource("/contentao/ddl.sql"), true);
        mockStatic(dbConnectionFactory.class);
        mockStatic(TopicAO.class);
        
        when(dbConnectionFactory.getConnection()).thenAnswer(new Answer<Connection>() {
            public Connection answer(InvocationOnMock invocationOnMock) throws Throwable {
                return dataSource.getConnection();
            }
        });
        
        when(TopicAO.getTopicsByContentId(anyInt())).thenReturn(new ArrayList<Topic>());
    }

    @Test
    public void contentShouldNotBeWaitingForApprovalIfLatestVersionIsApproved() throws SQLException {
        SimpleJdbcTestUtils.executeSqlScript(jdbcTemplate, new ClassPathResource("/contentao/LatestVersionHasBeenPublished.sql"), false);
        
        List contentListForApproval = ContentAO.getContentListForApproval();
        assertTrue("Content for approval was not empty", contentListForApproval.isEmpty());
    }

    @Test
    public void contentShouldBeWaitingForApprovalIfNoVersionIsApproved() throws SQLException {
        SimpleJdbcTestUtils.executeSqlScript(jdbcTemplate, new ClassPathResource("/contentao/noVersionsHasBeenPublished.sql"), false);

        List contentListForApproval = ContentAO.getContentListForApproval();
        assertFalse("Content for approval was empty", contentListForApproval.isEmpty());
    }

    @After
    public void after(){
        SimpleJdbcTestUtils.deleteFromTables(jdbcTemplate, "associationcategory","associations","attachments","attribute_editablelist","content","contentattributes","contenttemplates","contentversion","ct2association","ct2parent","ct2topic","dbuserattributes","dbuserpassword","dbuserpasswordresettoken","dbuserprofile","dbuserrole","dbuserrole2user","deleteditems","displaytemplatecontroller","displaytemplates","documenttype","eventlog","form","formdelegation","formfilled","formfilledfile","formpdfimage","formsubmission","formsubmissionvalues","formversion","forum_attachment","forum_forum","forum_forum_groups","forum_forumcategory","forum_post","forum_thread","forum_thread_topics","link","linkoccurrence","mailsubscription","multimedia"," multimediaexifdata"," multimediaimagemap","multimediausage"," notes","oa_db_migrations","  objectpermissions"," poll","  ratings"," role2topic"," schedulelog"," searchlog","  site2hostname"," sites"," survey","  survey_participant","  survey_result"," tmassociation","  tmbasename"," tmmaps"," tmoccurence","  tmtopic"," trafficlog","transactionlocks"," uno_yrker"," urlstatus"," xmlcache");
    }
    private DataSource getDataSource() throws Exception {
        Properties props = new Properties();
        props.setProperty("driverClassName", driverClass);
        props.setProperty("url", jdbcConnection);

        return BasicDataSourceFactory.createDataSource(props);
    }
}
