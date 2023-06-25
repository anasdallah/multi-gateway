package com.tap.multigateway.api;

import com.tap.multigateway.constant.ApiErrors;
import com.tap.multigateway.validation.ValidationError;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;

@NoArgsConstructor
public class BaseResponseBuilder {


    public static <R> ResponseEntity<BaseResponse<R>> ok() {
        return ResponseEntity.ok(successResponse());
    }

    public static <R> ResponseEntity<BaseResponse<R>> ok(final R responseBody) {
        return ResponseEntity.ok(successResponse(responseBody));
    }


    public static <R> ResponseEntity<BaseResponse<R>> created(final R responseBody)  {
        return ResponseEntity.created(URI.create("")).body(successResponse(responseBody));
    }


    private static <R> BaseResponse<R> successResponse() {
        return successResponse(null);
    }

    public static <R> BaseResponse<R> successResponse(final R responseBody) {

        BaseResponse<R> response = new BaseResponse<>();

        response.setResponseStatus("Success");
        response.setDate(Instant.now());
        response.setResponseBody(responseBody);

        return response;
    }

    public static ResponseEntity<Object> generic(final HttpStatusCode status, final String message) {
        return ResponseEntity.status(status).body(errorResponse(null, message, new Object[0], null));
    }

    public static <R> ResponseEntity<BaseResponse<R>> internalServerError() {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrors.INTERNAL_SERVER_ERROR, null);
    }

    public static <R> ResponseEntity<BaseResponse<R>> errorResponse(final HttpStatus status, final ApiErrors error,
                                                                    final Object[] arguments) {
        return ResponseEntity.status(status).body(errorResponse(error.getCode(), error.getDescription(), arguments, null));
    }

    public static <R> ResponseEntity<BaseResponse<R>> validationErrorResponse(final List<ValidationError> validationErrors) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse(ApiErrors.VALIDATION_ERROR.getCode(), ApiErrors.VALIDATION_ERROR.getDescription(), null,
                        validationErrors));
    }

    private static <R> BaseResponse<R> errorResponse(final String errorCode, final String errorMsg,
                                                     final Object[] arguments, final List<ValidationError> validationErrors) {

        BaseResponse<R> response = new BaseResponse<>();

        response.setResponseStatus("Failed");
        response.setDate(Instant.now());
        response.setErrorCode(errorCode);
        response.setErrorMessage(MessageFormat.format(errorMsg, arguments));
        response.setValidationErrors(validationErrors);

        return response;
    }


}