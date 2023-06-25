package com.tap.multigateway.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.apache.logging.log4j.util.Strings;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = Currency.CurrencyValidator.class)
@Documented
public @interface Currency {

    String message() default "Invalid Currency";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CurrencyValidator implements ConstraintValidator<Currency, String> {

        @Override
        public boolean isValid(String currencyField, ConstraintValidatorContext cxt) {

            if (Strings.isBlank(currencyField)) {
                return false;
            }

            try {
                java.util.Currency.getInstance(currencyField);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}
