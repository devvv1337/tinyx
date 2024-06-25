package com.tinyx.metrics;

import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/metrics")
public class MetricsResource {

    @Inject
    MetricsService metricsService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Metered(name = "getMetrics", description = "Metrics retrieval")
    public Metrics getMetrics() {
        return metricsService.getMetrics();
    }
}
