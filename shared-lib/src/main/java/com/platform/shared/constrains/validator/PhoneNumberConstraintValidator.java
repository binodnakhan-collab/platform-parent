package com.platform.shared.constrains.validator;

import com.platform.shared.constrains.ValidatePhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberConstraintValidator implements ConstraintValidator<ValidatePhoneNumber, String> {

    /*
     * Rules:
     *  - Optional leading '+' (country code indicator), max once, only at start
     *  - Digits and '-' only after that
     *  - No leading/trailing '-', no consecutive '--'
     *  - Total digit count between 7 and 15 (ITU E.164 range)
     *
     * Examples that MATCH:  +977-9812345678, 9812345678, +1-415-555-2671, 015-4567890
     * Examples that FAIL:   +91 9876543210 (space), (91)9876543210, 91--9876543210, ++919876543210
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9]+(?:-[0-9]+)*$"
    );

    private static final int MIN_DIGITS = 7;
    private static final int MAX_DIGITS = 15;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        if (!PHONE_PATTERN.matcher(value).matches()) {
            return false;
        }

        long digitCount = value.chars().filter(Character::isDigit).count();
        return digitCount >= MIN_DIGITS && digitCount <= MAX_DIGITS;
    }
}