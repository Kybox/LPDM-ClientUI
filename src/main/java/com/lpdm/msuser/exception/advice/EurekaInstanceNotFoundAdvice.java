package com.lpdm.msuser.exception.advice;

import com.lpdm.msuser.exception.EurekaInstanceNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class EurekaInstanceNotFoundAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EurekaInstanceNotFound.class)
    public String exceptionHandler(EurekaInstanceNotFound e){
        return e.getMessage();
    }
}
