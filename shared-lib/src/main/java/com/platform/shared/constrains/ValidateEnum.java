package com.platform.shared.constrains;

import com.platform.shared.constrains.validator.EnumValidatorForList;
import com.platform.shared.constrains.validator.EnumValidatorForSet;
import com.platform.shared.constrains.validator.EnumValidatorForString;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {EnumValidatorForString.class, EnumValidatorForList.class, EnumValidatorForSet.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ValidateEnum {

    /**
     * The enum class to validate against.
     * 
     * @return the enum class
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * The error message to display when validation fails.
     * 
     * @return the error message
     */
    String message() default "Invalid enum value(s).";

    /**
     * The groups this constraint belongs to.
     * 
     * @return the groups
     */
    Class<?>[] groups() default {};

    /**
     * The payload associated to the constraint.
     * 
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The field name to use in error messages.
     * 
     * @return the field name
     */
    String field();

} 