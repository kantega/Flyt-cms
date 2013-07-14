package org.kantega.openaksess.plugins.metrics.dao;

import org.joda.time.LocalDateTime;
import org.kantega.openaksess.plugins.metrics.MetricsDatapoint;

import java.util.List;

public interface MetricsDao {
    MetricsDatapoint saveMetrics(MetricsDatapoint model);
    List<MetricsDatapoint> getMetrics(LocalDateTime start, LocalDateTime end);
}
