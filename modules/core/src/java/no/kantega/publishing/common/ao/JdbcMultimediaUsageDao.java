package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.multimedia.MultimediaUsageDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

public class JdbcMultimediaUsageDao extends JdbcDaoSupport implements MultimediaUsageDao {

    public void removeUsageForContentId(int contentId) {
        // Get Multimedia ids belonging to the Content object
        List<Integer> multimediaIds = getUsagesForContentId(contentId);

        JdbcTemplate template = getJdbcTemplate();
        template.update("delete from multimediausage where ContentId = ?", contentId);

        // Update usage count ("noUsages") on Multimedia objects belonging to the Content object
        for (int multimediaId : multimediaIds) {
            int noUsages = template.queryForObject("SELECT COUNT(*) FROM multimediausage WHERE MultimediaId = ?", Integer.class, multimediaId);
            template.update("UPDATE multimedia SET NoUsages = ? WHERE Id = ?", noUsages, multimediaId);
        }
    }

    public void removeMultimediaId(int multimediaId) {
        getJdbcTemplate().update("delete from multimediausage where MultimediaId = ?", multimediaId);
    }

    public List<Integer> getUsagesForMultimediaId(int multimediaId) {
        return getJdbcTemplate().queryForList("select ContentId from multimediausage where MultimediaId = ?", new Object[] {multimediaId}, Integer.class);
    }

    public List<Integer> getUsagesForContentId(int contentId) {
        return getJdbcTemplate().queryForList("select MultimediaId from multimediausage where ContentId = ?", new Object[] {contentId}, Integer.class);
    }

    public void addUsageForContentId(int contentId, int multimediaId) {
        JdbcTemplate template = getJdbcTemplate();
        if (template.queryForObject("select count(*) from multimediausage where ContentId = ? and MultimediaId = ?", Integer.class, contentId, multimediaId) == 0) {
            template.update("insert into multimediausage values (?,?)", contentId, multimediaId);
        }

        // Update usage count ("noUsages") on the Multimedia object itself
        int noUsages = template.queryForObject("SELECT COUNT(*) FROM multimediausage WHERE MultimediaId = ?", Integer.class, multimediaId);
        template.update("UPDATE multimedia SET NoUsages = ? WHERE Id = ?", noUsages, multimediaId);
    }
}
