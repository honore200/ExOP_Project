package com.omp.api.resource;

import com.omp.api.security.Secured;
import com.omp.rail.entity.RailwayRotation;
import com.omp.rail.service.RailwayRotationService;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Path("/rail")
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RailResource {

    @Inject
    private RailwayRotationService railwayRotationService;

    @GET
    @Path("/rotations")
    public List<RotationDTO> listRotations(@QueryParam("limit") @jakarta.ws.rs.DefaultValue("50") int limit) {
        return railwayRotationService.findRecent(limit).stream().map(RailResource::toDTO).toList();
    }

    @POST
    @Path("/rotations")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY"})
    public Response createRotation(Map<String, Object> body) {
        RailwayRotation rotation = railwayRotationService.create(
                (String) body.get("rotationNumber"),
                body.get("clientId") != null ? Long.valueOf(body.get("clientId").toString()) : null,
                body.get("clientDetailId") != null ? Long.valueOf(body.get("clientDetailId").toString()) : null,
                (String) body.get("trainArrivalNumber"),
                (String) body.get("trainReturnNumber"),
                (String) body.get("trainCodeGsez"),
                (String) body.get("trainCodeArise"),
                body.get("declaredTonnage") != null ? new BigDecimal(body.get("declaredTonnage").toString()) : null,
                body.get("wagonCount") != null ? Integer.valueOf(body.get("wagonCount").toString()) : null,
                body.get("announcedDepartureDatetime") != null
                        ? LocalDateTime.parse((String) body.get("announcedDepartureDatetime")) : null);
        return Response.status(Response.Status.CREATED).entity(toDTO(rotation)).build();
    }

    @POST
    @Path("/rotations/{id}/arrival")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY"})
    public Response recordArrival(@PathParam("id") Long id) {
        return Response.ok(toDTO(railwayRotationService.recordArrival(id, LocalDateTime.now()))).build();
    }

    @POST
    @Path("/rotations/{id}/departure")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY"})
    public Response recordDeparture(@PathParam("id") Long id, Map<String, Object> body) {
        BigDecimal tonnage = body.get("tonnage") != null ? new BigDecimal(body.get("tonnage").toString()) : null;
        return Response.ok(toDTO(railwayRotationService.recordDeparture(id, LocalDateTime.now(), tonnage))).build();
    }

    static RotationDTO toDTO(RailwayRotation r) {
        return new RotationDTO(r.getId(), r.getOperation().getId(), r.getRotationNumber(),
                r.getTrainArrivalNumber(), r.getTrainReturnNumber(), r.getWagonCount(), r.getTonnage(),
                r.getDeclaredTonnage(), r.getArrivalDatetime(), r.getDepartureDatetime(),
                r.getStatus() != null ? r.getStatus().getCode() : null);
    }

    public record RotationDTO(Long id, Long operationId, String rotationNumber, String trainArrivalNumber,
                               String trainReturnNumber, Integer wagonCount, BigDecimal tonnage,
                               BigDecimal declaredTonnage, LocalDateTime arrivalDatetime,
                               LocalDateTime departureDatetime, String statusCode) {
    }
}
