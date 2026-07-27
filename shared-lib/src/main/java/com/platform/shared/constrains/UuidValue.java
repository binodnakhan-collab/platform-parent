package com.platform.shared.constrains;

import com.platform.shared.constrains.validator.UuidValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UuidValueValidator.class)
public @interface UuidValue {

    String message() default "Invalid UUID.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}