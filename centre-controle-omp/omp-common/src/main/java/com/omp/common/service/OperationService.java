package com.omp.common.service;

import com.omp.common.entity.Domain;
import com.omp.common.entity.Location;
import com.omp.common.entity.Operation;
import com.omp.common.entity.OperationType;
import com.omp.common.entity.Status;
import com.omp.common.interceptor.Audited;
import com.omp.common.repository.ClientRepository;
import com.omp.common.repository.LocationRepository;
import com.omp.common.repository.OperationRepository;
import com.omp.common.repository.OperationTypeRepository;
import com.omp.common.repository.StatusRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class OperationService {

    @Inject
    private OperationRepository operationRepository;

    @Inject
    private OperationTypeRepository operationTypeRepository;

    @Inject
    private ClientRepository clientRepository;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private StatusRepository statusRepository;

    public Optional<Operation> findById(Long id) {
        return operationRepository.findById(id);
    }

    public List<Operation> findByDomainCode(String domainCode) {
        return operationRepository.findByDomainCode(domainCode);
    }

    public List<Operation> findActiveBetween(LocalDateTime from, LocalDateTime to) {
        return operationRepository.findActiveBetween(from, to);
    }

    @Audited(action = "CREATE", entityType = "Operation")
    public Operation create(String operationCode, String operationTypeCode, Domain domain,
                             Long clientId, Long locationId, LocalDateTime startDatetime, String description) {
        OperationType operationType = operationTypeRepository
                .findByDomainCodeAndCode(domain.getCode(), operationTypeCode)
                .orElseThrow(() -> new IllegalArgumentException("OperationType inconnu: " + operationTypeCode));

        Operation operation = new Operation();
        operation.setOperationCode(operationCode);
        operation.setOperationType(operationType);
        operation.setDomain(domain);
        operation.setStartDatetime(startDatetime);
        operation.setDescription(description);

        if (clientId != null) {
            operation.setClient(clientRepository.findById(clientId).orElse(null));
        }
        if (locationId != null) {
            operation.setLocation(locationRepository.findById(locationId).orElse(null));
        }
        statusRepository.findByCategoryAndCode("OPERATION", "PLANNED").ifPresent(operation::setStatus);

        return operationRepository.save(operation);
    }

    /**
     * Purge des operations terminees/annulees plus vieilles que le cutoff (cf docker/README,
     * retention configurable via OMP_PURGE_RETENTION_DAYS). Le cascade ON DELETE CASCADE (Flyway
     * V2-V4) supprime automatiquement les enfants (operation_step, event, incident, alert,
     * railway_rotation/port_call...) - pas besoin de les charger un par un ici.
     */
    @Audited(action = "PURGE", entityType = "Operation")
    public int purgeBefore(LocalDateTime cutoff) {
        List<Operation> purgeable = operationRepository.findPurgeable(cutoff);
        purgeable.forEach(operationRepository::delete);
        return purgeable.size();
    }

    @Audited(action = "UPDATE_STATUS", entityType = "Operation")
    public Operation changeStatus(Long operationId, String statusCode) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Operation inconnue: " + operationId));
        Status status = statusRepository.findByCategoryAndCode("OPERATION", statusCode)
                .orElseThrow(() -> new IllegalArgumentException("Statut inconnu: " + statusCode));
        operation.setStatus(status);
        if ("COMPLETED".equals(statusCode) || "CANCELLED".equals(statusCode)) {
            operation.setEndDatetime(LocalDateTime.now());
        }
        return operationRepository.update(operation);
    }
}
