package dev.bruno.banking.service;

import dev.bruno.banking.config.CustomUserDetails;
import dev.bruno.banking.dto.TransactionRequestDTO;
import dev.bruno.banking.dto.TransactionResponseDTO;
import dev.bruno.banking.exception.InvalidTransactionException;
import dev.bruno.banking.exception.TransactionNotFoundException;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import dev.bruno.banking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void testUpdateTransaction_Success() {
        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(1L);
        existingTransaction.setAmount(BigDecimal.valueOf(100));
        existingTransaction.setDescription("Old description");
        existingTransaction.setType(TransactionType.DEPOSIT);
        existingTransaction.setDate(LocalDateTime.now().minusHours(1));
        User user = new User();
        user.setId(1L);
        user.setName("user");
        existingTransaction.setUser(user);

        when(transactionRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(BigDecimal.valueOf(200));
        request.setDescription("Updated description");
        request.setType(TransactionType.DEPOSIT);
        request.setDate(LocalDateTime.now().minusMinutes(30));

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        TransactionResponseDTO response = transactionService.updateTransaction(1L, request, userDetails);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(200), response.getAmount());
        assertEquals("Updated description", response.getDescription());
        assertEquals(TransactionType.DEPOSIT, response.getType());
    }

    @Test
    void testUpdateTransaction_ThrowsInvalidTransactionException_WhenAmountIsNegative() {
        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(1L);
        existingTransaction.setAmount(BigDecimal.valueOf(100));
        existingTransaction.setDescription("Old description");
        existingTransaction.setType(TransactionType.DEPOSIT);
        existingTransaction.setDate(LocalDateTime.now().minusHours(1));
        User user = new User();
        user.setId(1L);
        existingTransaction.setUser(user);

        when(transactionRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(existingTransaction));

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(BigDecimal.valueOf(-50));
        request.setDescription("Invalid update");
        request.setType(TransactionType.DEPOSIT);
        request.setDate(LocalDateTime.now().minusMinutes(30));

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class,
                () -> transactionService.updateTransaction(1L, request, userDetails));
        assertTrue(ex.getMessage().contains("Transaction amount must be positive"));
    }

    @Test
    void testDeleteTransaction_Success() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        User user = new User();
        user.setId(1L);
        transaction.setUser(user);

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(1L, userDetails);
        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    void testDeleteTransaction_ThrowsTransactionNotFoundException() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        TransactionNotFoundException ex = assertThrows(TransactionNotFoundException.class,
                () -> transactionService.deleteTransaction(1L, userDetails));
        assertTrue(ex.getMessage().contains("Transaction not found for the user: 1"));
    }
}
