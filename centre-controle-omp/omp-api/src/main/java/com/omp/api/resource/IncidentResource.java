package com.omp.api.resource;

import com.omp.api.security.Secured;
import com.omp.common.entity.Incident;
import com.omp.common.service.IncidentService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/incidents")
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IncidentResource {

    @Inject
    private IncidentService incidentService;

    @GET
    public List<IncidentDTO> list(@QueryParam("operationId") Long operationId,
                                   @QueryParam("open") Boolean open) {
        List<Incident> incidents = (operationId != null)
                ? incidentService.findByOperationId(operationId)
                : incidentService.findOpen();
        return incidents.stream().map(IncidentResource::toDTO).toList();
    }

    @POST
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY", "PORT", "MAINTENANCE"})
    public Response create(Map<String, String> body) {
        Incident incident = incidentService.create(
                Long.valueOf(body.get("operationId")), body.get("incidentTypeCode"), body.get("description"));
        return Response.status(Response.Status.CREATED).entity(toDTO(incident)).build();
    }

    @POST
    @Path("/{id}/resolve")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY", "PORT", "MAINTENANCE"})
    public Response resolve(@PathParam("id") Long id) {
        return Response.ok(toDTO(incidentService.resolve(id))).build();
    }

    static IncidentDTO toDTO(Incident i) {
        return new IncidentDTO(i.getId(), i.getOperation().getId(), i.getIncidentType().getCode(),
                i.getSeverity(), i.isResolved(), i.getStartDatetime(), i.getEndDatetime(), i.getDescription());
    }

    public record IncidentDTO(Long id, Long operationId, String incidentTypeCode,
                               com.omp.common.enums.Severity severity, boolean resolved,
                               java.time.LocalDateTime startDatetime, java.time.LocalDateTime endDatetime,
                               String description) {
    }
}
