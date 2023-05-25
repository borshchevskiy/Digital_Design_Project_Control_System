package ru.borshchevskiy.pcs.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.borshchevskiy.pcs.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice(basePackages = "ru.borshchevskiy.pcs.controllers")
public class RestExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({DeletedItemModificationException.class})
    public ResponseEntity<String> handleNotFoundException(DeletedItemModificationException exception) {
        return ResponseEntity.status(NOT_FOUND).body(exception.getMessage());
    }
}
