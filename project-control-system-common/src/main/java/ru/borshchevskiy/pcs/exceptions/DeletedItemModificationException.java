package ru.borshchevskiy.pcs.exceptions;

public class DeletedItemModificationException extends RuntimeException {

    public DeletedItemModificationException(String message) {
        super(message);
    }

    public DeletedItemModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeletedItemModificationException(Throwable cause) {
        super(cause);
    }
}
