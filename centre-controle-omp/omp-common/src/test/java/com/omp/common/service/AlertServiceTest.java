package com.omp.common.service;

import com.omp.common.dto.AlerteCreeePayload;
import com.omp.common.entity.Alert;
import com.omp.common.entity.Event;
import com.omp.common.enums.AlertStatus;
import com.omp.common.enums.Severity;
import com.omp.common.repository.AlertRepository;
import com.omp.common.repository.EventRepository;
import com.omp.common.repository.UserRepository;
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
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private jakarta.enterprise.event.Event<AlerteCreeePayload> alertBus;

    @InjectMocks
    private AlertService alertService;

    @Test
    void createFromEvent_shouldCopySeverityFromEventAndFireCdiEvent() {
        Event event = new Event();
        event.setSeverity(Severity.CRITICAL);
        when(eventRepository.findById(5L)).thenReturn(Optional.of(event));
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Alert result = alertService.createFromEvent(5L, "SAFETY_BREACH");

        assertEquals(Severity.CRITICAL, result.getSeverity());
        assertEquals(AlertStatus.OPEN, result.getStatus());
        verify(alertBus).fire(any(AlerteCreeePayload.class));
    }

    @Test
    void acknowledge_shouldSetStatusAndAssignee() {
        Alert alert = new Alert();
        alert.setStatus(AlertStatus.OPEN);
        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new com.omp.common.entity.User()));
        when(alertRepository.update(alert)).thenReturn(alert);

        Alert result = alertService.acknowledge(1L, 2L);

        assertEquals(AlertStatus.ACKNOWLEDGED, result.getStatus());
    }
}
