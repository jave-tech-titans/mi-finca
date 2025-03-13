package com.techtitans.mifinca.domain.exceptions;

public enum ApiError {
    EMPTY_FIELDS("EMPTY_FIELDS", ErrorType.BAD_INPUT),
    PASSWORD_TO_SHORT("PASSWORD_TO_SHORT", ErrorType.BAD_INPUT),
    INVALID_EMAIL("INVALID_EMAIL", ErrorType.BAD_INPUT),
    EMAIL_ALREADY_TAKEN("EMAIL_ALREADY_TAKEN", ErrorType.BAD_INPUT),
    NON_EXISTING_ACCOUNT("NON_EXISTING_ACCOUNT", ErrorType.BAD_INPUT),
    INCORRECT_PASSWORD("INCORRECT_PASSWORD", ErrorType.BAD_INPUT),
    INVALID_TOKEN("INVALID_TOKEN", ErrorType.BAD_INPUT),
    EXPIRED_TOKEN("EXPIRED_TOKEN", ErrorType.UNATHORIZED),
    UNATHORIZED_TO_POST_PROPERTY("UNATHORIZED_TO_POST_PROPERTY", ErrorType.UNATHORIZED),
    INVALID_DEPARTMENT("INVALID_DEPARTMENT", ErrorType.BAD_INPUT),
    INVALID_PARAMETERS("INVALID_PARAMETERS", ErrorType.BAD_INPUT),
    UNATHORIZED_TO_EDIT_PROPERTY("UNATHORIZED_TO_EDIT_PROPERTY", ErrorType.UNATHORIZED),
    UNABLE_TO_STORE_IMAGE("UNABLE_TO_STORE_IMAGE", ErrorType.INTERNAL_ERROR),
    FILE_NOT_FOUND("FILE_NOT_FOUND", ErrorType.NOT_FOUND),
    UNABLE_TO_SEND_EMAIL("UNABLE_TO_SEND_EMAIL", ErrorType.INTERNAL_ERROR),
    PROPERTY_NOT_FOUND("PROPERTY_NOT_FOUND", ErrorType.NOT_FOUND),
    UNATHORIZED_TO_REQUEST("UNATHORIZED_TO_REQUEST", ErrorType.UNATHORIZED),
    INVALID_SCHEDULE_DATES("INVALID_SCHEDULE_DATES", ErrorType.BAD_INPUT),
    REQUEST_NOT_FOUND("REQUEST_NOT_FOUND", ErrorType.NOT_FOUND),
    UNATHORIZED_TO_EDIT_REQUEST("UNABLE_TO_EDIT_REQUEST", ErrorType.UNATHORIZED),
    UNABLE_TO_EDIT_REQUEST("UNABLE_TO_EDIT_REQUEST", ErrorType.BAD_INPUT),
    REQUEST_ISNT_IN_PAYMENT("REQUEST_ISNT_IN_PAYMENT", ErrorType.BAD_INPUT),
    USER_IS_NOT_THE_REQUEST_ONE("USER_IS_NOT_THE_REQUEST_ONE", ErrorType.UNATHORIZED),
    CANT_RATE_YET("CANT_RATE_YET", ErrorType.BAD_INPUT),
    UNATHORIZED_TO_RATE("UNATHORIZED_TO_RATE", ErrorType.UNATHORIZED),
    ALREADY_RATED("ALREADY_RATED", ErrorType.BAD_INPUT)
    ;

    public String message;
    public ErrorType type;


    ApiError(String message, ErrorType type){
        this.message = message;
        this.type = type;
    }
}
