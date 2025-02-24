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

    List<Transaction> findByTypeAndUserEmail(@Param("type") TransactionType type, @Param("email") String email);

    List<Transaction> findByDateBetweenAndUserEmail(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("email") String email);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
            "AND (:startDate IS NULL OR t.date >= :startDate) " +
            "AND (:endDate IS NULL OR t.date <= :endDate) " +
            "AND (:type IS NULL OR t.type = :type)")
    Page<Transaction> findByUserIdAndDateRangeAndType(Long userId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      @Param("type") TransactionType type,
                                                      Pageable pageable);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

}
