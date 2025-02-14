package dev.bruno.banking.exception;

public class TransactionNotFoundException extends BusinessException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
