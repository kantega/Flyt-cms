package org.kantega.openaksess.plugins.metrics.dao;

import org.joda.time.LocalDateTime;
import org.kantega.openaksess.plugins.metrics.MetricsModel;

import java.util.List;

public interface MetricsDao {
    void saveMetrics(MetricsModel model);
    List<MetricsModel> getMetrics(LocalDateTime start, LocalDateTime end);
}
