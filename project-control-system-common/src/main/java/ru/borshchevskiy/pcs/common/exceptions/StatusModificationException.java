package ru.borshchevskiy.pcs.common.exceptions;

public class StatusModificationException extends RuntimeException {

    public StatusModificationException(String message) {
        super(message);
    }

    public StatusModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusModificationException(Throwable cause) {
        super(cause);
    }
}
