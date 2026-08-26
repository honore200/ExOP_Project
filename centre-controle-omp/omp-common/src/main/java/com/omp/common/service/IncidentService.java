package com.omp.common.service;

import com.omp.common.entity.Incident;
import com.omp.common.entity.IncidentType;
import com.omp.common.entity.Operation;
import com.omp.common.interceptor.Audited;
import com.omp.common.repository.IncidentRepository;
import com.omp.common.repository.IncidentTypeRepository;
import com.omp.common.repository.OperationRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class IncidentService {

    @Inject
    private IncidentRepository incidentRepository;

    @Inject
    private IncidentTypeRepository incidentTypeRepository;

    @Inject
    private OperationRepository operationRepository;

    public List<Incident> findOpen() {
        return incidentRepository.findOpen();
    }

    public List<Incident> findByOperationId(Long operationId) {
        return incidentRepository.findByOperationId(operationId);
    }

    @Audited(action = "CREATE", entityType = "Incident")
    public Incident create(Long operationId, String incidentTypeCode, String description) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Operation inconnue: " + operationId));
        IncidentType incidentType = incidentTypeRepository.findByCode(incidentTypeCode)
                .orElseThrow(() -> new IllegalArgumentException("IncidentType inconnu: " + incidentTypeCode));

        Incident incident = new Incident();
        incident.setOperation(operation);
        incident.setIncidentType(incidentType);
        incident.setStartDatetime(LocalDateTime.now());
        incident.setDescription(description);

        return incidentRepository.save(incident);
    }

    @Audited(action = "RESOLVE", entityType = "Incident")
    public Incident resolve(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident inconnu: " + incidentId));
        incident.setResolved(true);
        incident.setEndDatetime(LocalDateTime.now());
        return incidentRepository.update(incident);
    }
}
