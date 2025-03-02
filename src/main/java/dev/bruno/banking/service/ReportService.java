package dev.bruno.banking.service;

import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.exception.InvalidReportFormatException;
import dev.bruno.banking.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionService transactionService;
    private final ReportGenerationService reportGenerationService;
    private final UserService userService;

    public byte[] generateReport(TransactionSummaryRequestDTO requestDTO, UserDetails userDetails, String format) {
        Page<TransactionSummaryDTO> summaryPage = transactionService.getTransactionSummary(
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                requestDTO.getTransactionType(),
                requestDTO.getPage(),
                10,
                userDetails
        );

        List<TransactionSummaryDTO> summaries = summaryPage.getContent();

        if (summaries.isEmpty()) {
            throw new BusinessException("No transactions found for the given criteria.");
        }

        if (!format.equalsIgnoreCase("PDF") && !format.equalsIgnoreCase("EXCEL")) {
            throw new InvalidReportFormatException("Unsupported report format: " + format);
        }

        return reportGenerationService.generateReport(summaries, format);
    }
}
