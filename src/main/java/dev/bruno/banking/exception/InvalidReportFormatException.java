package dev.bruno.banking.exception;

public class InvalidReportFormatException extends BusinessException {
    public InvalidReportFormatException(String message) {
        super(message);
    }
}
