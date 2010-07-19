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

package no.kantega.search.index;

import no.kantega.commons.log.Log;
import no.kantega.search.index.jobs.IndexJob;

import java.util.List;
import java.util.ArrayList;

/**
 * Date: Jan 5, 2009
 * Time: 11:01:11 AM
 *
 * @author Tarje Killingberg
 */
public class IndexJobManager implements Runnable {

    private static final String SOURCE = IndexJobManager.class.getName();

    private List indexJobQueue = new ArrayList();
    private Thread thread;
    private boolean shutdownHint = false;


    public void init() {
        thread = new Thread(this);
        Log.debug(SOURCE, "IndexManager kicking off IndexJob Thread", null, null);
        thread.start();
    }

    public void destroy() {
        shutdownHint = true;
        synchronized (indexJobQueue) {
            System.out.println("destroy() notifying all threads");
            indexJobQueue.notifyAll();
        }

        if(thread.isAlive()) {
            long maxWait = 30000;
            long start = System.currentTimeMillis();
            System.out.println("Waiting for IndexManager thread to shut down");

            while(thread.isAlive()) {
                if(System.currentTimeMillis() - start > maxWait) {
                    System.out.println("IndexManager thread did not shut down in " +maxWait +" ms, returning forcefully");
                    return;
                } else {
                    try {
                        System.out.print(".");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("IndexManager thread shut down OK");
    }

    public void run() {

        while (true) {
            IndexJob job = null;
            synchronized (indexJobQueue) {
                if (indexJobQueue.size() == 0) {
                    Log.debug(SOURCE, "No jobs in queue, waiting", null, null);
                    try {
                        indexJobQueue.wait();
                    } catch (InterruptedException e) {
                        Log.error(SOURCE, e, null, null);
                    }
                }
                if (shutdownHint) {
                    Log.info(SOURCE, "Someone wants us to shut down, search update thread returning", null, null);
                    return;
                }
                job = (IndexJob) indexJobQueue.get(0);
                Log.debug(SOURCE, "Index updater thread got woken up, getting first job from queue: " + job, null, null);
            }
            try {
                job.executeJob(IndexManagerImpl.getInstance().new DefaultJobContext());
                if (shutdownHint) {
                    Log.info(SOURCE, "Someone wants us to shut down, search update thread returning", null, null);
                    return;
                }
            } catch (Throwable e) {
                Log.error(SOURCE, e, null, null);
            } finally {
                synchronized (indexJobQueue) {
                    indexJobQueue.remove(job);
                    Log.debug(SOURCE, "Removed job " + job + " from queue, size is now " + indexJobQueue.size(), null, null);
                }
            }
        }

    }

    public void addIndexJob(IndexJob job) {
        synchronized (indexJobQueue) {
            indexJobQueue.add(job);
            Log.debug(SOURCE, "Added job " + job + " to queue. Size is now " + indexJobQueue.size(), null, null);
            indexJobQueue.notifyAll();
        }
    }

    public boolean getShutdownHint() {
        return shutdownHint;
    }

}
