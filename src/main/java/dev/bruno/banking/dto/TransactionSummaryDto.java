package dev.bruno.banking.dto;

import dev.bruno.banking.model.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionSummaryDto {
    private TransactionType type;
    private BigDecimal totalAmount;
    private long transactionCount;

    public TransactionSummaryDto(TransactionType type, BigDecimal totalAmount, long transactionCount) {
        this.type = type;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
    }
}
