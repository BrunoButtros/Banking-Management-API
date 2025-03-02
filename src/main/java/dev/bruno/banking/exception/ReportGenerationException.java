package dev.bruno.banking.exception;

public class ReportGenerationException extends BusinessException {
    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
