package no.kantega.publishing.jobs.search;

import no.kantega.commons.log.Log;

/**
 *
 */
public class RebuildSearchIndexJob {
//    private IndexManager indexManager;

    public void execute() {
        Log.info(getClass().getName(), "Starting rebuild of index", null, null);

        /*ProgressReporter p = new ProgressReporter() {

            public void reportProgress(int c, String d, int t) {
            }

            public void reportFinished() {
                Log.info(getClass().getName(), "Finished rebuilding index", null, null);
            }
        };*/

/*        indexManager.addIndexJob(new RebuildIndexJob(p));
        indexManager.addIndexJob(new OptimizeIndexJob());
        indexManager.addIndexJob(new RebuildSpellCheckIndexJob());*/
    }

}
