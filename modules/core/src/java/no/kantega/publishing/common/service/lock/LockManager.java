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
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LockManager {
    private final static String SOURCE = "aksess.LockManager";

    private static Map<Integer, ContentLock> locks = new ConcurrentHashMap<Integer, ContentLock>();
    private static int lockTimeToLive = Aksess.getLockTimeToLive()* 1000;

    /**
     * @param contentId to get lock for.
     * @return the ContentLock for the Content with contentId if it exists. Null if not locked or lock is expired.
     */
    public static ContentLock peekAtLock(int contentId) {
        Log.info(SOURCE, "Peeking at lock for content " + contentId);
        ContentLock lock = locks.get(contentId);
        // No lock
        if(lock == null) {
            return null;
        } else {
            // Check if lock is expired
            long age = new Date().getTime() - lock.getCreateTime().getTime();

            boolean expired = age > lockTimeToLive;

            if(expired) {
                Log.info(SOURCE, "Expired lock for content " + contentId);
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
    public static boolean lockContent(String owner, int contentId) {
        Log.info(SOURCE, "Lockingcontent " + contentId + " for owner " + owner);
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
    public static void releaseLock(int contentId) {
        Log.info(SOURCE, "Releasing lock for content " + contentId);
        locks.remove(contentId);
    }

    /**
     * Release locks owned by owner
     * @param owner - user owning the locks.
     */
    public static void releaseLocksForOwner(final String owner) {
        Log.info(SOURCE, "Releasinglock for owner " + owner);
        Map<Integer, ContentLock> locks = getLocks();
        Set<Map.Entry<Integer,ContentLock>> lockEntries = locks.entrySet();
        Collection<Map.Entry<Integer, ContentLock>> contentLocksWithOwner = Collections2.filter(lockEntries, new Predicate<Map.Entry<Integer, ContentLock>>() {
            @Override
            public boolean apply(@Nullable Map.Entry<Integer, ContentLock> lockEntry) {
                return lockEntry.getValue().getOwner().equals(owner);
            }
        });
        Collection<Integer> lockIdsForOwner = Collections2.transform(contentLocksWithOwner, new Function<Map.Entry<Integer, ContentLock>, Integer>() {
            @Override
            public Integer apply(@Nullable Map.Entry<Integer, ContentLock> lockEntry) {
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
    public static void cleanup() {
        Log.info(SOURCE, "Cleaning locks");
        for (Integer id : LockManager.getLocks().keySet()) {
            LockManager.peekAtLock(id);
        }
    }

    public static Map<Integer, ContentLock> getLocks() {
        return locks;
    }
}