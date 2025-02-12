package dev.bruno.banking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Transaction amount is required.")
    @Positive(message = "Transaction amount must be positive.")
    private BigDecimal amount;

    @Column(length = 255)
    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Transaction type is required.")
    private TransactionType type;

    @Column(nullable = false)
    @NotNull(message = "Transaction date is required.")
    @PastOrPresent(message = "Transaction date must be in the past or present.")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Associated user is required.")
    private User user;
}
