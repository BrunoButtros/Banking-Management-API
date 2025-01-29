package dev.bruno.banking.service;


import dev.bruno.banking.dto.TransactionSummaryDto;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    private Long getAuthenticatedUserId(UserDetails userDetails) {
        // Utilizando para teste o ID de usuário fixo
        return 1L; // Simulando ID de usuário para teste
        // return userDetails.getUsername(); // Regra final para pegar o ID real do usuário
    }

    public Optional<Transaction> findById(Long id, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        return transactionRepository.findByIdAndUserId(id, userId);
    }

    public Page<TransactionSummaryDto> getTransactionSummary(Long userId, LocalDateTime startDate,
                                                             LocalDateTime endDate, TransactionType type,
                                                             int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        // Se tipo for nulo, buscar todas as transações, se não, filtra por tipo
        Page<Transaction> transactions = (type == null)
                ? transactionRepository.findByUserIdAndDateRangeAndType(userId, startDate, endDate, null, pageRequest)
                : transactionRepository.findByUserIdAndDateRangeAndType(userId, startDate, endDate, type, pageRequest);

        // Agrupando as transações por tipo e calculando o resumo
        List<TransactionSummaryDto> summaries = transactions
                .stream()
                .collect(Collectors.groupingBy(Transaction::getType))
                .entrySet()
                .stream()
                .map(entry -> new TransactionSummaryDto(
                        entry.getKey(),
                        entry.getValue().stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                        entry.getValue().size()
                ))
                .collect(Collectors.toList());

        // Retorna os resultados como uma página
        return new PageImpl<>(summaries, pageRequest, transactions.getTotalElements());
    }

    public Transaction createTransaction(Transaction transaction, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails); // Obtendo o ID do usuário autenticado
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));
        transaction.setUser(user); // Associando a transação ao usuário
        if (transaction.getType() == null) {
            throw new IllegalArgumentException("O tipo da transação não pode ser nulo");
        }
        return transactionRepository.save(transaction);
    }
}

