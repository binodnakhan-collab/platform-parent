package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.ValidateEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class EmailConstraintValidator implements BeanValidator<ValidateEmail, String> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*" +
        "@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$"
    );

    private static final int MAX_LENGTH = 254;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        if (value.length() > MAX_LENGTH) {
            return false;
        }
        return EMAIL_PATTERN.matcher(value).matches();
    }

    @Override
    public List<Function<String, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        return List.of();
    }
}