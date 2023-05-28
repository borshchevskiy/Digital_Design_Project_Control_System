package ru.borshchevskiy.pcs.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.borshchevskiy.pcs.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.exceptions.StatusModificationException;
import ru.borshchevskiy.pcs.exceptions.UserAlreadyExistsException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice(basePackages = "ru.borshchevskiy.pcs.controllers")
public class RestExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({DeletedItemModificationException.class})
    public ResponseEntity<String> handleDeletedItemModificationException(DeletedItemModificationException exception) {
        return ResponseEntity.status(NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({StatusModificationException.class})
    public ResponseEntity<String> handleStatusModificationException(StatusModificationException exception) {
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return ResponseEntity.status(BAD_REQUEST).body(exception.getMessage());
    }

}
