package org.kantega.openaksess.plugins.metrics;

import org.joda.time.LocalDateTime;
import org.kantega.openaksess.plugins.metrics.dao.MetricsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/administration/metrics")
public class MetricsController {

    @Autowired
    private MetricsDao dao;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView show() {
        return new ModelAndView("org/kantega/openaksess/plugins/metrics/view");
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public @ResponseBody List<MetricsDatapoint> getData(@RequestParam LocalDateTime from, @RequestParam LocalDateTime to){
        return dao.getMetrics(from, to);
    }
}
