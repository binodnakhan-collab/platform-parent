package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.validator.utils.ConstraintUtil;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EnumValidatorForString extends BaseEnumValidator<String> {

    /**
     * Builds the validation chain for string enum validation.
     *
     * @param context the constraint validator context
     * @return a list of validation functions to be executed in sequence
     */
    @Override
    public List<Function<String, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        List<Function<String, Boolean>> chain = new ArrayList<>();
        chain.add(ConstraintUtil.validateNotBlank(context, fieldName));
        chain.add(ConstraintUtil.validateContains(values, context, fieldName));
        return chain;
    }
}