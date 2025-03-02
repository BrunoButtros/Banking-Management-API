package dev.bruno.banking.service;

import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.exception.InvalidReportFormatException;
import dev.bruno.banking.exception.ReportGenerationException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportGenerationService {

    public byte[] generateReport(List<TransactionSummaryDTO> summaries, String format) {
        if (format.equalsIgnoreCase("PDF")) {
            return generatePdfReport(summaries);
        } else if (format.equalsIgnoreCase("EXCEL")) {
            return generateExcelReport(summaries);
        } else {
            throw new InvalidReportFormatException("Unsupported report format: " + format);
        }
    }

    private byte[] generateExcelReport(List<TransactionSummaryDTO> summaries) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Transaction Summary");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Transaction Type", "Total Amount", "Transaction Count"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            int rowIdx = 1;
            for (TransactionSummaryDTO dto : summaries) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getType().toString());
                row.createCell(1).setCellValue(dto.getTotalAmount().doubleValue());
                row.createCell(2).setCellValue(dto.getTransactionCount());
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ReportGenerationException("Error generating Excel report", e);
        }
    }

    private byte[] generatePdfReport(List<TransactionSummaryDTO> summaries) {
        Document document = new Document();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("Transaction Summary Report", titleFont));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(3);
            table.addCell("Transaction Type");
            table.addCell("Total Amount");
            table.addCell("Transaction Count");
            for (TransactionSummaryDTO dto : summaries) {
                table.addCell(dto.getType().toString());
                table.addCell(dto.getTotalAmount().toString());
                table.addCell(String.valueOf(dto.getTransactionCount()));
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new ReportGenerationException("Error generating PDF report", e);
        }
    }
}
