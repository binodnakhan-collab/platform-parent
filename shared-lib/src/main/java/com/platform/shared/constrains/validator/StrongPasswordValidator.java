package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.StrongPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.function.Function;

public class StrongPasswordValidator implements BeanValidator<StrongPassword, String> {

    private int minLength;
    private boolean upperCase;
    private boolean lowerCase;
    private boolean digit;
    private boolean specialCharacter;

    @Override
    public void initialize(StrongPassword annotation) {

        minLength = annotation.minLength();
        upperCase = annotation.upperCase();
        lowerCase = annotation.lowerCase();
        digit = annotation.digit();
        specialCharacter = annotation.specialCharacter();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return true;
        }

        if (value.length() < minLength) {
            return false;
        }

        if (upperCase && value.chars().noneMatch(Character::isUpperCase)) {
            return false;
        }

        if (lowerCase && value.chars().noneMatch(Character::isLowerCase)) {
            return false;
        }

        if (digit && value.chars().noneMatch(Character::isDigit)) {
            return false;
        }

        if (specialCharacter &&
                value.chars().allMatch(Character::isLetterOrDigit)) {
            return false;
        }

        return true;
    }

    @Override
    public List<Function<String, Boolean>> buildValidatorChain(ConstraintValidatorContext context) {
        return List.of();
    }
}