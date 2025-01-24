package dev.bruno.banking.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
    @NotNull(message = "O valor da transação é obrigatório.")
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)//Armazena o valor do enum ao banco de dados
    @Column(nullable = false)
    private TransactionType type; //"DEPÓSITO", "RETIRADA"

    @Column(nullable = false)
    @NotNull(message = "A data da transação é obrigatória.")
    @PastOrPresent(message = "Deve ser uma data do passado ou presente")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
