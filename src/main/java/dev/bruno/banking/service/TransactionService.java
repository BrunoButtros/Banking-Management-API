package dev.bruno.banking.service;

import dev.bruno.banking.dto.TransactionRequestDTO;
import dev.bruno.banking.dto.TransactionResponseDTO;
import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    // Para fins de teste;
    private Long getAuthenticatedUserId(UserDetails userDetails) {
        return 5L;
    }

    public TransactionResponseDTO createTransaction(@Valid TransactionRequestDTO transactionRequest, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Transaction transaction = mapFromRequestDTO(transactionRequest);
        transaction.setUser(user);

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive.");
        }
        if (transaction.getDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Transaction date must be in the past or present.");
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponseDTO(savedTransaction);
    }

    public TransactionResponseDTO findById(Long id, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found for the user: " + id));
        return mapToResponseDTO(transaction);
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
        int size = 10; // Fixed page size
        return getTransactionSummary(
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                requestDTO.getTransactionType(),
                requestDTO.getPage(),
                size,
                userDetails
        );
    }

    public TransactionResponseDTO updateTransaction(Long id, @Valid TransactionRequestDTO transactionRequest, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        Transaction existingTransaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found for the user: " + id));

        existingTransaction.setAmount(transactionRequest.getAmount());
        existingTransaction.setType(transactionRequest.getType());
        existingTransaction.setDescription(transactionRequest.getDescription());
        existingTransaction.setDate(transactionRequest.getDate());

        if (existingTransaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive.");
        }
        if (existingTransaction.getDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Transaction date must be in the past or present.");
        }

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        return mapToResponseDTO(updatedTransaction);
    }

    public void deleteTransaction(Long id, UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found for the user: " + id));
        transactionRepository.delete(transaction);
    }

    private Transaction mapFromRequestDTO(TransactionRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setType(dto.getType());
        transaction.setDate(dto.getDate());
        return transaction;
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setType(transaction.getType());
        dto.setDate(transaction.getDate());
        dto.setUserName(transaction.getUser().getName());
        return dto;
    }
}
