package com.tap.multigateway.exception;


import com.tap.multigateway.constant.ApiErrors;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TapException extends RuntimeException {

    protected final HttpStatus status;
    protected final ApiErrors error;
    private final Object[] arguments;

    public TapException(HttpStatus status, ApiErrors error, Object... arguments) {
        super(new Exception());
        this.status = status;
        this.error = error;
        this.arguments = arguments;
    }

    @Override
    public String getMessage() {
        return String.format("Error code [%s], http status [%s]", error.getCode(), status.toString());
    }

    @Override
    public String toString() {
        return getMessage();
    }


    public static TapException badRequest(final ApiErrors error, final Object... args) {
        return new TapException(HttpStatus.BAD_REQUEST, error, args);
    }

    public static TapException invalidValue(final String fields) {
        return badRequest(ApiErrors.INVALID_FIELD_VALUE, fields);
    }

}