package dev.bruno.banking.exception;

public class ExchangeRateException extends BusinessException {
    public ExchangeRateException(String message, Throwable cause) {
        super(message, cause);
    }
}
