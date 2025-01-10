package dev.bruno.banking.repository;

import dev.bruno.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByType(String type); // Busca por tipo de transação

    List<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate); // Busca por intervalo de datas
}
