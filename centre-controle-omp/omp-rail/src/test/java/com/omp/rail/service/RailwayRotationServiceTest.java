package com.omp.rail.service;

import com.omp.common.entity.Domain;
import com.omp.common.entity.Operation;
import com.omp.common.entity.Status;
import com.omp.common.repository.ClientDetailRepository;
import com.omp.common.repository.ClientRepository;
import com.omp.common.repository.StatusRepository;
import com.omp.common.service.DomainService;
import com.omp.common.service.OperationService;
import com.omp.rail.entity.RailwayRotation;
import com.omp.rail.repository.RailwayRotationRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RailwayRotationServiceTest {

    @Mock
    private RailwayRotationRepository railwayRotationRepository;
    @Mock
    private OperationService operationService;
    @Mock
    private DomainService domainService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientDetailRepository clientDetailRepository;
    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private RailwayRotationService railwayRotationService;

    @Test
    void create_shouldLinkOperationAndSetAnnouncedStatus() {
        Domain domain = new Domain();
        domain.setCode("RAILWAY");
        Operation operation = new Operation();
        operation.setOperationCode("RAIL-42");
        Status announced = new Status();
        announced.setCategory("RAILWAY_ROTATION");
        announced.setCode("ANNOUNCED");

        when(domainService.findByCode("RAILWAY")).thenReturn(domain);
        when(operationService.create(anyString(), anyString(), any(), any(), any(), any(), anyString()))
                .thenReturn(operation);
        when(statusRepository.findByCategoryAndCode("RAILWAY_ROTATION", "ANNOUNCED"))
                .thenReturn(Optional.of(announced));
        when(railwayRotationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RailwayRotation result = railwayRotationService.create("42", null, null, "TR-A", "TR-R",
                "GSEZ-1", "ARISE-1", BigDecimal.valueOf(500), 20, LocalDateTime.now());

        assertEquals(operation, result.getOperation());
        assertEquals(announced, result.getStatus());
        assertEquals("42", result.getRotationNumber());
        verify(railwayRotationRepository).save(any(RailwayRotation.class));
    }
}
