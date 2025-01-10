package dev.bruno.banking.controller;

import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;


    // ID / TYPE / DATE

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> findById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.findById(id);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/type")
    public ResponseEntity<List<Transaction>> FindByType(@RequestParam String type) {
        List<Transaction> transacoes = transactionService.findByType(type);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/dates")
    public ResponseEntity<List<Transaction>> findByDataBetween(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        List<Transaction> transactions = transactionService.findByDateBetween(inicio, fim);
        return ResponseEntity.ok(transactions);
    }
}
