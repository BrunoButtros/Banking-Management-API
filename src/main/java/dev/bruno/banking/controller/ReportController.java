package dev.bruno.banking.controller;

import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> generateReport(@RequestBody TransactionSummaryRequestDTO requestDTO,
                                                 @RequestParam("format") String format,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        byte[] report = reportService.generateReport(requestDTO, userDetails, format);
        String filename = "transaction-summary." + (format.equalsIgnoreCase("PDF") ? "pdf" : "xlsx");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(report);
    }
}
