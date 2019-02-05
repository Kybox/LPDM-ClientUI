package com.lpdm.msuser.exception.advice;

import com.lpdm.msuser.exception.NotFound404Exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotFound404Advice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFound404Exception.class)
    public String exceptionHandler(NotFound404Exception e){
        return e.getMessage();
    }
}
