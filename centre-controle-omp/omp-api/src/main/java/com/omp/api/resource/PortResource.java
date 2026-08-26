package com.omp.api.resource;

import com.omp.api.security.Secured;
import com.omp.port.entity.PortCall;
import com.omp.port.entity.PortCargoOperation;
import com.omp.port.enums.CargoDirection;
import com.omp.port.service.PortCallService;
import com.omp.port.service.PortCargoOperationService;
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

@Path("/port")
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PortResource {

    @Inject
    private PortCallService portCallService;

    @Inject
    private PortCargoOperationService portCargoOperationService;

    @GET
    @Path("/calls")
    public List<PortCallDTO> listCalls(@QueryParam("atBerth") Boolean atBerth,
                                        @QueryParam("limit") @jakarta.ws.rs.DefaultValue("50") int limit) {
        List<PortCall> calls = Boolean.TRUE.equals(atBerth)
                ? portCallService.findAtBerth()
                : portCallService.findRecent(limit);
        return calls.stream().map(PortResource::toDTO).toList();
    }

    @POST
    @Path("/calls")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "PORT"})
    public Response createCall(Map<String, Object> body) {
        PortCall portCall = portCallService.create(
                (String) body.get("operationTypeCode"),
                (String) body.get("imoNumber"),
                (String) body.get("vesselName"),
                (String) body.get("flag"),
                (String) body.get("vesselType"),
                body.get("lengthM") != null ? new BigDecimal(body.get("lengthM").toString()) : null,
                body.get("grossTonnage") != null ? new BigDecimal(body.get("grossTonnage").toString()) : null,
                body.get("quayLocationId") != null ? Long.valueOf(body.get("quayLocationId").toString()) : null,
                body.get("etaDatetime") != null ? LocalDateTime.parse((String) body.get("etaDatetime")) : null,
                body.get("declaredTonnage") != null ? new BigDecimal(body.get("declaredTonnage").toString()) : null);
        return Response.status(Response.Status.CREATED).entity(toDTO(portCall)).build();
    }

    @POST
    @Path("/calls/{id}/berthing")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "PORT"})
    public Response recordBerthing(@PathParam("id") Long id) {
        return Response.ok(toDTO(portCallService.recordBerthing(id))).build();
    }

    @POST
    @Path("/calls/{id}/departure")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "PORT"})
    public Response recordDeparture(@PathParam("id") Long id) {
        return Response.ok(toDTO(portCallService.recordDeparture(id))).build();
    }

    @POST
    @Path("/calls/{id}/cargo")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "PORT"})
    public Response addCargo(@PathParam("id") Long id, Map<String, Object> body) {
        PortCargoOperation cargo = portCargoOperationService.create(
                id,
                (String) body.get("cargoTypeCode"),
                CargoDirection.valueOf((String) body.get("direction")),
                body.get("tonnage") != null ? new BigDecimal(body.get("tonnage").toString()) : null,
                body.get("containerCount") != null ? Integer.valueOf(body.get("containerCount").toString()) : null);
        return Response.status(Response.Status.CREATED).entity(toDTO(cargo)).build();
    }

    static PortCallDTO toDTO(PortCall p) {
        return new PortCallDTO(p.getId(), p.getOperation().getId(), p.getVessel().getAssetName(),
                p.getVessel().getAssetCode(), p.getQuay() != null ? p.getQuay().getCode() : null,
                p.getEtaDatetime(), p.getAtaDatetime(), p.getEtdDatetime(), p.getAtdDatetime(),
                p.getDeclaredTonnage(), p.getStatus() != null ? p.getStatus().getCode() : null);
    }

    static CargoDTO toDTO(PortCargoOperation c) {
        return new CargoDTO(c.getId(), c.getPortCall().getId(), c.getCargoType().getCode(),
                c.getDirection(), c.getTonnage(), c.getContainerCount());
    }

    public record PortCallDTO(Long id, Long operationId, String vesselName, String vesselCode, String quayCode,
                               LocalDateTime etaDatetime, LocalDateTime ataDatetime, LocalDateTime etdDatetime,
                               LocalDateTime atdDatetime, BigDecimal declaredTonnage, String statusCode) {
    }

    public record CargoDTO(Long id, Long portCallId, String cargoTypeCode, CargoDirection direction,
                            BigDecimal tonnage, Integer containerCount) {
    }
}
