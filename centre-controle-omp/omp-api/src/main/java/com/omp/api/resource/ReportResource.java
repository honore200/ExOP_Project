package com.omp.api.resource;

import com.omp.api.security.Secured;
import com.omp.common.service.ReportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;

@Path("/reports")
@Secured
public class ReportResource {

    @Inject
    private ReportService reportService;

    @GET
    @Path("/{domain}")
    @Produces({"application/pdf", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    public Response report(
            @jakarta.ws.rs.PathParam("domain") String domain,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("format") @jakarta.ws.rs.DefaultValue("pdf") String format) {

        LocalDateTime fromDt = from != null ? LocalDateTime.parse(from) : LocalDateTime.now().minusDays(7);
        LocalDateTime toDt = to != null ? LocalDateTime.parse(to) : LocalDateTime.now();

        byte[] content;
        String mediaType;
        String filename;
        if ("xlsx".equalsIgnoreCase(format)) {
            content = reportService.generateExcel(domain, fromDt, toDt);
            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            filename = "rapport-" + domain + ".xlsx";
        } else {
            content = reportService.generatePdf(domain, fromDt, toDt);
            mediaType = "application/pdf";
            filename = "rapport-" + domain + ".pdf";
        }

        return Response.ok(content, mediaType)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
    }
}
