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
import no.kantega.search.index.Fields;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class RebuildSpellCheckIndexJob extends IndexJob {

    private Logger log = Logger.getLogger(getClass());

    public void executeJob(final JobContext context) {
        IndexReader ir = null;
        Directory spellingDirectory = null;
        try {
            ir = context.getIndexReaderManager().getReader("spelling");
            spellingDirectory = ir.directory();
            SpellChecker checker = new SpellChecker(spellingDirectory);
            log.info("Updating spellchecking index");
            long before = System.currentTimeMillis();
            checker.indexDictionary(new LuceneDictionary(context.getIndexReaderManager().getReader("aksess"), Fields.CONTENT_UNSTEMMED));
            log.info("Finished updating spellchecking index in " + (System.currentTimeMillis()-before));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (spellingDirectory!=null) {
                    spellingDirectory.close();
                }
                if (ir!=null) {
                    ir.close();
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
