package com.omp.web.bean;

import com.omp.common.service.ReportService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named
@ViewScoped
public class RapportsBean implements Serializable {

    @Inject
    private ReportService reportService;

    private String domainCode = "RAILWAY";
    private LocalDate from = LocalDate.now().minusDays(7);
    private LocalDate to = LocalDate.now();

    public StreamedContent downloadPdf() {
        byte[] content = reportService.generatePdf(domainCode, from.atStartOfDay(), to.atTime(23, 59));
        return DefaultStreamedContent.builder()
                .name("rapport-" + domainCode + ".pdf")
                .contentType("application/pdf")
                .stream(() -> new ByteArrayInputStream(content))
                .build();
    }

    public StreamedContent downloadExcel() {
        byte[] content = reportService.generateExcel(domainCode, from.atStartOfDay(), to.atTime(23, 59));
        return DefaultStreamedContent.builder()
                .name("rapport-" + domainCode + ".xlsx")
                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .stream(() -> new ByteArrayInputStream(content))
                .build();
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }
}
