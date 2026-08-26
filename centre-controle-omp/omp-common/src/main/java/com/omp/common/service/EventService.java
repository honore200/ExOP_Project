package com.omp.common.service;

import com.omp.common.dto.EvenementCreePayload;
import com.omp.common.entity.EventType;
import com.omp.common.entity.Operation;
import com.omp.common.entity.OperationStep;
import com.omp.common.interceptor.Audited;
import com.omp.common.repository.EventRepository;
import com.omp.common.repository.EventTypeRepository;
import com.omp.common.repository.OperationRepository;
import com.omp.common.repository.OperationStepRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cree les Event et diffuse un payload CDI (jakarta.enterprise.event.Event<EvenementCreePayload>)
 * observe par EventsWebSocketEndpoint (omp-api) pour pousser la mise a jour en temps reel.
 * jakarta.enterprise.event.Event est reference en nom qualifie complet pour ne jamais entrer en
 * collision avec l'entite JPA "Event" (cf plan decision d'architecture #5).
 */
@Stateless
public class EventService {

    @Inject
    private EventRepository eventRepository;

    @Inject
    private EventTypeRepository eventTypeRepository;

    @Inject
    private OperationRepository operationRepository;

    @Inject
    private OperationStepRepository operationStepRepository;

    @Inject
    private jakarta.enterprise.event.Event<EvenementCreePayload> eventBus;

    public List<com.omp.common.entity.Event> findByOperationId(Long operationId) {
        return eventRepository.findByOperationId(operationId);
    }

    public List<com.omp.common.entity.Event> findRecent(int limit) {
        return eventRepository.findRecent(limit);
    }

    @Audited(action = "CREATE", entityType = "Event")
    public com.omp.common.entity.Event create(Long operationId, Long operationStepId, String eventTypeCode,
                                               String description) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Operation inconnue: " + operationId));
        EventType eventType = eventTypeRepository.findByCode(eventTypeCode)
                .orElseThrow(() -> new IllegalArgumentException("EventType inconnu: " + eventTypeCode));

        com.omp.common.entity.Event event = new com.omp.common.entity.Event();
        event.setOperation(operation);
        event.setEventType(eventType);
        event.setSeverity(eventType.getDefaultSeverity());
        event.setEventDatetime(LocalDateTime.now());
        event.setDescription(description);

        if (operationStepId != null) {
            OperationStep step = operationStepRepository.findById(operationStepId).orElse(null);
            event.setOperationStep(step);
        }

        com.omp.common.entity.Event saved = eventRepository.save(event);

        eventBus.fire(new EvenementCreePayload(saved.getId(), operation.getId(), operation.getOperationCode(),
                eventType.getCode(), saved.getSeverity(), saved.getEventDatetime(), saved.getDescription()));

        return saved;
    }
}
