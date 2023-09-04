package com.simon.smile.common.exception;

import com.simon.smile.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
public class ExceptionHandleAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleNotFoundException(ObjectNotFoundException e) {
        return Result.fail()
                .setCode(HttpStatus.NOT_FOUND.value())
                .setMessage(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = e.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        objectError -> ((FieldError) objectError).getField(),
                        ObjectError::getDefaultMessage
                ));
        return Result.fail()
                .setCode(HttpStatus.BAD_REQUEST.value())
                .setMessage("Provided arguments are invalid, set data for details")
                .setData(errorMap);
    }

    // Fallback handles any unhandled exceptions.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Result handleOtherException(Exception ex) {
        return Result.fail()
                .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage("A server internal error occurs")
                .setData(ex.getMessage());
    }
}
