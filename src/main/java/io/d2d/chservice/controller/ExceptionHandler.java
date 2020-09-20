package io.d2d.chservice.controller;

import io.d2d.chservice.exception.InvalidOperationException;
import io.d2d.chservice.model.rest.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public ErrorResponse exceptionHandler(Exception ex, HttpServletRequest request) {
        log.error("Exception : {}", ex);
        ErrorResponse response = new ErrorResponse();
        response.setErrorDescription(ex.getLocalizedMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(value = InvalidOperationException.class)
    public ErrorResponse invalidOperationHandler(Exception ex) {
        log.error("Exception : {}", ex);
        ErrorResponse response = new ErrorResponse();
        response.setErrorDescription(ex.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ErrorResponse badRequestHandler(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("MethodArgumentTypeMismatchException : {}", ex);
        ErrorResponse response = new ErrorResponse();
        response.setErrorDescription(ex.getLocalizedMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ErrorResponse badRequestHandler2(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException : {}", ex);
        String errorFields = ex.getBindingResult().getFieldErrors().stream()
                .map(ExceptionHandler::fieldErrorToString)
                .collect(Collectors.joining(" , "));
        ErrorResponse response = new ErrorResponse();
        response.setErrorDescription(errorFields);
        return response;
    }

    private static String fieldErrorToString(FieldError fieldError) {
        return fieldError.getField() + " : " + fieldError.getDefaultMessage();
    }

}
