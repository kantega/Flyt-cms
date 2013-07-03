package no.kantega.openaksess.plugins.metrics;

import org.junit.Test;
import org.kantega.openaksess.plugins.metricsManager.SaveMetricsJob;

public class MetricsTest {

    @Test
    public void saveMetricsTest(){
        SaveMetricsJob job = new SaveMetricsJob();

        job.autoSaveMetrics();

    }

}
