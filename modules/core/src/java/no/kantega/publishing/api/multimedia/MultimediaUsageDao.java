package no.kantega.publishing.api.multimedia;

import java.util.List;

public interface MultimediaUsageDao {
    public void removeUsageForContentId(int contentId);

    public void removeMultimediaId(int multimediaId);

    public List<Integer> getUsagesForMultimediaId(int multimediaId);

    public void addUsageForContentId(int contentId, int multimediaId);

}
