package ru.borshchevskiy.pcs.common.exceptions;

public class RequestDataValidationException extends RuntimeException {

    public RequestDataValidationException(String message) {
        super(message);
    }

    public RequestDataValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestDataValidationException(Throwable cause) {
        super(cause);
    }
}
