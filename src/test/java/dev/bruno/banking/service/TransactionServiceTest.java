package dev.bruno.banking.service;

import dev.bruno.banking.config.CustomUserDetails;
import dev.bruno.banking.exception.TransactionNotFoundException;
import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = mock(CustomUserDetails.class);
    }

    @Test
    void testDeleteTransaction_Success() {
        User user = new User();
        user.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(user);

        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(1L);
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(1L, userDetails);

        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    void testDeleteTransaction_TransactionNotFound() {
        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(1L); // ✅ Agora dentro do teste
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransaction(1L, userDetails)
        );

        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void testDeleteTransaction_AnotherUsersTransaction() {
        User anotherUser = new User();
        anotherUser.setId(2L);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(anotherUser);

        when(userService.getAuthenticatedUserId(userDetails)).thenReturn(1L);
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransaction(1L, userDetails)
        );

        verify(transactionRepository, never()).delete(any());
    }
}
