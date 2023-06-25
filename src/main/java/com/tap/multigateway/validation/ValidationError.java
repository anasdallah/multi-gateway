package com.tap.multigateway.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ValidationError {

    private String fieldName;
    private String errorMessage;

    public static List<ValidationError> getFormattedFieldsValidationErrors(List<ObjectError> objectErrors) {

        List<ValidationError> validationErrors = new ArrayList<>();

        objectErrors.forEach(objectError -> {

            if (objectError instanceof FieldError fieldError) {

                validationErrors.add(new ValidationError(
                        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldError.getField()),
                        fieldError.getDefaultMessage()));
            }

        });
        return validationErrors;

    }
}
