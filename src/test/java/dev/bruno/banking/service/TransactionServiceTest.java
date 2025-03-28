package dev.bruno.banking.service;

import dev.bruno.banking.config.CustomUserDetails;
import dev.bruno.banking.dto.TransactionRequestDTO;
import dev.bruno.banking.dto.TransactionResponseDTO;
import dev.bruno.banking.dto.TransactionSummaryDTO;
import dev.bruno.banking.dto.TransactionSummaryRequestDTO;
import dev.bruno.banking.exception.BusinessException;
import dev.bruno.banking.exception.InvalidTransactionException;
import dev.bruno.banking.exception.TransactionNotFoundException;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import dev.bruno.banking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private final Long userId = 1L;
    private final Long transactionId = 10L;
    private User user;
    private Transaction transaction;
    private TransactionRequestDTO validRequest;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setName("User");

        userDetails = new CustomUserDetails(user);

        transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setUser(user);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDate(LocalDateTime.now().minusDays(1));

        validRequest = new TransactionRequestDTO();
        validRequest.setAmount(BigDecimal.valueOf(100));
        validRequest.setType(TransactionType.DEPOSIT);
        validRequest.setDate(LocalDateTime.now().minusHours(1));
        validRequest.setDescription("Test Transaction");
    }

    @Test
    void createTransaction_Success() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponseDTO response = transactionService.createTransaction(validRequest, userDetails);

        assertNotNull(response);
        assertEquals(transactionId, response.getId());
        assertEquals(user.getName(), response.getUserName());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_UserNotFound() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                transactionService.createTransaction(validRequest, userDetails)
        );
    }

    @Test
    void createTransaction_AmountNotPositive() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        validRequest.setAmount(BigDecimal.ZERO);

        assertThrows(InvalidTransactionException.class, () ->
                transactionService.createTransaction(validRequest, userDetails)
        );
    }

    @Test
    void createTransaction_DateNull() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        validRequest.setDate(null);

        assertThrows(InvalidTransactionException.class, () ->
                transactionService.createTransaction(validRequest, userDetails)
        );
    }

    @Test
    void createTransaction_DateInFuture() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        validRequest.setDate(LocalDateTime.now().plusDays(1));

        assertThrows(InvalidTransactionException.class, () ->
                transactionService.createTransaction(validRequest, userDetails)
        );
    }

    @Test
    void findById_Success() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(transactionId, userId))
                .thenReturn(Optional.of(transaction));

        TransactionResponseDTO response = transactionService.findById(transactionId, userDetails);

        assertNotNull(response);
        assertEquals(transactionId, response.getId());
        assertEquals(user.getName(), response.getUserName());
    }

    @Test
    void findById_NotFound() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.findById(transactionId, userDetails)
        );
    }

    @Test
    void updateTransaction_Success() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(transactionId, userId))
                .thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(transactionId);
            return t;
        });

        validRequest.setDescription("Updated");
        validRequest.setType(TransactionType.WITHDRAW);
        TransactionResponseDTO response = transactionService.updateTransaction(transactionId, validRequest, userDetails);

        assertNotNull(response);
        assertEquals(transactionId, response.getId());
        assertEquals("Updated", response.getDescription());
        assertEquals(TransactionType.WITHDRAW, response.getType());
        assertEquals(user.getName(), response.getUserName());
    }

    @Test
    void updateTransaction_NotFound() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.updateTransaction(transactionId, validRequest, userDetails)
        );
    }

    @Test
    void updateTransaction_AmountNotPositive() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(transactionId, userId))
                .thenReturn(Optional.of(transaction));
        validRequest.setAmount(BigDecimal.ZERO);

        assertThrows(InvalidTransactionException.class, () ->
                transactionService.updateTransaction(transactionId, validRequest, userDetails)
        );
    }

    @Test
    void updateTransaction_DateNull() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(transactionId, userId))
                .thenReturn(Optional.of(transaction));
        validRequest.setDate(null);

        assertThrows(InvalidTransactionException.class, () ->
                transactionService.updateTransaction(transactionId, validRequest, userDetails)
        );
    }

    @Test
    void updateTransaction_DateInFuture() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(transactionId, userId))
                .thenReturn(Optional.of(transaction));
        validRequest.setDate(LocalDateTime.now().plusDays(1));

        assertThrows(InvalidTransactionException.class, () ->
                transactionService.updateTransaction(transactionId, validRequest, userDetails)
        );
    }

    @Test
    void getTransactionSummary_Empty() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByUserIdAndDateRangeAndType(eq(userId), any(), any(), any(), any()))
                .thenReturn(Page.empty());

        Page<TransactionSummaryDTO> result = transactionService.getTransactionSummary(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now(),
                null,
                0,
                10,
                userDetails
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionSummary_NonEmpty() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);

        Transaction t1 = new Transaction();
        t1.setType(TransactionType.DEPOSIT);
        t1.setAmount(BigDecimal.valueOf(100));

        Transaction t2 = new Transaction();
        t2.setType(TransactionType.DEPOSIT);
        t2.setAmount(BigDecimal.valueOf(50));

        Transaction t3 = new Transaction();
        t3.setType(TransactionType.WITHDRAW);
        t3.setAmount(BigDecimal.valueOf(30));

        when(transactionRepository.findByUserIdAndDateRangeAndType(eq(userId), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(t1, t2, t3)));

        Page<TransactionSummaryDTO> result = transactionService.getTransactionSummary(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now(),
                null,
                0,
                10,
                userDetails
        );

        assertFalse(result.isEmpty());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void getTransactionSummary_RequestDTO() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByUserIdAndDateRangeAndType(eq(userId), any(), any(), any(), any()))
                .thenReturn(Page.empty());

        TransactionSummaryRequestDTO dto = new TransactionSummaryRequestDTO();
        dto.setPage(0);
        dto.setStartDate(LocalDateTime.now().minusDays(1));
        dto.setEndDate(LocalDateTime.now());
        dto.setType(String.valueOf(TransactionType.DEPOSIT));

        Page<TransactionSummaryDTO> result = transactionService.getTransactionSummary(dto, userDetails);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteTransaction_Success() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(transactionId, userId))
                .thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(transactionId, userDetails);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransaction_NotFound() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransaction(transactionId, userDetails)
        );
        verify(transactionRepository, never()).delete(any());
    }
}
