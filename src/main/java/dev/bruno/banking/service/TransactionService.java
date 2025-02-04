package dev.bruno.banking.service;

import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    private Long getAuthenticatedUserId(UserDetails userDetails) {
        // Para testes
        return 5L;
    }

    public Optional<Transaction> findById(Long id, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        return transactionRepository.findByIdAndUserId(id, userId);
    }

    public Page<TransactionSummaryDTO> getTransactionSummary(LocalDateTime startDate,
                                                             LocalDateTime endDate,
                                                             TransactionType type,
                                                             int page,
                                                             int size,
                                                             UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Transaction> transactions = transactionRepository
                .findByUserIdAndDateRangeAndType(userId, startDate, endDate, type, pageRequest);

        List<TransactionSummaryDTO> summaries = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getType))
                .entrySet()
                .stream()
                .map(entry -> new TransactionSummaryDTO(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(Transaction::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        entry.getValue().size()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(summaries, pageRequest, transactions.getTotalElements());
    }

    public Page<TransactionSummaryDTO> getTransactionSummary(TransactionSummaryRequestDTO requestDTO,
                                                             UserDetails userDetails) {
        int size = 10; // Tamanho fixo da página
        return getTransactionSummary(
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                requestDTO.getTransactionType(),
                requestDTO.getPage(),
                size,
                userDetails
        );
    }

    public Transaction createTransaction(Transaction transaction, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));
        transaction.setUser(user);
        if (transaction.getType() == null) {
            throw new IllegalArgumentException("O tipo da transação não pode ser nulo");
        }
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction updatedTransaction, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        Transaction existingTransaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada para o usuário: " + id));

        if (updatedTransaction.getType() == null) {
            throw new IllegalArgumentException("O tipo da transação não pode ser nulo");
        }
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setType(updatedTransaction.getType());
        existingTransaction.setDescription(updatedTransaction.getDescription());
        return transactionRepository.save(existingTransaction);
    }

    public void deleteTransaction(Long id, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada para o usuário: " + id));
        transactionRepository.delete(transaction);
    }
}
