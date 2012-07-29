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

public class ProgressReporter {

    private final String docType;
    private final long total;
    private final AtomicLong current;
    private final AtomicBoolean isFinished;

    public ProgressReporter(String docType, long total) {
        this.docType = docType;
        this.total = total;
        this.current = new AtomicLong(0L);
        isFinished = new AtomicBoolean(false);
    }

    public void reportProgress(){
        if(current.incrementAndGet() == total){
            isFinished.set(true);
        }
    }

    public boolean isFinished(){
        return isFinished.get();
    }

    public long getCurrent() {
        return current.get();
    }

    public long getTotal() {
        return total;
    }

    public String getDocType() {
        return docType;
    }
}
