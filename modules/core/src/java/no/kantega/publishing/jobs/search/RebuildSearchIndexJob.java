package no.kantega.publishing.jobs.search;

import no.kantega.commons.log.Log;
import no.kantega.publishing.search.index.jobs.OptimizeIndexJob;
import no.kantega.publishing.search.index.jobs.RebuildIndexJob;
import no.kantega.publishing.search.index.jobs.RebuildSpellCheckIndexJob;
import no.kantega.search.index.rebuild.ProgressReporter;

/**
 *
 */
public class RebuildSearchIndexJob {
    private IndexManager indexManager;

    public void execute() {
        Log.info(getClass().getName(), "Starting rebuild of index", null, null);

        ProgressReporter p = new ProgressReporter() {

            public void reportProgress(int c, String d, int t) {
            }

            public void reportFinished() {
                Log.info(getClass().getName(), "Finished rebuilding index", null, null);
            }
        };

        indexManager.addIndexJob(new RebuildIndexJob(p));
        indexManager.addIndexJob(new OptimizeIndexJob());
        indexManager.addIndexJob(new RebuildSpellCheckIndexJob());
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }
}
