package no.kantega.publishing.common.data;

import no.kantega.publishing.common.data.enums.AssociationType;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ContentQueryTest {

    @Test
    public void emptyContentQueryShouldNotIncludeCrosspostingAndShortcuts(){
        ContentQuery cq = new ContentQuery();
        ContentQuery.QueryWithParameters queryWithParameters = cq.getQueryWithParameters();
        assertEquals("Query was wrong", "select content.*, contentversion.*, associations.* from content,contentversion,associations where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0) and contentversion.Status IN (30) and content.ContentId = associations.ContentId and associations.Type not in (2,0) and content.VisibilityStatus in (10) order by ContentVersionId", queryWithParameters.getQuery().trim());
        assertEquals("Wrong number of parameters", 0, queryWithParameters.getParams().size());
    }

    @Test
    public void queryWithContentListShouldNotContainShortcuts(){
        ContentQuery cq = new ContentQuery();
        cq.setContentList("1,2,3");
        ContentQuery.QueryWithParameters queryWithParameters = cq.getQueryWithParameters();
        assertEquals("Query was wrong", "select content.*, contentversion.*, associations.* from content,contentversion,associations where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0) and contentversion.Status IN (30) and content.ContentId = associations.ContentId and associations.UniqueId in (?,?,?) and associations.Type not in (0) and content.VisibilityStatus in (10) order by ContentVersionId", queryWithParameters.getQuery().trim());
        assertEquals("Wrong number of parameters", 3, queryWithParameters.getParams().size());
    }

    @Test
    public void queryWithContentListAndExcludeCrossPostingShouldContainShortcutsAndDefaultPosting(){
        ContentQuery cq = new ContentQuery();
        cq.setContentList("1,2,3");
        cq.setExcludedAssociationTypes(new int[]{AssociationType.CROSS_POSTING});
        ContentQuery.QueryWithParameters queryWithParameters = cq.getQueryWithParameters();
        assertEquals("Query was wrong", "select content.*, contentversion.*, associations.* from content,contentversion,associations where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0) and contentversion.Status IN (30) and content.ContentId = associations.ContentId and associations.UniqueId in (?,?,?) and associations.Type not in (2) and content.VisibilityStatus in (10) order by ContentVersionId", queryWithParameters.getQuery().trim());
        assertEquals("Wrong number of parameters", 3, queryWithParameters.getParams().size());
    }

}
