package org.kantega.openaksess.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

public class OpenAksessMetrics {
    public static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    static {
        addMetricsGauges();
    }

    private static void addMetricsGauges() {
        MetricRegistry metrics = OpenAksessMetrics.METRIC_REGISTRY;
        metrics.register(MetricRegistry.name(dbConnectionFactory.class, "open-connections"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return dbConnectionFactory.getActiveConnections();
                    }
                });

        metrics.register(MetricRegistry.name(dbConnectionFactory.class, "idle-connections"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return dbConnectionFactory.getIdleConnections();
                    }
                });

        metrics.register(MetricRegistry.name(dbConnectionFactory.class, "max-connections"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return dbConnectionFactory.getMaxConnections();
                    }
                });
    }
}
