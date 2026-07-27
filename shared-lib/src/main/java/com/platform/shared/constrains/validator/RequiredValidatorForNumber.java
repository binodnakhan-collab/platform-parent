package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.Required;
import com.platform.shared.constrains.validator.utils.ConstraintUtil;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RequiredValidatorForNumber implements BeanValidator<Required, Number> {

    @Override
    public List<Function<Number, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        List<Function<Number, Boolean>> chain = new ArrayList<>();
        final String fieldName = getFieldName(context);
        chain.add(ConstraintUtil.validateNotBlank(context, fieldName));
        return chain;
    }

}
