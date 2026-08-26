package com.omp.api.resource;

import com.omp.api.security.CurrentUserResolver;
import com.omp.api.security.Secured;
import com.omp.common.entity.Alert;
import com.omp.common.enums.AlertStatus;
import com.omp.common.service.AlertService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/alerts")
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AlertResource {

    @Inject
    private AlertService alertService;

    @Inject
    private CurrentUserResolver currentUserResolver;

    @GET
    public List<AlertDTO> list(@QueryParam("status") @jakarta.ws.rs.DefaultValue("OPEN") String status) {
        return alertService.findByStatus(AlertStatus.valueOf(status)).stream()
                .map(AlertResource::toDTO)
                .toList();
    }

    @POST
    @Path("/{id}/acquitter")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY", "PORT", "MAINTENANCE"})
    public Response acknowledge(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long userId = currentUserResolver.resolveUserId(securityContext);
        return Response.ok(toDTO(alertService.acknowledge(id, userId))).build();
    }

    @POST
    @Path("/{id}/resolve")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY", "PORT", "MAINTENANCE"})
    public Response resolve(@PathParam("id") Long id) {
        return Response.ok(toDTO(alertService.resolve(id))).build();
    }

    static AlertDTO toDTO(Alert a) {
        return new AlertDTO(a.getId(), a.getEvent().getId(), a.getAlertType(), a.getSeverity(),
                a.getStatus(), a.getCreatedAt(), a.getAcknowledgedAt(), a.getResolvedAt());
    }

    public record AlertDTO(Long id, Long eventId, String alertType, com.omp.common.enums.Severity severity,
                            AlertStatus status, java.time.LocalDateTime createdAt,
                            java.time.LocalDateTime acknowledgedAt, java.time.LocalDateTime resolvedAt) {
    }
}
