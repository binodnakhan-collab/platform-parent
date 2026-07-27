package com.platform.shared.constrains;

import com.platform.shared.constrains.validator.PhoneNumberConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatePhoneNumber {
    String message() default "Invalid phone number. Allowed format: optional '+' country code, digits, and '-' separators only";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}