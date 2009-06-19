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
import no.kantega.search.index.provider.DocumentProviderHandler;
import no.kantega.search.index.rebuild.ProgressReporter;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RebuildIndexJob extends IndexJob {
    private ProgressReporter progressReporter;

    private Logger log = Logger.getLogger(getClass());

    public RebuildIndexJob(ProgressReporter progressReporter) {
        this.progressReporter = progressReporter;
    }


    public ProgressReporter getProgressReporter() {
        return progressReporter;
    }

    public void executeJob(final JobContext context) {
        try {
            boolean createIndex = true;

            if(getSource() != null) {
                createIndex = false;
                DocumentProvider provider = context.getDocumentProviderSelector().select(getSource());
                Term deleteTerm = provider.getDeleteAllTerm();

                IndexReader ir = null;
                try {
                    log.info("Source is '" +getSource() +"', deleting all documents from that provider");

                    ir = context.getIndexReaderManager().getReader("aksess");
                    int del = ir.deleteDocuments(deleteTerm);
                    log.info(del + " documents deleted ");

                } catch (IOException e) {
                    log.error(e);
                } finally {
                    if(ir != null) {
                        try {
                            ir.close();
                        } catch (IOException e) {
                            log.error(e);
                        }
                    }
                }
            }

            final IndexWriter writer = context.getIndexWriterManager().getIndexWriter("aksess", createIndex);

            DocumentProviderHandler handler = new DocumentProviderHandler() {
                int docs = 0;

                public void handleDocument(Document document) {
                    try {
                        writer.addDocument(document);
                        if(docs++ % 1000 == 0) {
                            writer.optimize();
                            writer.commit();
                        }
                    } catch (IOException e) {
                        log.error(e);
                    }
                }

                public boolean isStopRequested() {
                    return context.isStopRequested();
                }
            };

            List providers = new ArrayList();
            if(getSource() == null) {
                providers.addAll(context.getDocumentProviderSelector().getAllProviders());
            } else {
                providers.add(context.getDocumentProviderSelector().select(getSource()));
            }

            for (int i = 0; i < providers.size(); i++) {
                DocumentProvider provider = (DocumentProvider) providers.get(i);
                log.info("Adding documents from provider " + provider.getClass());
                provider.provideDocuments(handler, getProgressReporter());
            }


        } catch (IOException e) {
            log.error("IOException rebuilding index: " +e.getMessage());
            log.error(e);
        } finally {
            try {
                context.getIndexWriterManager().ensureClosed("aksess");
            } catch (IOException e) {
                log.error(e);
            }
            getProgressReporter().reportFinished();
            log.info("Finished rebuilding index");
        }


    }
}
