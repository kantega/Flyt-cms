package org.kantega.openaksess.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

public class OpenAksessMetrics {
    public static final MetricRegistry METRIC_REGISTRY = SharedMetricRegistries.getOrCreate("oa-metric-registry");

    static {
        addMetricsGauges();
        addJVMMetrics();
    }

    private static void addJVMMetrics() {
        METRIC_REGISTRY.registerAll(new GarbageCollectorMetricSet());
        METRIC_REGISTRY.registerAll(new JvmAttributeGaugeSet());
        METRIC_REGISTRY.registerAll(new ThreadStatesGaugeSet());
        METRIC_REGISTRY.registerAll(new MemoryUsageGaugeSet());
        METRIC_REGISTRY.register(MetricRegistry.name(FileDescriptorRatioGauge.class), new FileDescriptorRatioGauge());
    }

    private static void addMetricsGauges() {
        METRIC_REGISTRY.register(MetricRegistry.name(dbConnectionFactory.class, "open-connections"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return dbConnectionFactory.getActiveConnections();
                    }
                });

        METRIC_REGISTRY.register(MetricRegistry.name(dbConnectionFactory.class, "idle-connections"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return dbConnectionFactory.getIdleConnections();
                    }
                });

        METRIC_REGISTRY.register(MetricRegistry.name(dbConnectionFactory.class, "max-connections"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return dbConnectionFactory.getMaxConnections();
                    }
                });
    }

    public MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }
}
