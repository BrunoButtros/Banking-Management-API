package dev.bruno.banking.controller;

import dev.bruno.banking.dto.TransactionRequestDTO;
import dev.bruno.banking.dto.TransactionResponseDTO;
import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.service.ExcelTemplateService;
import dev.bruno.banking.service.TransactionImportService;
import dev.bruno.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionImportService transactionImportService;
    private final ExcelTemplateService excelTemplateService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody TransactionRequestDTO transactionRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        TransactionResponseDTO createdTransaction = transactionService.createTransaction(transactionRequest, userDetails);
        return ResponseEntity.ok(createdTransaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> findById(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.findById(id, userDetails));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<TransactionSummaryDTO>> getTransactionSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute TransactionSummaryRequestDTO requestDTO) {

        Page<TransactionSummaryDTO> summaryPage = transactionService.getTransactionSummary(requestDTO, userDetails);
        return ResponseEntity.ok(summaryPage.getContent());
    }


    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDTO transactionRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        TransactionResponseDTO updatedTransaction = transactionService.updateTransaction(id, transactionRequest, userDetails);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        transactionService.deleteTransaction(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTransactionTemplate() {
        byte[] template = excelTemplateService.getTransactionTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Transaction_Template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(template);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importTransactions(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) throws IOException {
        int totalImported = transactionImportService.importTransactions(file.getInputStream(), userId).size();
        return ResponseEntity.ok("Transactions imported successfully! Total transactions imported: " + totalImported);
    }

}
