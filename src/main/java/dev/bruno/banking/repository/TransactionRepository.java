package dev.bruno.banking.repository;

import dev.bruno.banking.model.Transaction;
import dev.bruno.banking.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Busca por tipo de transação e e-mail
    @Query("SELECT t FROM Transaction t WHERE t.type = :type AND t.user.email = :email")
    List<Transaction> findByTypeAndUserEmail(@Param("type") TransactionType type, @Param("email") String email);

    // Busca por intervalo de datas e usuário associado
    @Query("SELECT t FROM Transaction t WHERE t.date BETWEEN :startDate AND :endDate AND t.user.email = :email")
    List<Transaction> findByDateBetweenAndUserEmail(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("email") String email);

    // Busca por intervalo de datas e tipo, com suporte a parâmetros opcionais
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
            "AND (:startDate IS NULL OR t.date >= :startDate) " +
            "AND (:endDate IS NULL OR t.date <= :endDate) " +
            "AND (:type IS NULL OR t.type = :type)")
    Page<Transaction> findByUserIdAndDateRangeAndType(Long userId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      @Param("type") TransactionType type,
                                                      Pageable pageable);

    // Método de busca por ID e usuário
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

}
