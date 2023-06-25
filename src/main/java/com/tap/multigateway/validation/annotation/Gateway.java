package com.tap.multigateway.validation.annotation;

import com.tap.multigateway.constant.GatewaysNames;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.apache.logging.log4j.util.Strings;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Gateway.GatewayValidator.class)
@Documented
public @interface Gateway {

    String message() default "Invalid gateway";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class GatewayValidator implements ConstraintValidator<Gateway, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {

            if (Strings.isBlank(value)) {
                return false;
            }

            for (GatewaysNames gatewaysName : GatewaysNames.values()) {
                if (gatewaysName.getName().equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }

}
