package com.tap.multigateway.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiErrors {

    INTERNAL_SERVER_ERROR("TAP-1", "internal server error"),
    NOT_FOUND("TAP-2", "{0} could not be found with the given parameters"),
    REQUIRED_FIELDS("TAP-3", "These Fields Are Required: [{0}]"),
    INVALID_FIELD_VALUE("TAP-4", "Invalid field value for field(s) {0}"),
    VALIDATION_ERROR("TAP-5", "Validation Error"),
    REQUEST_TIMEOUT("TAP-6", "Request timeout, takes more than 5 seconds"),
    REQUEST_ALREADY_PROCESSED_BEFORE("TAP-7", "Request with Id: [{0}] already processed before"),
    THIRD_PARTY("TAP-8", "{0}");


    private final String code;
    private final String description;


    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

}
