package dev.bruno.banking.service;

import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.exception.InvalidReportFormatException;
import dev.bruno.banking.exception.ReportGenerationException;
import dev.bruno.banking.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class ReportGenerationServiceTest {

    @Spy
    @InjectMocks
    private ReportGenerationService reportGenerationService;

    private List<TransactionSummaryDTO> transactionSummaries;

    @BeforeEach
    void setUp() {
        transactionSummaries = List.of(
                new TransactionSummaryDTO(TransactionType.DEPOSIT, new BigDecimal("1000.00"), 5),
                new TransactionSummaryDTO(TransactionType.WITHDRAW, new BigDecimal("500.00"), 3)
        );
    }

    @Test
    void shouldGeneratePdfReportSuccessfully() {
        byte[] pdfReport = reportGenerationService.generateReport(transactionSummaries, "PDF");

        assertNotNull(pdfReport);
        assertTrue(pdfReport.length > 0);
    }

    @Test
    void shouldGenerateExcelReportSuccessfully() {
        byte[] excelReport = reportGenerationService.generateReport(transactionSummaries, "EXCEL");

        assertNotNull(excelReport);
        assertTrue(excelReport.length > 0);
    }

    @Test
    void shouldThrowInvalidReportFormatException_WhenFormatIsInvalid() {
        InvalidReportFormatException exception = assertThrows(InvalidReportFormatException.class,
                () -> reportGenerationService.generateReport(transactionSummaries, "TXT"));

        assertEquals("Unsupported report format: TXT", exception.getMessage());
    }

    @Test
    void shouldThrowReportGenerationException_WhenPdfFails() {
        ReportGenerationService spyService = spy(reportGenerationService);
        doThrow(new ReportGenerationException("Error generating PDF report", new RuntimeException()))
                .when(spyService).generateReport(transactionSummaries, "PDF");

        ReportGenerationException exception = assertThrows(ReportGenerationException.class,
                () -> spyService.generateReport(transactionSummaries, "PDF"));

        assertEquals("Error generating PDF report", exception.getMessage());
    }
}
