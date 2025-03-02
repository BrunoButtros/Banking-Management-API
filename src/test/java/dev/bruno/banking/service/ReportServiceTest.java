package dev.bruno.banking.service;

import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.exception.BusinessException;
import dev.bruno.banking.exception.InvalidReportFormatException;
import dev.bruno.banking.exception.ReportGenerationException;
import dev.bruno.banking.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ReportGenerationService reportGenerationService;

    @Mock
    private UserDetails userDetails;

    private TransactionSummaryRequestDTO requestDTO;
    private List<TransactionSummaryDTO> summaries;

    @BeforeEach
    void setUp() {
        requestDTO = new TransactionSummaryRequestDTO();
        requestDTO.setStartDate(LocalDateTime.parse("2024-01-01T00:00:00"));
        requestDTO.setEndDate(LocalDateTime.parse("2024-01-31T23:59:59"));
        requestDTO.setType(TransactionType.DEPOSIT.name());
        requestDTO.setPage(0);

        summaries = List.of(new TransactionSummaryDTO(TransactionType.DEPOSIT, new BigDecimal("1000.00"), 5));
    }

    @Test
    void shouldGenerateReportSuccessfully_WhenValidPDFFormat() {
        Page<TransactionSummaryDTO> page = new PageImpl<>(summaries);
        when(transactionService.getTransactionSummary(any(), any(), eq(TransactionType.DEPOSIT), anyInt(), anyInt(), any())).thenReturn(page);
        when(reportGenerationService.generateReport(summaries, "PDF")).thenReturn(new byte[]{1, 2, 3});

        byte[] report = reportService.generateReport(requestDTO, userDetails, "PDF");
        assertNotNull(report);
        assertTrue(report.length > 0);
    }

    @Test
    void shouldThrowBusinessException_WhenNoTransactionsFound() {
        Page<TransactionSummaryDTO> emptyPage = new PageImpl<>(Collections.emptyList());
        when(transactionService.getTransactionSummary(any(), any(), eq(TransactionType.DEPOSIT), anyInt(), anyInt(), any())).thenReturn(emptyPage);

        assertThrows(BusinessException.class, () ->
                        reportService.generateReport(requestDTO, userDetails, "PDF"),
                "No transactions found for the given criteria.");
    }

    @Test
    void shouldThrowInvalidReportFormatException_WhenFormatIsInvalid() {
        Page<TransactionSummaryDTO> page = new PageImpl<>(summaries);
        when(transactionService.getTransactionSummary(any(), any(), eq(TransactionType.DEPOSIT), anyInt(), anyInt(), any())).thenReturn(page);

        assertThrows(InvalidReportFormatException.class, () ->
                        reportService.generateReport(requestDTO, userDetails, "TXT"),
                "Unsupported report format: TXT");
    }
}
