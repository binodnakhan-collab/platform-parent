package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.Required;
import com.platform.shared.constrains.validator.utils.ConstraintUtil;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RequiredValidatorForLocalDate implements BeanValidator<Required, LocalDate> {

    @Override
    public List<Function<LocalDate, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        List<Function<LocalDate, Boolean>> chain = new ArrayList<>();
        final String fieldName = getFieldName(context);
        chain.add(ConstraintUtil.validateNotBlank(context, fieldName));
        return chain;
    }
}
