package com.techtitans.mifinca.domain.exceptions;

public enum ApiError {
    EMPTY_FIELDS("EMPTY_FIELDS", ErrorType.BAD_INPUT),
    PASSWORD_TO_SHORT("PASSWORD_TO_SHORT", ErrorType.BAD_INPUT),
    INVALID_EMAIL("INVALID_EMAIL", ErrorType.BAD_INPUT),
    EMAIL_ALREADY_TAKEN("EMAIL_ALREADY_TAKEN", ErrorType.BAD_INPUT),
    NON_EXISTING_ACCOUNT("NON_EXISTING_ACCOUNT", ErrorType.BAD_INPUT),
    INCORRECT_PASSWORD("INCORRECT_PASSWORD", ErrorType.BAD_INPUT)
    ;

    public String message;
    public ErrorType type;


    ApiError(String message, ErrorType type){
        this.message = message;
        this.type = type;
    }
}
