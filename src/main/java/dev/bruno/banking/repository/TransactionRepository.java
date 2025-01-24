package dev.bruno.banking.repository;

import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.type = :type AND t.user.email = :email")
    List<Transaction> findByTypeAndUserEmail(@Param("type") TransactionType type, @Param("email") String email);

    // Busca por intervalo de datas filtrado pelo usuário
    @Query("SELECT t FROM Transaction t WHERE t.date BETWEEN :startDate AND :endDate AND t.user.email = :email")
    List<Transaction> findByDateBetweenAndUserEmail(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("email") String email);

    // Busca por ID, garantindo que pertence ao usuário autenticado
    @Query("SELECT t FROM Transaction t WHERE t.id = :id AND t.user.email = :email")
    Optional<Transaction> findByIdAndUserEmail(@Param("id") Long id, @Param("email") String email);
}
