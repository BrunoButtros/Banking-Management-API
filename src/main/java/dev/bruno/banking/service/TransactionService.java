package dev.bruno.banking.service;


import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;
    private final UserService userService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }


    public Optional<Transaction> findById(Long id, UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return transactionRepository.findByIdAndUserEmail(id, userEmail);
    }

    public List<Transaction> findByType(String type, UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return transactionRepository.findByTypeAndUserEmail(type, userEmail);
    }

    public List<Transaction> findByDateBetween(LocalDateTime inicio, LocalDateTime fim, UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return transactionRepository.findByDateBetweenAndUserEmail(inicio, fim, userEmail);
    }

    public Transaction createTransaction(Transaction transaction, UserDetails userDetails) {
        // Obter o usuário autenticado
        String userEmail = userDetails.getUsername();
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }
}
