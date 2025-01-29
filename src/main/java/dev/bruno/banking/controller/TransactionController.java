package dev.bruno.banking.controller;

import dev.bruno.banking.dto.TransactionSummaryDto;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.service.ExcelTemplateService;
import dev.bruno.banking.service.TransactionImportService;
import dev.bruno.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    private TransactionImportService transactionImportService;

    @Autowired
    private ExcelTemplateService excelTemplateService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

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
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/summary")
    public ResponseEntity<Page<TransactionSummaryDto>> getTransactionSummary(
            @AuthenticationPrincipal UserDetails userDetails, // Remover o userId como parâmetro, agora é obtido do userDetails
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) TransactionType type, // Tipo opcional
            @RequestParam(defaultValue = "0") int page) {

        int size = 10; // Fixando o tamanho da página em 10

        // Verifica se startDate e endDate são nulos, e ajusta para pegar todas as transações caso não sejam passadas
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0); // Data inicial mais antiga possível
        }
        if (endDate == null) {
            endDate = LocalDateTime.now(); // Data final como o momento atual
        }

        // Extrai o ID do usuário autenticado
        Long userId = Long.parseLong(userDetails.getUsername());

        // Chama o serviço para obter o resumo das transações
        Page<TransactionSummaryDto> summary = transactionService.getTransactionSummary(userId, startDate, endDate, type, page, size);

        return ResponseEntity.ok(summary);
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
    public ResponseEntity<?> importTransactions(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        try {
            // Converte o arquivo MultipartFile para InputStream e chama o serviço de importação
            List<Transaction> transactions = transactionImportService.importTransactions(file.getInputStream(), userId);

            // Retorna a resposta com as transações importadas
            return ResponseEntity.ok("Transações importadas com sucesso! Total de transações importadas: " + transactions.size());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao processar o arquivo Excel: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: dados inválidos: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro desconhecido: " + e.getMessage());
        }
    }
}


