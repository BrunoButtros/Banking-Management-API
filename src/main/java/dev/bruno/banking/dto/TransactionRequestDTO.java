package dev.bruno.banking.dto;

import dev.bruno.banking.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionRequestDTO {

    @NotNull(message = "The transaction amount is required.")
    @Positive(message = "The transaction amount must be positive.")
    private BigDecimal amount;

    @Size(max = 255, message = "The description cannot exceed 255 characters.")
    private String description;

    @NotNull(message = "The transaction type is required.")
    private TransactionType type;

    @NotNull(message = "The transaction date is required.")
    @PastOrPresent(message = "The date must be in the past or present.")
    private LocalDateTime date;
}
