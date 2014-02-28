package org.kantega.openaksess.metrics;

import com.codahale.metrics.MetricRegistry;

public class InstrumentedFilterContextListener extends com.codahale.metrics.servlet.InstrumentedFilterContextListener {
    @Override
    protected MetricRegistry getMetricRegistry() {
        return OpenAksessMetrics.METRIC_REGISTRY;
    }
}
