package no.kantega.publishing.api.service.lock;

import java.util.Map;

public interface LockManager {

    ContentLock peekAtLock(int contentId);

    boolean lockContent(String owner, int contentId);

    void releaseLock(int contentId);

    void releaseLocksForOwner(final String owner);

    void cleanup();

    Map<Integer, ContentLock> getLocks();
}
