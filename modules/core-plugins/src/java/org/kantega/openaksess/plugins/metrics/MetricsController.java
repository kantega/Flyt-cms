package org.kantega.openaksess.plugins.metrics;

import org.kantega.openaksess.plugins.metrics.dao.MetricsDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/administration/metrics")
public class MetricsController {

    private MetricsDao dao;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView show() throws IOException {
        SaveMetricsJob job = new SaveMetricsJob();
        job.autoSaveMetrics();
//        dao.getMetrics(null,null);


        Map<String, Object> model = new HashMap<String, Object>();


        return new ModelAndView("org/kantega/openaksess/plugins/metrics/view", model);
    }

    public void setDao(MetricsDao dao) {
        this.dao = dao;
    }

}
