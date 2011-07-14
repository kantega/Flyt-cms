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

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;

import java.util.*;

public class LockManager {
    private final static String SOURCE = "aksess.LockManager";

    private static Map locks = new HashMap();
    private static int lockTimeToLive = Aksess.getLockTimeToLive()* 1000;


    public static ContentLock peekAtLock(int contentId) {
        ContentLock lock;
        synchronized(locks) {
            lock = (ContentLock) locks.get(contentId);
        }
        // No lock
        if(lock == null) {
            return null;
        } else {
            // Check if lock is expired
            long age = new Date().getTime() - lock.getCreateTime().getTime();

            boolean expired = age > lockTimeToLive;

            if(expired) {
                // Remove lock and return false (not locked anymore)
                synchronized(locks) {
                    locks.remove(contentId);
                    return null;
                }
            } else {
                // Yes, the content is locked
                return lock;
            }
        }
    }

    public static boolean lockContent(String owner, int contentId) {
        ContentLock contentLock = peekAtLock(contentId);
        if(contentLock != null) {
            // Content already locked
            return false;
        } else {
            // Kan kun låse et objekt i gangen
            releaseLocksForOwner(owner);

            ContentLock lock = new ContentLock(owner, contentId);
            synchronized(locks) {
                locks.put(contentId, lock);
            }
            return true;
        }
    }

    public static void releaseLock(int contentId) {
        synchronized(locks) {
            locks.remove(contentId);
        }
    }

    public static void releaseLocksForOwner(String owner) {
        synchronized(locks) {
            Iterator i = LockManager.getLocks().values().iterator();
            while (i.hasNext()) {
                ContentLock contentLock = (ContentLock) i.next();
                if(contentLock.getOwner().equals(owner)) {
                    locks.remove(contentLock.getContentId());
                }
            }
        }
    }

    public static void cleanup() {
        synchronized(locks) {

            Iterator i = LockManager.getLocks().keySet().iterator();
            while (i.hasNext()) {
                Integer id = (Integer) i.next();
                LockManager.peekAtLock(id);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {


        LockManager.setLockTimeToLive(100);
        Thread m = new Thread() {
            public void run() {
                while(2 < 3)  {
                    LockManager.cleanup();

                    LockManager.getLocks();
                    LockManager.printLocks();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Log.error(SOURCE, e, null, null);
                    }
                }
            }
        };

        m.start();

        Random r = new Random();
        for(int i = 0; i < 100; i++) {
            TestThread thread = new TestThread(10);
            thread.start();

            Thread.sleep(Math.abs(r.nextInt()) % 3000);

        }


    }

    public static Map getLocks() {
        synchronized(locks) {
            return new HashMap(locks);
        }
    }

    public static void setLockTimeToLive(int lockTimeToLive) {
        LockManager.lockTimeToLive = lockTimeToLive;
    }

    public static void printLocks() {
        synchronized(locks) {
            System.out.println("#######################################");
           Iterator i = locks.values().iterator();
            while (i.hasNext()) {
                ContentLock contentLock = (ContentLock) i.next();
                System.out.println(contentLock.getContentId() +" locked by " +contentLock.getOwner() +" at " + contentLock.getCreateTime());
            }
            System.out.println("#######################################");
        }
    }
}

 class TestThread extends Thread {
        private int runs;
        private Random  random = new Random();
        public TestThread(int runs) {
            this.runs = runs;
        }

        public void run() {
            for(int i = 0; i < runs; i++) {
                try {

                    int c = Math.abs(random.nextInt()) % 10;
                    LockManager.peekAtLock(c);
                    Thread.sleep(100);
                    LockManager.lockContent(Thread.currentThread().getName(), c);
                    Thread.sleep(100);
                    LockManager.releaseLock(c);
                    Thread.sleep(200);
                    if(c==9) {
                        LockManager.releaseLocksForOwner(Thread.currentThread().getName());
                    }
                } catch (InterruptedException e) {
                    Log.error(getClass().getName(), e, null, null);
                }
            }
            System.out.println(Thread.currentThread() +" finished");
        }
 }
