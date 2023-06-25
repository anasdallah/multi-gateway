package com.tap.multigateway.exception;

import com.tap.multigateway.api.BaseResponse;
import com.tap.multigateway.api.BaseResponseBuilder;
import com.tap.multigateway.validation.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@ControllerAdvice
@Order(HIGHEST_PRECEDENCE)
@Slf4j
public class ControllerAdvices extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
                                                                         HttpStatusCode status, WebRequest request) {

        StringBuilder sb = new StringBuilder();

        sb.append(ex.getMethod());
        sb.append(" method is not supported for this request. Supported methods is/are ");

        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (supportedMethods != null) {
            supportedMethods.forEach(t -> sb.append(t).append(" "));
        }

        return BaseResponseBuilder.generic(status, sb.toString());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers,
                                                                     final HttpStatusCode status, final WebRequest request) {
        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(final HttpMediaTypeNotAcceptableException ex, final HttpHeaders headers,
                                                                      final HttpStatusCode status, final WebRequest request) {
        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(final MissingPathVariableException ex, final HttpHeaders headers,
                                                               final HttpStatusCode status, final WebRequest request) {
        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers,
                                                                          final HttpStatusCode status, final WebRequest request) {
        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(final ServletRequestBindingException ex, final HttpHeaders headers,
                                                                          final HttpStatusCode status, final WebRequest request) {
        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(final ConversionNotSupportedException ex, final HttpHeaders headers,
                                                                  final HttpStatusCode status, final WebRequest request) {
        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatusCode status,
                                                        final WebRequest request) {

        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(final HttpMessageNotWritableException ex, final HttpHeaders headers,
                                                                  final HttpStatusCode status, final WebRequest request) {
        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers,
                                                                     final HttpStatusCode status, final WebRequest request) {
        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers,
                                                                   final HttpStatusCode status, final WebRequest request) {

        return BaseResponseBuilder.generic(status,
                "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL());
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(final AsyncRequestTimeoutException ex, final HttpHeaders headers,
                                                                        final HttpStatusCode status, final WebRequest webRequest) {
        return BaseResponseBuilder.generic(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                          HttpStatusCode status, WebRequest request) {

        List<ValidationError> validationErrors = ValidationError.getFormattedFieldsValidationErrors(
                ex.getBindingResult().getAllErrors());

        return BaseResponseBuilder.validationErrorResponse(validationErrors);

    }

    /* Extract Class*/

    @ExceptionHandler(TapException.class)
    public ResponseEntity<BaseResponse<Void>> handleServiceException(final TapException ex) {

        log.error("Exception Occurred: " + MessageFormat.format(ex.getError().getDescription(), ex.getArguments()));
        return BaseResponseBuilder.errorResponse(ex.getStatus(), ex.getError(), ex.getArguments());
    }

    @ExceptionHandler(value = {RuntimeException.class, UnsupportedOperationException.class, IllegalStateException.class})
    public ResponseEntity<BaseResponse<Void>> runTimeException(Exception ex) {

        log.error(ex.getMessage());

        return BaseResponseBuilder.internalServerError();

    }
}

