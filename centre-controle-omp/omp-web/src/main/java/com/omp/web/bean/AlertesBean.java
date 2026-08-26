package com.omp.web.bean;

import com.omp.common.entity.Alert;
import com.omp.common.entity.Incident;
import com.omp.common.enums.AlertStatus;
import com.omp.common.service.AlertService;
import com.omp.common.service.IncidentService;
import com.omp.common.service.UserService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AlertesBean implements Serializable {

    @Inject
    private AlertService alertService;

    @Inject
    private IncidentService incidentService;

    @Inject
    private UserService userService;

    @Inject
    private SecurityContext securityContext;

    private List<Alert> openAlerts;
    private List<Incident> openIncidents;

    public void init() {
        openAlerts = alertService.findByStatus(AlertStatus.OPEN);
        openIncidents = incidentService.findOpen();
    }

    public void acknowledge(Alert alert) {
        Long userId = userService.findByUsername(securityContext.getCallerPrincipal().getName())
                .map(com.omp.common.entity.User::getId)
                .orElseThrow();
        alertService.acknowledge(alert.getId(), userId);
        init();
    }

    public void resolveAlert(Alert alert) {
        alertService.resolve(alert.getId());
        init();
    }

    public void resolveIncident(Incident incident) {
        incidentService.resolve(incident.getId());
        init();
    }

    public List<Alert> getOpenAlerts() {
        return openAlerts;
    }

    public List<Incident> getOpenIncidents() {
        return openIncidents;
    }
}
