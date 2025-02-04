package dev.bruno.banking.controller;

import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.service.ExcelTemplateService;
import dev.bruno.banking.service.TransactionImportService;
import dev.bruno.banking.service.TransactionService;
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
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionImportService transactionImportService;
    private final ExcelTemplateService excelTemplateService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody Transaction transaction,
            @AuthenticationPrincipal UserDetails userDetails) {
        Transaction createdTransaction = transactionService.createTransaction(transaction, userDetails);
        return ResponseEntity.ok(createdTransaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> findById(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Transaction> transaction = transactionService.findById(id, userDetails);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/summary")
    public ResponseEntity<Page<TransactionSummaryDTO>> getTransactionSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute TransactionSummaryRequestDTO requestDTO) {

        Page<TransactionSummaryDTO> summary = transactionService.getTransactionSummary(requestDTO, userDetails);
        return ResponseEntity.ok(summary);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id,
                                                         @RequestBody Transaction transaction,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction, userDetails);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        transactionService.deleteTransaction(id, userDetails);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTransactionTemplate() {
        byte[] template = excelTemplateService.createTransactionTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Cadastro_de_transacoes.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(template);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importTransactions(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) throws IOException {
        int totalImported = transactionImportService.importTransactions(file.getInputStream(), userId).size();
        return ResponseEntity.ok("Transações importadas com sucesso! Total de transações importadas: " + totalImported);
    }
}
