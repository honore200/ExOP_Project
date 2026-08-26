package com.omp.web.bean;

import com.omp.common.service.DashboardAggregationService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class DashboardGlobalBean implements Serializable {

    @Inject
    private DashboardAggregationService dashboardAggregationService;

    private DashboardAggregationService.DashboardSummary summary;

    public void init() {
        summary = dashboardAggregationService.summarize();
    }

    public DashboardAggregationService.DashboardSummary getSummary() {
        return summary;
    }
}
