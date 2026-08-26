package com.omp.api.scheduler;

import com.omp.common.service.KpiCalculationService;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Calcule les KPI (cf docs/referentiel-*.md section KPI) toutes les 15 minutes. */
@Singleton
@Startup
public class KpiCalculationTimerBean {

    private static final Logger LOG = Logger.getLogger(KpiCalculationTimerBean.class.getName());

    @Inject
    private KpiCalculationService kpiCalculationService;

    @Schedule(hour = "*", minute = "*/15", persistent = false)
    public void run() {
        try {
            kpiCalculationService.calculatePendingKpis();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Echec du calcul periodique des KPI", e);
        }
    }
}
