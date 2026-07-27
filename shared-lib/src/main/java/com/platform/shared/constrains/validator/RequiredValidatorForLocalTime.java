package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.Required;
import com.platform.shared.constrains.validator.utils.ConstraintUtil;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RequiredValidatorForLocalTime implements BeanValidator<Required, LocalTime> {

    @Override
    public List<Function<LocalTime, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        List<Function<LocalTime, Boolean>> chain = new ArrayList<>();
        final String fieldName = getFieldName(context);
        chain.add(ConstraintUtil.validateNotBlank(context, fieldName));
        return chain;
    }
}
