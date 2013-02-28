package no.kantega.openaksess.search.index.rebuild;

import no.kantega.search.api.index.ProgressReporter;

import java.util.List;

public class ProgressReporterUtils {

    public static boolean notAllProgressReportersAreMarkedAsFinished(List<ProgressReporter> progressReporters) {
        boolean isFinished = true;
        for (ProgressReporter progressReporter : progressReporters) {
            isFinished &= progressReporter.isFinished();
        }
        return !isFinished;
    }
}
