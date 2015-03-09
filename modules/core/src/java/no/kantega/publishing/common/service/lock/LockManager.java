/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.service.lock;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import no.kantega.publishing.api.service.lock.ContentLock;
import no.kantega.publishing.common.Aksess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LockManager implements no.kantega.publishing.api.service.lock.LockManager {
    private static final Logger log = LoggerFactory.getLogger(LockManager.class);

    private static Map<Integer, ContentLock> locks = new ConcurrentHashMap<>();
    private static int lockTimeToLive = Aksess.getLockTimeToLive()* 1000;

    /**
     * @param contentId to get lock for.
     * @return the ContentLock for the Content with contentId if it exists. Null if not locked or lock is expired.
     */
    public ContentLock peekAtLock(int contentId) {
        log.info( "Peeking at lock for content " + contentId);
        ContentLock lock = locks.get(contentId);
        // No lock
        if(lock == null) {
            return null;
        } else {
            // Check if lock is expired
            long age = new Date().getTime() - lock.getCreateTime().getTime();

            boolean expired = age > lockTimeToLive;

            if(expired) {
                log.info( "Expired lock for content " + contentId);
                // Remove lock and return false (not locked anymore)
                locks.remove(contentId);
                return null;
            } else {
                // Yes, the content is locked
                return lock;
            }
        }
    }

    /**
     * @param owner - User which is locking the Content
     * @param contentId of the Content to lock.
     * @return true if a new lock is created. false if a lock for the Content with contentId already exists.
     */
    public  boolean lockContent(String owner, int contentId) {
        log.info( "Locking content " + contentId + " for owner " + owner);
        ContentLock contentLock = peekAtLock(contentId);
        if(contentLock != null) {
            // Content already locked
            return false;
        } else {
            // Kan kun låse et objekt i gangen
            releaseLocksForOwner(owner);

            ContentLock lock = new ContentLock(owner, contentId);
            locks.put(contentId, lock);
            return true;
        }
    }

    /**
     * Release lock on Content with the given contentId
     * @param contentId of the Content to release locks for
     */
    public void releaseLock(int contentId) {
        log.info( "Releasing lock for content " + contentId);
        locks.remove(contentId);
    }

    /**
     * Release locks owned by owner
     * @param owner - user owning the locks.
     */
    public void releaseLocksForOwner(final String owner) {
        log.info( "Releasing lock for owner " + owner);
        Map<Integer, ContentLock> locks = getLocks();
        Set<Map.Entry<Integer,ContentLock>> lockEntries = locks.entrySet();
        Collection<Map.Entry<Integer, ContentLock>> contentLocksWithOwner = Collections2.filter(lockEntries, new Predicate<Map.Entry<Integer, ContentLock>>() {
            @Override
            public boolean apply(Map.Entry<Integer, ContentLock> lockEntry) {
                return lockEntry.getValue().getOwner().equals(owner);
            }
        });
        Collection<Integer> lockIdsForOwner = Collections2.transform(contentLocksWithOwner, new Function<Map.Entry<Integer, ContentLock>, Integer>() {
            @Override
            public Integer apply(Map.Entry<Integer, ContentLock> lockEntry) {
                return lockEntry.getKey();
            }
        });
        for (Integer lockId : lockIdsForOwner) {
            locks.remove(lockId);
        }
    }

    /**
     * Go though all locks and remove expired locks
     */
    public void cleanup() {
        log.info( "Cleaning locks");
        for (Integer id : getLocks().keySet()) {
            peekAtLock(id);
        }
    }

    public Map<Integer, ContentLock> getLocks() {
        return locks;
    }
}
