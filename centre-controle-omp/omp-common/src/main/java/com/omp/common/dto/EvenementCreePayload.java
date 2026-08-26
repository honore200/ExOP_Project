package com.omp.common.dto;

import com.omp.common.enums.Severity;
import java.time.LocalDateTime;

/**
 * Payload CDI diffuse via jakarta.enterprise.event.Event<EvenementCreePayload> lors de la creation
 * d'un Event JPA. Nom volontairement distinct de l'entite "Event" (cf plan decision d'architecture
 * #5 : jakarta.enterprise.event.Event<T> et l'entite JPA Event partagent le meme nom simple).
 */
public record EvenementCreePayload(
        Long eventId,
        Long operationId,
        String operationCode,
        String eventTypeCode,
        Severity severity,
        LocalDateTime eventDatetime,
        String description
) {
}
