package com.omp.api.scheduler;

import com.omp.common.service.OperationService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Purge quotidienne des vieilles operations (cf doc conception §2/§9, retention 30 jours par defaut). */
@Singleton
@Startup
public class PurgeTimerBean {

    private static final Logger LOG = Logger.getLogger(PurgeTimerBean.class.getName());
    private static final int DEFAULT_RETENTION_DAYS = 30;

    @Inject
    private OperationService operationService;

    private int retentionDays;

    @PostConstruct
    void init() {
        String configured = System.getenv("OMP_PURGE_RETENTION_DAYS");
        retentionDays = configured != null ? Integer.parseInt(configured) : DEFAULT_RETENTION_DAYS;
    }

    @Schedule(hour = "3", minute = "0", persistent = false)
    public void run() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        try {
            int purged = operationService.purgeBefore(cutoff);
            LOG.info(() -> "Purge quotidienne : " + purged + " operation(s) anterieure(s) a " + cutoff);
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Echec de la purge quotidienne", e);
        }
    }
}
