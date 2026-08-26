package com.omp.common.service;

import com.omp.common.dto.AlerteCreeePayload;
import com.omp.common.entity.Alert;
import com.omp.common.entity.Event;
import com.omp.common.entity.User;
import com.omp.common.enums.AlertStatus;
import com.omp.common.interceptor.Audited;
import com.omp.common.repository.AlertRepository;
import com.omp.common.repository.EventRepository;
import com.omp.common.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class AlertService {

    @Inject
    private AlertRepository alertRepository;

    @Inject
    private EventRepository eventRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private jakarta.enterprise.event.Event<AlerteCreeePayload> alertBus;

    public List<Alert> findByStatus(AlertStatus status) {
        return alertRepository.findByStatus(status);
    }

    @Audited(action = "CREATE", entityType = "Alert")
    public Alert createFromEvent(Long eventId, String alertType) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event inconnu: " + eventId));

        Alert alert = new Alert();
        alert.setEvent(event);
        alert.setAlertType(alertType);
        alert.setSeverity(event.getSeverity());
        alert.setStatus(AlertStatus.OPEN);

        Alert saved = alertRepository.save(alert);
        alertBus.fire(new AlerteCreeePayload(saved.getId(), event.getId(), alertType, saved.getSeverity()));
        return saved;
    }

    @Audited(action = "ACKNOWLEDGE", entityType = "Alert")
    public Alert acknowledge(Long alertId, Long userId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert inconnue: " + alertId));
        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        alert.setAcknowledgedAt(LocalDateTime.now());
        User user = userRepository.findById(userId).orElse(null);
        alert.setAssignedTo(user);
        return alertRepository.update(alert);
    }

    @Audited(action = "RESOLVE", entityType = "Alert")
    public Alert resolve(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert inconnue: " + alertId));
        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        return alertRepository.update(alert);
    }
}
