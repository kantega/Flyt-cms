package org.kantega.openaksess.plugins.metricsManager;

import org.kantega.openaksess.plugins.metricsManager.dao.MetricsDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
@RequestMapping("/administration/metrics")
public class MetricsController {

    private MetricsDao dao;

    @RequestMapping(method = RequestMethod.GET)
    public String show() throws IOException {
        SaveMetricsJob job = new SaveMetricsJob();
        job.autoSaveMetrics();
        return null;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handle() throws IOException {
        SaveMetricsJob job = new SaveMetricsJob();
        job.autoSaveMetrics();
        return null;
    }
}
