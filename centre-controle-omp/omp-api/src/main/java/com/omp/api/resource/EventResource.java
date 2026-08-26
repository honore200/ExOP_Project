package com.omp.api.resource;

import com.omp.api.security.Secured;
import com.omp.common.entity.Event;
import com.omp.common.service.EventService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/events")
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    private EventService eventService;

    @GET
    public List<EventDTO> list(@QueryParam("operationId") Long operationId,
                                @QueryParam("limit") @jakarta.ws.rs.DefaultValue("50") int limit) {
        List<Event> events = (operationId != null)
                ? eventService.findByOperationId(operationId)
                : eventService.findRecent(limit);
        return events.stream().map(EventResource::toDTO).toList();
    }

    @POST
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY", "PORT", "MAINTENANCE"})
    public Response create(Map<String, Object> body) {
        Event event = eventService.create(
                Long.valueOf(body.get("operationId").toString()),
                body.get("operationStepId") != null ? Long.valueOf(body.get("operationStepId").toString()) : null,
                (String) body.get("eventTypeCode"),
                (String) body.get("description"));
        return Response.status(Response.Status.CREATED).entity(toDTO(event)).build();
    }

    static EventDTO toDTO(Event e) {
        return new EventDTO(e.getId(), e.getOperation().getId(), e.getEventType().getCode(),
                e.getSeverity(), e.getEventDatetime(), e.getDescription());
    }

    public record EventDTO(Long id, Long operationId, String eventTypeCode,
                            com.omp.common.enums.Severity severity,
                            java.time.LocalDateTime eventDatetime, String description) {
    }
}
