package dev.bruno.banking.dto;

import dev.bruno.banking.model.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponseDTO {
    private Long id;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private LocalDateTime date;
    private String userName;
}
