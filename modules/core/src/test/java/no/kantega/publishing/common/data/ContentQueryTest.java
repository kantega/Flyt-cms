package no.kantega.publishing.common.data;

import no.kantega.publishing.common.data.enums.AssociationType;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ContentQueryTest {

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
