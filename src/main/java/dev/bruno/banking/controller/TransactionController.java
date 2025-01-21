package dev.bruno.banking.controller;

import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction,
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
    public ResponseEntity<List<Transaction>> findByType(@RequestParam String type,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        List<Transaction> transactions = transactionService.findByType(type, userDetails);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/dates")
    public ResponseEntity<List<Transaction>> findByDateBetween(@RequestParam LocalDateTime inicio,
                                                               @RequestParam LocalDateTime fim,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        List<Transaction> transactions = transactionService.findByDateBetween(inicio, fim, userDetails);
        return ResponseEntity.ok(transactions);
    }
}
