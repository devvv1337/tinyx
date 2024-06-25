package com.tinyx.metrics;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MetricsService {

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    public Metrics getMetrics() {
        Metrics metrics = new Metrics();
        metrics.setPostCount(registry.counter("post_count").getCount());
        metrics.setUserCount(registry.counter("user_count").getCount());
        return metrics;
    }
}
