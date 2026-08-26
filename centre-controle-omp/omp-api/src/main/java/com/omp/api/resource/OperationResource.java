package com.omp.api.resource;

import com.omp.api.security.Secured;
import com.omp.common.dto.OperationDTO;
import com.omp.common.entity.Domain;
import com.omp.common.entity.Operation;
import com.omp.common.service.DomainService;
import com.omp.common.service.OperationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Path("/operations")
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OperationResource {

    @Inject
    private OperationService operationService;

    @Inject
    private DomainService domainService;

    @GET
    public List<OperationDTO> list(@QueryParam("domain") String domainCode) {
        List<Operation> operations = (domainCode != null)
                ? operationService.findByDomainCode(domainCode)
                : operationService.findActiveBetween(LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));
        return operations.stream().map(OperationResource::toDTO).toList();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return operationService.findById(id)
                .map(op -> Response.ok(toDTO(op)).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY", "PORT", "MAINTENANCE"})
    public Response create(Map<String, Object> body) {
        String domainCode = (String) body.get("domainCode");
        Domain domain = domainService.findByCode(domainCode);

        Operation operation = operationService.create(
                (String) body.get("operationCode"),
                (String) body.get("operationTypeCode"),
                domain,
                body.get("clientId") != null ? Long.valueOf(body.get("clientId").toString()) : null,
                body.get("locationId") != null ? Long.valueOf(body.get("locationId").toString()) : null,
                LocalDateTime.parse((String) body.get("startDatetime")),
                (String) body.get("description"));

        return Response.status(Response.Status.CREATED).entity(toDTO(operation)).build();
    }

    @PATCH
    @Path("/{id}/status")
    @RolesAllowed({"ADMIN", "CONTROL_ROOM", "RAILWAY", "PORT", "MAINTENANCE"})
    public Response changeStatus(@PathParam("id") Long id, Map<String, String> body) {
        Operation operation = operationService.changeStatus(id, body.get("statusCode"));
        return Response.ok(toDTO(operation)).build();
    }

    static OperationDTO toDTO(Operation o) {
        return new OperationDTO(
                o.getId(),
                o.getOperationCode(),
                o.getOperationType().getCode(),
                o.getDomain().getCode(),
                o.getClient() != null ? o.getClient().getCode() : null,
                o.getStatus() != null ? o.getStatus().getCode() : null,
                o.getLocation() != null ? o.getLocation().getCode() : null,
                o.getStartDatetime(),
                o.getEndDatetime(),
                o.getDescription());
    }
}
