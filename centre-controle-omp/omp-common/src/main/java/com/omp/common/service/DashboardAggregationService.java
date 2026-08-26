package com.omp.common.service;

import com.omp.common.entity.Operation;
import com.omp.common.enums.AlertStatus;
import com.omp.common.repository.AlertRepository;
import com.omp.common.repository.IncidentRepository;
import com.omp.common.repository.OperationRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Point unique d'agregation transversale port+rail+maintenance pour le dashboard general.
 *
 * Ajustement par rapport au plan initial (decision d'architecture #7) : ce service repose
 * UNIQUEMENT sur les entites generiques (Operation/Alert/Incident, deja domain-agnostic via
 * Domain/Status), pas sur les repositories specifiques omp-port/omp-rail. Raison : omp-web appelle
 * les EJB en local (decision #1) mais ne doit jamais dependre de omp-api (qui reste reserve aux
 * clients HTTP/WebSocket) - seul omp-common est visible des deux. Placer ce service dans
 * omp-common evite donc a la fois le cycle Maven (raison initiale de la decision #7) et ce
 * probleme d'acces depuis omp-web, sans perdre d'information puisque le detail port/rail est deja
 * porte par Domain sur Operation.
 */
@Stateless
public class DashboardAggregationService {

    @Inject
    private OperationRepository operationRepository;

    @Inject
    private AlertRepository alertRepository;

    @Inject
    private IncidentRepository incidentRepository;

    public DashboardSummary summarize() {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);

        List<Operation> todayOperations = operationRepository.findActiveBetween(todayStart, todayEnd);
        Map<String, Long> operationsByDomain = todayOperations.stream()
                .collect(Collectors.groupingBy(o -> o.getDomain().getCode(), Collectors.counting()));

        Map<String, Long> inProgressByDomain = todayOperations.stream()
                .filter(o -> o.getStatus() != null && "IN_PROGRESS".equals(o.getStatus().getCode()))
                .collect(Collectors.groupingBy(o -> o.getDomain().getCode(), Collectors.counting()));

        long openAlerts = alertRepository.findByStatus(AlertStatus.OPEN).size();
        long openIncidents = incidentRepository.findOpen().size();

        return new DashboardSummary(todayOperations.size(), operationsByDomain, inProgressByDomain,
                openAlerts, openIncidents);
    }

    public record DashboardSummary(
            int operationsToday,
            Map<String, Long> operationsByDomain,
            Map<String, Long> operationsInProgressByDomain,
            long openAlerts,
            long openIncidents
    ) {
    }
}
