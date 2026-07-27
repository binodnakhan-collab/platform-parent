package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.Required;
import com.platform.shared.constrains.validator.utils.ConstraintUtil;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RequiredValidatorForBoolean implements BeanValidator<Required, Boolean> {

    @Override
    public List<Function<Boolean, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        List<Function<Boolean, Boolean>> chain = new ArrayList<>();
        final String fieldName = getFieldName(context);
        chain.add(ConstraintUtil.validateNotBlank(context, fieldName));
        return chain;
    }

}
