package org.kantega.openaksess.metrics;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

public class HealthRegistryServletContextListener extends HealthCheckServlet.ContextListener {
    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return new HealthCheckRegistry();
    }
}
