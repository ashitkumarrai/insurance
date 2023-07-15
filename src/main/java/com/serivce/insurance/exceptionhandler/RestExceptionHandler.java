package com.serivce.insurance.exceptionhandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        List<String> details = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> details.add(error.getDefaultMessage()));

        ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, details.toString());
         error.setTitle("Method Argument Not Valid");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

                List<String> errors = new ArrayList<>();
        errors.add(ex.getCause().toString());
        ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        error.setTitle("JSON parse error");

        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.BAD_REQUEST);
       
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(RecordNotFoundException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        
        details.add(ex.getLocalizedMessage());

        ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, details.toString());
        error.setTitle("record not found error");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getCause().toString());
        ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        error.setTitle("ConstraintViolationException");

        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.NOT_FOUND);

    }

     @ExceptionHandler({ SQLIntegrityConstraintViolationException.class })
    public ResponseEntity<Object> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
        error.setTitle("SQLIntegrityConstraintViolationException");

        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.BAD_REQUEST);

    }
    
    @ExceptionHandler(BlankMandatoryFieldException.class)
    public final ResponseEntity<Object> handleUserBlankMandatoryFieldException(BlankMandatoryFieldException ex) {
        List<String> details = new ArrayList<>();
        
        details.add(ex.getMessage());

        ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, details.toString());
        error.setTitle("Blank Mandatory field exception");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}

