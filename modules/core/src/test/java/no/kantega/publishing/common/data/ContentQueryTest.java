package no.kantega.publishing.common.data;

import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.spring.RootContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentQueryTest {

    @Before
    public void setup(){
        ApplicationContext mock = mock(ApplicationContext.class);
        RootContext.setInstance(mock);
        when(mock.getBean(ContentIdHelper.class)).thenReturn(mock(ContentIdHelper.class));
    }

    @Test
    public void emptyContentQueryShouldNotIncludeCrosspostingAndShortcuts(){
        ContentQuery cq = new ContentQuery();
        ContentQuery.QueryWithParameters queryWithParameters = cq.getQueryWithParameters();
        assertEquals("Query was wrong", "select content.*, contentversion.*, associations.* from content,contentversion,associations where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0) and contentversion.Status IN (:status) and content.ContentId = associations.ContentId and associations.Type not in (:excludedAssociationTypes) and content.VisibilityStatus in (:VisibilityStatus) order by ContentVersionId", queryWithParameters.getQuery().trim());
        assertEquals("Wrong number of parameters", 3, queryWithParameters.getParams().size());
    }

    @Test
    public void queryWithContentListShouldNotContainShortcuts(){
        ContentQuery cq = new ContentQuery();
        cq.setContentList("1,2,3");
        ContentQuery.QueryWithParameters queryWithParameters = cq.getQueryWithParameters();
        assertEquals("Query was wrong", "select content.*, contentversion.*, associations.* from content,contentversion,associations where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0) and contentversion.Status IN (:status) and content.ContentId = associations.ContentId and associations.UniqueId in (:contentlist) and associations.Type not in (:excludedAssociationTypes) and content.VisibilityStatus in (:VisibilityStatus) order by ContentVersionId", queryWithParameters.getQuery().trim());
        assertEquals("Wrong number of parameters", 4, queryWithParameters.getParams().size());
    }

    @Test
    public void queryWithContentListAndExcludeCrossPostingShouldContainShortcutsAndDefaultPosting(){
        ContentQuery cq = new ContentQuery();
        cq.setContentList("1,2,3");
        cq.setExcludedAssociationTypes(asList(AssociationType.CROSS_POSTING));
        ContentQuery.QueryWithParameters queryWithParameters = cq.getQueryWithParameters();
        assertEquals("Query was wrong", "select content.*, contentversion.*, associations.* from content,contentversion,associations where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0) and contentversion.Status IN (:status) and content.ContentId = associations.ContentId and associations.UniqueId in (:contentlist) and associations.Type not in (:excludedAssociationTypes) and content.VisibilityStatus in (:VisibilityStatus) order by ContentVersionId", queryWithParameters.getQuery().trim());
        assertEquals("Wrong number of parameters", 4, queryWithParameters.getParams().size());
    }

}
