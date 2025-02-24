package dev.bruno.banking.repository;

import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import dev.bruno.banking.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User user;
    private Transaction transaction;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("transaction");
        user.setEmail("transaction@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100.50));
        transaction.setDescription("Test transaction");
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDate(LocalDateTime.now().minusDays(1));
        transaction.setUser(user);
    }

    @Test
    void shouldSaveTransaction() {
        Transaction savedTransaction = transactionRepository.save(transaction);

        assertNotNull(savedTransaction.getId());
        assertEquals(BigDecimal.valueOf(100.50), savedTransaction.getAmount());
        assertEquals("Test transaction", savedTransaction.getDescription());
        assertEquals(TransactionType.DEPOSIT, savedTransaction.getType());
        assertEquals(user.getId(), savedTransaction.getUser().getId());
    }

    @Test
    void shouldFindTransactionByIdAndUserId() {
        Transaction savedTransaction = transactionRepository.save(transaction);

        Optional<Transaction> foundTransaction = transactionRepository.findByIdAndUserId(savedTransaction.getId(), user.getId());

        assertTrue(foundTransaction.isPresent());
        assertEquals("Test transaction", foundTransaction.get().getDescription());
    }

    @Test
    void shouldReturnEmptyWhenTransactionNotExistsForUser() {
        Optional<Transaction> foundTransaction = transactionRepository.findByIdAndUserId(999L, user.getId());

        assertTrue(foundTransaction.isEmpty());
    }

    @Test
    void shouldFindByTypeAndUserEmail() {
        transactionRepository.save(transaction);

        List<Transaction> transactions = transactionRepository.findByTypeAndUserEmail(TransactionType.DEPOSIT, "transaction@example.com");

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals("Test transaction", transactions.get(0).getDescription());
    }

    @Test
    void shouldFindByDateRangeAndUserEmail() {
        transactionRepository.save(transaction);

        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();

        List<Transaction> transactions = transactionRepository.findByDateBetweenAndUserEmail(startDate, endDate, "transaction@example.com");

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
    }

    @Test
    void shouldReturnEmptyWhenNoTransactionsInDateRange() {
        transactionRepository.save(transaction);

        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now().minusDays(5);

        List<Transaction> transactions = transactionRepository.findByDateBetweenAndUserEmail(startDate, endDate, "transaction@example.com");

        assertTrue(transactions.isEmpty());
    }

    @Test
    void shouldFindByUserIdAndDateRangeAndTypeWithPagination() {
        transactionRepository.save(transaction);

        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Transaction> transactions = transactionRepository.findByUserIdAndDateRangeAndType(user.getId(), startDate, endDate, TransactionType.DEPOSIT, pageRequest);

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.getTotalElements());
    }

    @Test
    void shouldDeleteTransaction() {
        Transaction savedTransaction = transactionRepository.save(transaction);
        transactionRepository.delete(savedTransaction);

        Optional<Transaction> deletedTransaction = transactionRepository.findById(savedTransaction.getId());

        assertTrue(deletedTransaction.isEmpty());
    }
}
