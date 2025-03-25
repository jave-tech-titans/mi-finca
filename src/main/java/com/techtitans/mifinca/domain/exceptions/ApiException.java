package com.techtitans.mifinca.domain.exceptions;

public class ApiException extends RuntimeException{
    private final ApiError err;

    public ApiException(ApiError err){
        this.err = err;
    }

    public String getErrorCode(){
        return err.getMessage();
    }

    public ErrorType getErrorType(){
        return err.getType();
    }

    public ApiError getError(){
        return err;
    }
}
