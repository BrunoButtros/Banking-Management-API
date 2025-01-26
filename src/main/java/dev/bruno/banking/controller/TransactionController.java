package dev.bruno.banking.controller;

import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.service.ExcelTemplateService;
import dev.bruno.banking.service.TransactionImportService;
import dev.bruno.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // ID / TYPE / DATE

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> findById(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Transaction> transaction = transactionService.findById(id, userDetails);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/type")
    public ResponseEntity<List<Transaction>> findByType(
            @RequestParam TransactionType type,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername(); // Pega o e-mail do método getUsername()
        List<Transaction> transactions = transactionService.findByType(type, userEmail);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/dates")
    public ResponseEntity<List<Transaction>> findByDateBetween(@RequestParam LocalDateTime inicio,
                                                               @RequestParam LocalDateTime fim,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        List<Transaction> transactions = transactionService.findByDateBetween(inicio, fim, userDetails);
        return ResponseEntity.ok(transactions);
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
            // Retorna um erro em caso de falha na leitura do arquivo
            return ResponseEntity.status(500).body("Erro ao processar o arquivo Excel: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Retorna um erro caso algum dado inválido seja encontrado no arquivo
            return ResponseEntity.badRequest().body("Erro: dados inválidos: " + e.getMessage());
        } catch (Exception e) {
            // Retorna um erro genérico
            return ResponseEntity.status(500).body("Erro desconhecido: " + e.getMessage());
        }
    }

}


