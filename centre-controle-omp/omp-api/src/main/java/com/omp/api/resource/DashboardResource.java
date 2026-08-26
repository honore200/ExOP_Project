package com.omp.api.resource;

import com.omp.api.security.Secured;
import com.omp.common.service.DashboardAggregationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/dashboard")
@Secured
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @Inject
    private DashboardAggregationService dashboardAggregationService;

    @GET
    @Path("/summary")
    public DashboardAggregationService.DashboardSummary summary() {
        return dashboardAggregationService.summarize();
    }
}
