package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.validator.utils.ConstraintUtil;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EnumValidatorForList extends BaseEnumValidator<List<String>> {

    /**
     * Builds the validation chain for list enum validation.
     *
     * @param context the constraint validator context
     * @return a list of validation functions to be executed in sequence
     */
    @Override
    public List<Function<List<String>, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        List<Function<List<String>, Boolean>> chain = new ArrayList<>();
        chain.add(ConstraintUtil.validateNotBlankForCollection(context, fieldName));
        chain.add(ConstraintUtil.validateNotBlankElementForCollection(context, fieldName));
        chain.add(ConstraintUtil.validateContainsForList(values, context, fieldName, true));
        return chain;
    }
}