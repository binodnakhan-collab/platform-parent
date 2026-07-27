package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.Required;
import com.platform.shared.constrains.validator.utils.ConstraintUtil;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class RequiredValidatorForUUID implements BeanValidator<Required, UUID> {

    @Override
    public List<Function<UUID, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        List<Function<UUID, Boolean>> chain = new ArrayList<>();
        final String fieldName = getFieldName(context);
        chain.add(ConstraintUtil.validateNotBlank(context, fieldName));
        return chain;
    }
}
