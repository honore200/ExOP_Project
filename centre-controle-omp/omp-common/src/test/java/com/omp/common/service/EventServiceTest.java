package com.omp.common.service;

import com.omp.common.dto.EvenementCreePayload;
import com.omp.common.entity.Domain;
import com.omp.common.entity.EventType;
import com.omp.common.entity.Operation;
import com.omp.common.enums.Severity;
import com.omp.common.repository.EventRepository;
import com.omp.common.repository.EventTypeRepository;
import com.omp.common.repository.OperationRepository;
import com.omp.common.repository.OperationStepRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventTypeRepository eventTypeRepository;
    @Mock
    private OperationRepository operationRepository;
    @Mock
    private OperationStepRepository operationStepRepository;
    @Mock
    private jakarta.enterprise.event.Event<EvenementCreePayload> eventBus;

    @InjectMocks
    private EventService eventService;

    @Test
    void create_shouldPersistAndFireCdiEvent() {
        Domain domain = new Domain();
        domain.setCode("PORT");
        Operation operation = new Operation();
        operation.setOperationCode("PORT-001");
        operation.setDomain(domain);

        EventType eventType = new EventType();
        eventType.setCode("SHIP_ARRIVAL");
        eventType.setDefaultSeverity(Severity.INFO);

        when(operationRepository.findById(10L)).thenReturn(Optional.of(operation));
        when(eventTypeRepository.findByCode("SHIP_ARRIVAL")).thenReturn(Optional.of(eventType));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        com.omp.common.entity.Event result = eventService.create(10L, null, "SHIP_ARRIVAL", "Arrivee constatee");

        assertEquals(Severity.INFO, result.getSeverity());
        assertEquals(eventType, result.getEventType());
        verify(eventBus).fire(any(EvenementCreePayload.class));
    }
}
