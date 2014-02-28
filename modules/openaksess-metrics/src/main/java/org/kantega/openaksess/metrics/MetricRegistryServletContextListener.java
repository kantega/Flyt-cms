package org.kantega.openaksess.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

public class MetricRegistryServletContextListener extends MetricsServlet.ContextListener {

    @Override
    protected MetricRegistry getMetricRegistry() {
        return OpenAksessMetrics.METRIC_REGISTRY;
    }

}
