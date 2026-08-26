package com.omp.common.service;

import com.omp.common.entity.Operation;
import com.omp.common.repository.OperationRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Export periodique par domaine/periode (cf plan Phase 6) - omp-api/ReportResource et
 * omp-web/RapportsBean l'appellent tous deux directement (cf DashboardAggregationService pour la
 * meme raison : ne jamais faire dependre omp-web de omp-api). */
@Stateless
public class ReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] HEADERS = {"Code operation", "Type", "Statut", "Debut", "Fin", "Description"};

    @Inject
    private OperationRepository operationRepository;

    public byte[] generateExcel(String domainCode, LocalDateTime from, LocalDateTime to) {
        List<Operation> operations = findOperations(domainCode, from, to);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet(domainCode + " - operations");

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                header.createCell(i).setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (Operation operation : operations) {
                Row row = sheet.createRow(rowIndex++);
                writeCell(row, 0, operation.getOperationCode());
                writeCell(row, 1, operation.getOperationType().getName());
                writeCell(row, 2, operation.getStatus() != null ? operation.getStatus().getName() : "");
                writeCell(row, 3, format(operation.getStartDatetime()));
                writeCell(row, 4, format(operation.getEndDatetime()));
                writeCell(row, 5, operation.getDescription() != null ? operation.getDescription() : "");
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Echec generation Excel", e);
        }
    }

    public byte[] generatePdf(String domainCode, LocalDateTime from, LocalDateTime to) {
        List<Operation> operations = findOperations(domainCode, from, to);
        PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float y = page.getMediaBox().getHeight() - 60;
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(bold, 16);
                content.newLineAtOffset(50, y);
                content.showText("Rapport " + domainCode + " - " + format(from) + " au " + format(to));
                content.endText();
                y -= 30;

                content.setFont(regular, 10);
                for (Operation operation : operations) {
                    if (y < 50) {
                        break; // pagination multi-page laissee pour une iteration ulterieure
                    }
                    String line = operation.getOperationCode() + " | " + operation.getOperationType().getName()
                            + " | " + (operation.getStatus() != null ? operation.getStatus().getName() : "-")
                            + " | " + format(operation.getStartDatetime()) + " -> " + format(operation.getEndDatetime());
                    content.beginText();
                    content.newLineAtOffset(50, y);
                    content.showText(line);
                    content.endText();
                    y -= 16;
                }
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Echec generation PDF", e);
        }
    }

    private List<Operation> findOperations(String domainCode, LocalDateTime from, LocalDateTime to) {
        return operationRepository.findActiveBetween(from, to).stream()
                .filter(o -> domainCode == null || o.getDomain().getCode().equals(domainCode))
                .toList();
    }

    private void writeCell(Row row, int index, String value) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
    }

    private String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FMT) : "-";
    }
}
