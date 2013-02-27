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

package no.kantega.search.api.index;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * When reindexing the reindexer should report to the
 * ProgressReporter as it submits documents.
 */
public class ProgressReporter {

    private final String docType;
    private final long total;
    private final AtomicLong current;
    private final AtomicBoolean isFinished;
    private final AtomicBoolean isStarted;

    /**
     * @param docType - the document type of this indexprocess, typically the value of indexedContentType
     * @param total - The total number of documents that is submitted.
     */
    public ProgressReporter(String docType, long total) {
        this.docType = docType;
        this.total = total;
        this.current = new AtomicLong(0L);
        isFinished = new AtomicBoolean(false);
        isStarted = new AtomicBoolean(false);
    }

    /**
     * Report that processing and submiting of a single document has been performed.
     */
    public void reportProgress(){
        if(current.incrementAndGet() == total){
            isFinished.set(true);
        }
    }

    /**
     * @return true if the progress has been reported as many times as the number of total documents.
     */
    public boolean isFinished(){
        return isFinished.get();
    }

    /**
     * @return the current number of progresses made, i.e. how many times reportProgress has been called.
     */
    public long getCurrent() {
        return current.get();
    }

    public long getTotal() {
        return total;
    }

    public String getDocType() {
        return docType;
    }

    public AtomicBoolean getStarted() {
        return isStarted;
    }

    public void setStarted() {
        isStarted.set(true);
    }
}
