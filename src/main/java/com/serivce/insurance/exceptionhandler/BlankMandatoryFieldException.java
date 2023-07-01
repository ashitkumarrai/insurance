package com.serivce.insurance.exceptionhandler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BlankMandatoryFieldException extends Exception {
     public BlankMandatoryFieldException(String exception) {
    super(exception);
  }
    
}
