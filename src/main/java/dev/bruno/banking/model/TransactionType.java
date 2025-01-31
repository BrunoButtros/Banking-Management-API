package dev.bruno.banking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Objects;

public enum TransactionType {
    DEPOSIT("deposit"),
    WITHDRAW("withdraw"),
    PIX("pix"),
    TRANSFER("transfer");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static TransactionType fromValue(String value) {
        if (Objects.isNull(value))
            return null;
        return Arrays.stream(TransactionType.values())
                .filter(transactionType -> transactionType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction type: " + value));
    }
}