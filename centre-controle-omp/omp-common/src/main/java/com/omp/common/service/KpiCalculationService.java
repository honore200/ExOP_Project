package com.omp.common.service;

import com.omp.common.entity.Kpi;
import com.omp.common.entity.KpiValue;
import com.omp.common.entity.Operation;
import com.omp.common.entity.OperationStep;
import com.omp.common.repository.IncidentRepository;
import com.omp.common.repository.KpiRepository;
import com.omp.common.repository.KpiValueRepository;
import com.omp.common.repository.OperationRepository;
import com.omp.common.repository.OperationStepRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Calcule les KPI (cf docs referentiel + MCD doc2 section 11) pour toute Operation terminee qui
 * n'a pas encore de KpiValue associee. Invoque periodiquement par KpiCalculationTimerBean
 * (omp-api/scheduler, @Schedule) - la logique de calcul vit ici (omp-common) car elle interroge
 * uniquement des entites generiques (Operation/OperationStep/Incident), pas de dependance
 * omp-port/omp-rail necessaire (ARRIVAL_TO_DEPARTURE, TOTAL_WAITING_TIME, INCIDENT_COUNT et les
 * durees de step nommees s'appliquent a n'importe quel domaine).
 */
@Stateless
public class KpiCalculationService {

    private static final String ARRIVAL_TO_DEPARTURE = "ARRIVAL_TO_DEPARTURE";
    private static final String TOTAL_WAITING_TIME = "TOTAL_WAITING_TIME";
    private static final String UNLOADING_TIME = "UNLOADING_TIME";
    private static final String FORMATION_TIME = "FORMATION_TIME";
    private static final String BRAKE_TEST_TIME = "BRAKE_TEST_TIME";
    private static final String INCIDENT_COUNT = "INCIDENT_COUNT";

    @Inject
    private OperationRepository operationRepository;

    @Inject
    private OperationStepRepository operationStepRepository;

    @Inject
    private IncidentRepository incidentRepository;

    @Inject
    private KpiRepository kpiRepository;

    @Inject
    private KpiValueRepository kpiValueRepository;

    public void calculatePendingKpis() {
        List<Operation> completed = operationRepository.findAll().stream()
                .filter(o -> o.getStatus() != null && "COMPLETED".equals(o.getStatus().getCode()))
                .filter(o -> o.getEndDatetime() != null)
                .toList();

        for (Operation operation : completed) {
            computeArrivalToDeparture(operation);
            computeTotalWaitingTime(operation);
            computeStepDuration(operation, "UNLOADING", UNLOADING_TIME);
            computeStepDuration(operation, "FORMATION", FORMATION_TIME);
            computeStepDuration(operation, "BRAKE_TEST", BRAKE_TEST_TIME);
            computeIncidentCount(operation);
        }
    }

    private void computeArrivalToDeparture(Operation operation) {
        if (kpiValueRepository.existsForOperationAndKpi(operation.getId(), ARRIVAL_TO_DEPARTURE)) {
            return;
        }
        double hours = Duration.between(operation.getStartDatetime(), operation.getEndDatetime()).toMinutes() / 60.0;
        record(operation, ARRIVAL_TO_DEPARTURE, BigDecimal.valueOf(hours));
    }

    private void computeTotalWaitingTime(Operation operation) {
        if (kpiValueRepository.existsForOperationAndKpi(operation.getId(), TOTAL_WAITING_TIME)) {
            return;
        }
        double totalWaiting = operationStepRepository.findByOperationId(operation.getId()).stream()
                .map(OperationStep::getWaitingHours)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
        record(operation, TOTAL_WAITING_TIME, BigDecimal.valueOf(totalWaiting));
    }

    private void computeStepDuration(Operation operation, String stepTypeCode, String kpiCode) {
        if (kpiValueRepository.existsForOperationAndKpi(operation.getId(), kpiCode)) {
            return;
        }
        Optional<Double> duration = operationStepRepository.findByOperationId(operation.getId()).stream()
                .filter(s -> s.getStepType().getCode().equals(stepTypeCode))
                .map(OperationStep::getDurationHours)
                .filter(java.util.Objects::nonNull)
                .findFirst();
        duration.ifPresent(hours -> record(operation, kpiCode, BigDecimal.valueOf(hours)));
    }

    private void computeIncidentCount(Operation operation) {
        if (kpiValueRepository.existsForOperationAndKpi(operation.getId(), INCIDENT_COUNT)) {
            return;
        }
        long count = incidentRepository.findByOperationId(operation.getId()).size();
        record(operation, INCIDENT_COUNT, BigDecimal.valueOf(count));
    }

    private void record(Operation operation, String kpiCode, BigDecimal value) {
        Kpi kpi = kpiRepository.findByCode(kpiCode).orElse(null);
        if (kpi == null) {
            return; // referentiel KPI non seede (V5) - ne bloque pas le reste du calcul
        }
        KpiValue kpiValue = new KpiValue();
        kpiValue.setKpi(kpi);
        kpiValue.setOperation(operation);
        kpiValue.setValue(value.setScale(4, RoundingMode.HALF_UP));
        kpiValue.setPeriodStart(operation.getStartDatetime());
        kpiValue.setPeriodEnd(operation.getEndDatetime() != null ? operation.getEndDatetime() : LocalDateTime.now());
        kpiValueRepository.save(kpiValue);
    }
}
