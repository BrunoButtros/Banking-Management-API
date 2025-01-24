package dev.bruno.banking.service;


import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
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

    private String getAuthenticatedUserEmail(UserDetails userDetails) {// Método para obter o email do usuário autenticado
        return "albion@example.com"; //Utilizando para teste

        // return userDetails.getUsername();   regra final
    }

    public Optional<Transaction> findById(Long id, UserDetails userDetails) {
        String userEmail = getAuthenticatedUserEmail(userDetails);
        return transactionRepository.findByIdAndUserEmail(id, userEmail);
    }

    public List<Transaction> findByType(TransactionType type, String userEmail) {
        return transactionRepository.findByTypeAndUserEmail(type, userEmail);
    }

    public List<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate, UserDetails userDetails) {
        String userEmail = getAuthenticatedUserEmail(userDetails);
        return transactionRepository.findByDateBetweenAndUserEmail(startDate, endDate, userEmail);
    }

    public Transaction createTransaction(Transaction transaction, UserDetails userDetails) {
        String userEmail = getAuthenticatedUserEmail(userDetails); // Obter o usuário autenticado
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail));
        transaction.setUser(user); // Associar a transação ao usuário autenticado
        if (transaction.getType() == null) {
            throw new IllegalArgumentException("O tipo da transação não pode ser nulo");
        }
        return transactionRepository.save(transaction);
    }
}
