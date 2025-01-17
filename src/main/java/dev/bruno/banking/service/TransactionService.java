package dev.bruno.banking.service;


import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> findByType(String type) {
        return transactionRepository.findByType(type);
    }

    public List<Transaction> findByDateBetween(LocalDateTime inicio, LocalDateTime fim) {
        return transactionRepository.findByDateBetween(inicio, fim);
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

}
