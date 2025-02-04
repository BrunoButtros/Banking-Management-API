package dev.bruno.banking.dto;

import dev.bruno.banking.model.TransactionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionSummaryRequestDTO {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private String type;
    private int page = 0;

    public TransactionType getTransactionType() {
        return (type != null && !type.trim().isEmpty())
                ? TransactionType.fromValue(type)
                : null;
    }
}
