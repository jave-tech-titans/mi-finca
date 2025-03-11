package com.techtitans.mifinca.domain.exceptions;

public class ApiException extends RuntimeException{
    private ApiError err;

    public ApiException(ApiError err){
        this.err = err;
    }

    public String getErrorCode(){
        return err.message;
    }

    public ErrorType getErrorType(){
        return err.type;
    }
}
