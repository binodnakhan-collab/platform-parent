package com.platform.shared.constrains.validator;


import com.platform.shared.constrains.ValidateEnum;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseEnumValidator<T> implements BeanValidator<ValidateEnum, T> {

    /**
     * The enum values to validate against. Set during initialization.
     */
    protected List<String> values;

    /**
     * The field name for error messages. Set during initialization.
     */
    protected String fieldName;

    /**
     * Initializes the validator with the constraint annotation.
     * Extracts the enum class and field name for validation.
     *
     * @param constraintAnnotation the enum constraint annotation
     */
    @Override
    public void initialize(ValidateEnum constraintAnnotation) {
        this.values = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
        this.fieldName = constraintAnnotation.field();
    }

    /**
     * Builds the validation chain for enum validation.
     * This method must be implemented by subclasses to provide specific validation logic.
     *
     * @param context the constraint validator context
     * @return a list of validation functions to be executed in sequence
     */
    @Override
    public abstract List<Function<T, Boolean>> buildValidatorChain(ConstraintValidatorContext context);
}