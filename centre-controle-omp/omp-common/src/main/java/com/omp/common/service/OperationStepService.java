package com.omp.common.service;

import com.omp.common.entity.Operation;
import com.omp.common.entity.OperationStep;
import com.omp.common.entity.StepType;
import com.omp.common.interceptor.Audited;
import com.omp.common.repository.OperationRepository;
import com.omp.common.repository.OperationStepRepository;
import com.omp.common.repository.StepTypeRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class OperationStepService {

    @Inject
    private OperationStepRepository operationStepRepository;

    @Inject
    private OperationRepository operationRepository;

    @Inject
    private StepTypeRepository stepTypeRepository;

    public List<OperationStep> findByOperationId(Long operationId) {
        return operationStepRepository.findByOperationId(operationId);
    }

    @Audited(action = "ADD_STEP", entityType = "OperationStep")
    public OperationStep addStep(Long operationId, String stepTypeCode, LocalDateTime plannedStart) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Operation inconnue: " + operationId));
        StepType stepType = stepTypeRepository.findByDomainCodeAndCode(operation.getDomain().getCode(), stepTypeCode)
                .orElseThrow(() -> new IllegalArgumentException("StepType inconnu: " + stepTypeCode));

        int nextSequence = operationStepRepository.findByOperationId(operationId).size() + 1;

        OperationStep step = new OperationStep();
        step.setOperation(operation);
        step.setStepType(stepType);
        step.setSequence(nextSequence);
        step.setPlannedStart(plannedStart);

        return operationStepRepository.save(step);
    }

    @Audited(action = "COMPLETE_STEP", entityType = "OperationStep")
    public OperationStep markActualStart(Long stepId) {
        OperationStep step = operationStepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("OperationStep inconnu: " + stepId));
        step.setActualStart(LocalDateTime.now());
        return operationStepRepository.update(step);
    }

    @Audited(action = "COMPLETE_STEP", entityType = "OperationStep")
    public OperationStep markActualEnd(Long stepId) {
        OperationStep step = operationStepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("OperationStep inconnu: " + stepId));
        step.setActualEnd(LocalDateTime.now());
        if (step.getActualStart() != null) {
            double hours = java.time.Duration.between(step.getActualStart(), step.getActualEnd()).toMinutes() / 60.0;
            step.setDurationHours(hours);
        }
        return operationStepRepository.update(step);
    }
}
