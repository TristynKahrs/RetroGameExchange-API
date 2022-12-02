package com.tkahrs.retroexchange;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.management.openmbean.KeyAlreadyExistsException;

@ControllerAdvice
public class MyExceptionOverride extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(IllegalArgumentException.class)
//    public final ResponseEntity<MyExceptionMessage> somethingWentWrong(IllegalArgumentException ex){
//        System.out.println("somethingWentWrong()");
//        MyExceptionMessage myEx = new MyExceptionMessage(ex.getMessage(), 400);
//        return new ResponseEntity<MyExceptionMessage>(myEx, new HttpHeaders(), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(KeyAlreadyExistsException.class)
//    public final ResponseEntity<MyExceptionMessage> keyExists(RuntimeException ex){
//        System.out.println("Key Already Exists");
//        MyExceptionMessage myEx = new MyExceptionMessage(ex.getMessage(), 420);
//        return new ResponseEntity<MyExceptionMessage>(myEx, new HttpHeaders(), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(SecurityException.class)
//    public final ResponseEntity<MyExceptionMessage> unauthorized(SecurityException ex){
//        System.err.println("User not authorized");
//        MyExceptionMessage myEx = new MyExceptionMessage(ex.getMessage(), 401);
//        return new ResponseEntity<MyExceptionMessage>(myEx, new HttpHeaders(), 401);
//    }
//
//    @ExceptionHandler(NullPointerException.class)
//    public final ResponseEntity<MyExceptionMessage> unauthorized(NullPointerException ex){
//        System.err.println("not found");
//        MyExceptionMessage myEx = new MyExceptionMessage(ex.getMessage(), 404);
//        return new ResponseEntity<MyExceptionMessage>(myEx, new HttpHeaders(), 404);
//    }
//
//    @ExceptionHandler(NumberFormatException.class)
//    public final ResponseEntity<MyExceptionMessage> unauthorized(NumberFormatException ex){
//        System.err.println("number must be entered!");
//        MyExceptionMessage myEx = new MyExceptionMessage(ex.getMessage(), 400);
//        return new ResponseEntity<MyExceptionMessage>(myEx, new HttpHeaders(), 400);
//    }
}
