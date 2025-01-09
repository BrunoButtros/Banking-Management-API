package com.banking.management.banking_management_api.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String tipo; //"DEPÓSITO", "RETIRADA"

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();



}
