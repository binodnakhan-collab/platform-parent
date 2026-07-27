package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.validator.utils.ConstraintUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

public interface BeanValidator<A extends Annotation, T> extends ConstraintValidator<A, T> {

    default boolean isValid(T value, ConstraintValidatorContext context) {
        List<Function<T, Boolean>> validatorChain = buildValidatorChain(context);
        return ConstraintUtil.performValidation(validatorChain, value);
    }

    default String getFieldName(ConstraintValidatorContext context) {
        Field basePathField = null;
        PathImpl path = null;
        try {
            basePathField = ConstraintValidatorContextImpl.class.getDeclaredField("basePath");
            basePathField.setAccessible(true);
            path = (PathImpl) basePathField.get(context);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return path.getLeafNode().getName();
    }

    List<Function<T, Boolean>> buildValidatorChain(ConstraintValidatorContext context);
}
