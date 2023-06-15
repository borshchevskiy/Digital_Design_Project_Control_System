package ru.borshchevskiy.pcs.common.exceptions;

public class FileDeleteException extends RuntimeException {
    public FileDeleteException(String message) {
        super(message);
    }

    public FileDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileDeleteException(Throwable cause) {
        super(cause);
    }
}
