package ru.borshchevskiy.pcs.web.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.borshchevskiy.pcs.common.exceptions.*;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice(basePackages = "ru.borshchevskiy.pcs.web.controllers")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(NotFoundException exception) {
        log.error("Handled NotFoundException.", exception);
        return ResponseEntity.status(NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({DeletedItemModificationException.class})
    public ResponseEntity<String> handleDeletedItemModificationException(DeletedItemModificationException exception) {
        log.error("Handled DeletedItemModificationException.", exception);
        return ResponseEntity.status(NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({StatusModificationException.class})
    public ResponseEntity<String> handleStatusModificationException(StatusModificationException exception) {
        log.error("Handled StatusModificationException.", exception);
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        log.error("Handled UserAlreadyExistsException.", exception);
        return ResponseEntity.status(BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler({RequestDataValidationException.class})
    public ResponseEntity<String> handleRequestDataValidationException(RequestDataValidationException exception) {
        log.error("Handled RequestDataValidationException.", exception);
        return ResponseEntity.status(BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler({FileDeleteException.class})
    public ResponseEntity<String> handleFileDeleteException(FileDeleteException exception) {
        log.error("Handled FileDeleteException.", exception);
        return ResponseEntity.status(BAD_REQUEST).body(exception.getMessage());
    }

}
