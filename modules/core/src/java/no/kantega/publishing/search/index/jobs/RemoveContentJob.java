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

package no.kantega.publishing.search.index.jobs;

import no.kantega.search.index.jobs.context.JobContext;
import no.kantega.search.index.jobs.IndexJob;
import no.kantega.search.index.provider.DocumentProvider;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 *
 */
public class RemoveContentJob extends IndexJob {
    private Logger log = Logger.getLogger(getClass());

    public RemoveContentJob(String contentId, String source) {
        super(contentId, source);
    }

    public void executeJob(JobContext context) {
        IndexReader ir = null;
        try {
            ir = context.getIndexReaderManager().getReader("aksess");

            DocumentProvider provider = context.getDocumentProviderSelector().select(getSource());
            Term deleteTerm = provider.getDeleteTerm(getContentId());
            ir.deleteDocuments(deleteTerm);

        } catch (IOException e) {
            log.error(e);
        } finally {
            if(ir != null) {
                try {
                    ir.close();
                } catch (IOException e) {
                    // 
                }
            }
        }
    }
}
