package dev.bruno.banking.exception;

public class InvalidTransactionException extends BusinessException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
