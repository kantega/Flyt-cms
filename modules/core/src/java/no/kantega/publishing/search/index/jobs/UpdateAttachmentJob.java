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
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 *
 */
public class UpdateAttachmentJob extends IndexJob {

    private String attachmentId;

    private Logger log = Logger.getLogger(getClass());
    public UpdateAttachmentJob(String attachmentId, String source) {
        this.attachmentId = attachmentId;
        setSource(source);
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void executeJob(JobContext context) {
        new RemoveAttachmentJob(attachmentId, getSource()).executeJob(context);
        try {
            IndexWriter writer = context.getIndexWriterManager().getIndexWriter("aksess", false);
            DocumentProvider provider = context.getDocumentProviderSelector().select(getSource());
            Document d = provider.provideDocument(attachmentId);
            writer.addDocument(d);

        } catch (IOException e) {
            log.error(e);
        } finally {
            try {
                context.getIndexWriterManager().ensureClosed("aksess");
            } catch (IOException e) {
                log.error(e);
            }
        }
    }
}
