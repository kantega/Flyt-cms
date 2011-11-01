package no.kantega.publishing.common.ao;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.util.List;

public class JdbcMultimediaUsageDao extends SimpleJdbcDaoSupport implements MultimediaUsageDao {
    public void removeUsageForContentId(int contentId) {
        getSimpleJdbcTemplate().update("delete from multimediausage where ContentId = ?", contentId);

        // Update usage count ("noUsages") on Multimedia objects belonging to the Content object
        SimpleJdbcTemplate template = getSimpleJdbcTemplate();
        template.update("UPDATE multimedia SET NoUsages = ( SELECT COUNT(*) FROM multimediausage WHERE multimediausage.MultimediaId = multimedia.Id )");
    }

    public void removeMultimediaId(int multimediaId) {
        getSimpleJdbcTemplate().update("delete from multimediausage where MultimediaId = ?", multimediaId);
    }

    public List<Integer> getUsagesForMultimediaId(int multimediaId) {
        return getJdbcTemplate().queryForList("select ContentId from multimediausage where MultimediaId = ?", new Object[] {multimediaId}, Integer.class);
    }

    public void addUsageForContentId(int contentId, int multimediaId) {
        SimpleJdbcTemplate template = getSimpleJdbcTemplate();
        if (template.queryForInt("select count(*) from multimediausage where ContentId = ? and MultimediaId = ?", contentId, multimediaId) == 0) {
            template.update("insert into multimediausage values (?,?)", contentId, multimediaId);
        }

        // Update usage count ("noUsages") on the Multimedia object itself
        int noUsages = template.queryForInt("SELECT COUNT(*) FROM multimediausage WHERE MultimediaId = ?", multimediaId);
        template.update("UPDATE multimedia SET NoUsages = ? WHERE Id = ?", noUsages, multimediaId);
    }
}
