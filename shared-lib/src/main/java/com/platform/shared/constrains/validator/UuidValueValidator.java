package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.UuidValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class UuidValueValidator implements BeanValidator<UuidValue, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        switch (value) {
            case null -> {
                return true;
            }
            case UUID uuid -> {
                return true;
            }
            case String text -> {

                if (text.isBlank()) {
                    return true;
                }

                try {
                    UUID.fromString(text);
                    return true;
                } catch (IllegalArgumentException ex) {
                    return false;
                }
            }
            default -> {
            }
        }

        return false;
    }

    @Override
    public List<Function<Object, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        return List.of();
    }
}