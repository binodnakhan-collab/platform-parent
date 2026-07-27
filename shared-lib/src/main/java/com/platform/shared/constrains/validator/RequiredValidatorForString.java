package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.Required;
import com.platform.shared.constrains.validator.utils.ConstraintUtil;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RequiredValidatorForString implements BeanValidator<Required, String> {

    @Override
    public List<Function<String, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        List<Function<String, Boolean>> chain = new ArrayList<>();
        final String fieldName = getFieldName(context);
        chain.add(ConstraintUtil.validateNotBlank(context, fieldName));
        chain.add(ConstraintUtil.validateWhiteSpace(context, fieldName));
        return chain;
    }

}
