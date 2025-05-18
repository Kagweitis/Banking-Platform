package com.dtb.cardservice.Exceptions.handlers;


import com.dtb.cardservice.DTOs.Responses.ExceptionResponse;
import com.dtb.cardservice.Exceptions.AlreadyExistsException;
import com.dtb.cardservice.Exceptions.BadHttpRequestException;
import com.dtb.cardservice.Exceptions.EntityNotFoundException;
import com.dtb.cardservice.Exceptions.InternalApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GeneralExceptionHandler {


    @ExceptionHandler(value = InternalApplicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ExceptionResponse internalException(HttpServletRequest req, InternalApplicationException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ExceptionResponse notFound(HttpServletRequest req, EntityNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ExceptionResponse notFound(HttpServletRequest req, NoResourceFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();
    }


    @ExceptionHandler(value = AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse alreadyExists(HttpServletRequest req, AlreadyExistsException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(value = BadHttpRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse badHttpRequest(HttpServletRequest req, BadHttpRequestException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> methodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return errors;
    }


    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ExceptionResponse illegalArgument(HttpServletRequest req, IllegalArgumentException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleMaxSizeException(HttpServletRequest req, MaxUploadSizeExceededException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message("Files' size too large. Ensure the size is below 100MB")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse missingParamsException(HttpServletRequest req, MissingServletRequestParameterException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse requestMethodNotSupportedException(HttpServletRequest req, HttpRequestMethodNotSupportedException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }


    @ExceptionHandler(value = HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handlerMethodValidationException(HttpServletRequest req, HandlerMethodValidationException exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message("Required parameters missing " + exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exception(HttpServletRequest req, Exception exception) {
        log.error(exception.getMessage(), exception);
        return ExceptionResponse.builder()
                .message("Try again later")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }


}
