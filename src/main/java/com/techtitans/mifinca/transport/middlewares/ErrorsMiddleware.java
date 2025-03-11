package com.techtitans.mifinca.transport.middlewares;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.domain.exceptions.ErrorType;

import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class ErrorsMiddleware {

    private Map<ErrorType, HttpStatus> errorCodesMapping;

    ErrorsMiddleware(){
        initCodesMap();
    }

    private void initCodesMap(){
        errorCodesMapping = new HashMap<>();
        errorCodesMapping.put(ErrorType.BAD_INPUT, HttpStatus.BAD_REQUEST);
        errorCodesMapping.put(ErrorType.UNATHORIZED, HttpStatus.UNAUTHORIZED);
        errorCodesMapping.put(ErrorType.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //catches all exceptions thrown
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        //the base case we extract the message as the error message, and we set the 500 error
        String errorMessage = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        //checking if the exception is our own (API type)
        if(ex instanceof ApiException ours){
            //if its our own type of exception, then we read the message and the 
            errorMessage = ours.getErrorCode();
            status = errorCodesMapping.get(ours.getErrorType());
            if(status == null){
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("ERROR", errorMessage);
        return new ResponseEntity<>(errorResponse, status);
    }
    
}
