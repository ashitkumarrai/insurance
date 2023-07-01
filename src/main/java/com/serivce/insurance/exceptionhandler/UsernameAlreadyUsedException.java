package com.serivce.insurance.exceptionhandler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UsernameAlreadyUsedException extends RuntimeException {


     
    public UsernameAlreadyUsedException() {
        super("username name already used!");
    }
}

