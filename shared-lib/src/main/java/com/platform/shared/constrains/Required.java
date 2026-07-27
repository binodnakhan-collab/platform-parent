package com.platform.shared.constrains;

import com.platform.shared.constrains.validator.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {
        RequiredValidatorForString.class,
        RequiredValidatorForNumber.class,
        RequiredValidatorForBoolean.class,
        RequiredValidatorForLocalDate.class,
        RequiredValidatorForLocalTime.class,
        RequiredValidatorForUUID.class,
})
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {

    String message() default "Field is required.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean input() default true;

    String field();

}
