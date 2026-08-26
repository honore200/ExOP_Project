package com.omp.common.service;

import com.omp.common.entity.Domain;
import com.omp.common.entity.Operation;
import com.omp.common.entity.OperationType;
import com.omp.common.entity.Status;
import com.omp.common.repository.ClientRepository;
import com.omp.common.repository.LocationRepository;
import com.omp.common.repository.OperationRepository;
import com.omp.common.repository.OperationTypeRepository;
import com.omp.common.repository.StatusRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    @Mock
    private OperationRepository operationRepository;
    @Mock
    private OperationTypeRepository operationTypeRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private OperationService operationService;

    @Test
    void create_shouldResolveOperationTypeAndPersist() {
        Domain domain = new Domain();
        domain.setCode("RAILWAY");
        OperationType operationType = new OperationType();
        operationType.setCode("TRAIN_ROTATION");
        Status plannedStatus = new Status();
        plannedStatus.setCategory("OPERATION");
        plannedStatus.setCode("PLANNED");

        when(operationTypeRepository.findByDomainCodeAndCode("RAILWAY", "TRAIN_ROTATION"))
                .thenReturn(Optional.of(operationType));
        when(statusRepository.findByCategoryAndCode("OPERATION", "PLANNED"))
                .thenReturn(Optional.of(plannedStatus));
        when(operationRepository.save(any(Operation.class))).thenAnswer(inv -> inv.getArgument(0));

        Operation result = operationService.create("RAIL-001", "TRAIN_ROTATION", domain,
                null, null, LocalDateTime.now(), "Test rotation");

        assertEquals("RAIL-001", result.getOperationCode());
        assertEquals(operationType, result.getOperationType());
        assertEquals(plannedStatus, result.getStatus());
        verify(operationRepository).save(any(Operation.class));
    }

    @Test
    void create_shouldRejectUnknownOperationType() {
        Domain domain = new Domain();
        domain.setCode("PORT");
        when(operationTypeRepository.findByDomainCodeAndCode("PORT", "UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                operationService.create("X", "UNKNOWN", domain, null, null, LocalDateTime.now(), null));
    }

    @Test
    void changeStatus_shouldSetEndDatetimeWhenCompleted() {
        Operation operation = new Operation();
        Status completedStatus = new Status();
        completedStatus.setCategory("OPERATION");
        completedStatus.setCode("COMPLETED");

        when(operationRepository.findById(1L)).thenReturn(Optional.of(operation));
        when(statusRepository.findByCategoryAndCode("OPERATION", "COMPLETED"))
                .thenReturn(Optional.of(completedStatus));
        when(operationRepository.update(operation)).thenReturn(operation);

        Operation result = operationService.changeStatus(1L, "COMPLETED");

        assertEquals(completedStatus, result.getStatus());
        assertEquals(true, result.getEndDatetime() != null);
    }
}
