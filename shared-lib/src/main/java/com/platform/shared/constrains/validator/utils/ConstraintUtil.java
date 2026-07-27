package com.platform.shared.constrains.validator.utils;

import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConstraintUtil {

    private static void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    public static <T> Boolean performValidation(List<Function<T, Boolean>> validatorChain, T value) {
        for (Function<T, Boolean> validator : validatorChain) {
            Boolean isValid = validator.apply(value);
            if (!isValid) {
                return false;
            }
        }
        return true;
    }

    public static <T> Function<T, Boolean> validateNotBlank(ConstraintValidatorContext context, String fieldName) {
        return value -> {
            Boolean isNotBlank = ConstraintPredicateUtil.checkBlank().negate().test(value);
            if (!isNotBlank) {
                setMessage(context, fieldName + " must not be empty.");
            }
            return isNotBlank;
        };
    }

    public static Function<String, Boolean> validateInSize(Integer min, Integer max, ConstraintValidatorContext context, String fieldName) {
        return value -> {
            Boolean isInSize = ConstraintPredicateUtil.checkSize(min, max).test(value.length());
            if (!isInSize) {
                setMessage(context, fieldName + " must be of size between " + min + " and " + max + ".");
            }
            return isInSize;
        };
    }

    public static Function<String, Boolean> validatePatternMatches(Pattern pattern, ConstraintValidatorContext context, String fieldName) {
        return value -> {

            if (ConstraintPredicateUtil.checkNull().test(value)) {
                return true;
            }

            Boolean isPatternMatches = ConstraintPredicateUtil.checkPatternMatch(pattern).test(value);
            if (!isPatternMatches) {
                setMessage(context, "Please provide valid " + fieldName + ".");
            }
            return isPatternMatches;
        };
    }

    public static Function<LocalDate, Boolean> validateDob(ConstraintValidatorContext context, String fieldName) {
        return value -> {
            Boolean isDatePast = ConstraintPredicateUtil.checkDateInPast().test(value);
            if (!isDatePast) {
                setMessage(context, "Please provide valid " + fieldName + ".");
            }
            return isDatePast;
        };
    }

    public static Function<String, Boolean> validateWhiteSpace(ConstraintValidatorContext context, String fieldName) {
        return value -> {
            Boolean doesNotContainsWhiteSpace = ConstraintPredicateUtil.checkFirstOrLastCharWhiteSpace(value).negate().test(value);
            if (!doesNotContainsWhiteSpace) {
                setMessage(context, "Please remove whitespace from " + fieldName + ".");
            }
            return doesNotContainsWhiteSpace;
        };
    }

    public static Function<Long, Boolean> validatePositiveNumberForLong(ConstraintValidatorContext context, String fieldName) {
        return value -> validatePositiveNumber(context, fieldName, value);
    }

    public static Function<String, Boolean> validateContains(List<String> values, ConstraintValidatorContext context, String fieldName) {
        return value -> {
            Boolean doesContain = ConstraintPredicateUtil.checkContains(values).test(value);
            if (!doesContain) {
                setMessage(context, "Please provide valid " + fieldName);
            }
            return doesContain;
        };
    }

    public static Function<String, Boolean> validatePositiveNumberForString(ConstraintValidatorContext context, String fieldName) {
        return value -> validatePositiveNumber(context, fieldName, Long.valueOf(value));
    }

    public static Function<BigDecimal, Boolean> validatePositiveBigDecimal(ConstraintValidatorContext context, String fieldName) {
        return value -> {
            if (value == null) {
                return true;
            }
            Boolean isGreaterThanZero = ConstraintPredicateUtil.checkGreaterThanZeroBigDecimal().test(value);
            if (!isGreaterThanZero) {
                setMessage(context, fieldName + " must be greater than zero.");
            }
            return isGreaterThanZero;
        };
    }

    private static Boolean validatePositiveNumber(ConstraintValidatorContext context, String fieldName, Long value) {
        Boolean isGreaterThanZero = ConstraintPredicateUtil.checkGreaterThanZero().test(value);
        if (!isGreaterThanZero) {
            setMessage(context, fieldName + " must be greater than zero.");
        }
        return isGreaterThanZero;
    }

    public static Function<LocalDate, Boolean> validateFutureDate(ConstraintValidatorContext context, String fieldName) {
        return value -> {
            if (Objects.isNull(value)) {
                return true;
            }
            Boolean isDateFuture = ConstraintPredicateUtil.checkDateInFuture().test(value);
            if (!isDateFuture) {
                setMessage(context, "Please provide valid " + fieldName + ".");
            }
            return isDateFuture;
        };
    }

    public static <T extends Collection<?>> Function<T, Boolean> validateNotBlankForCollection(ConstraintValidatorContext context, String fieldName) {
        return collection -> {
            boolean isNotBlank = ConstraintPredicateUtil.checkBlankForCollection().negate().test(collection);
            if (!isNotBlank) {
                setMessage(context, fieldName + " must not be empty.");
            }
            return isNotBlank;
        };
    }

    public static <T extends Collection<?>> Function<T, Boolean> validateNotBlankElementForCollection(ConstraintValidatorContext context, String fieldName) {
        return collection -> {
            boolean hasNotBlankElement = ConstraintPredicateUtil.checkBlankElementForCollection().negate().test(collection);
            if (!hasNotBlankElement) {
                setMessage(context, fieldName + " must not have blank elements.");
            }
            return hasNotBlankElement;
        };
    }

    public static Function<List<String>, Boolean> validateContainsForList(List<String> valuesToValidateAgainst, ConstraintValidatorContext context, String fieldName, Boolean ignoreCase) {
        return valuesToValidate -> {

            List<String> convertedValuesToValidate = valuesToValidate;
            List<String> convertedValuesToValidateAgainst = valuesToValidateAgainst;

            if (ignoreCase) {
                convertedValuesToValidate = convertedValuesToValidate.stream().map(String::toUpperCase).collect(Collectors.toList());
                convertedValuesToValidateAgainst = convertedValuesToValidateAgainst.stream().map(String::toUpperCase).collect(Collectors.toList());
            }

            boolean doesContain = false;

            for (String valueToValidate : convertedValuesToValidate) {
                doesContain = ConstraintPredicateUtil.checkContains(convertedValuesToValidateAgainst).test(valueToValidate);
                if (!doesContain) {
                    setMessage(context, "Please provide valid " + fieldName);
                    break;
                }
            }

            return doesContain;
        };
    }

    public static Function<Set<String>, Boolean> validateContainsForSet(Set<String> valuesToValidateAgainst, ConstraintValidatorContext context, String fieldName, Boolean ignoreCase) {
        return valuesToValidate -> {

            Set<String> convertedValuesToValidate = valuesToValidate;
            Set<String> convertedValuesToValidateAgainst = valuesToValidateAgainst;

            if (ignoreCase) {
                convertedValuesToValidate = convertedValuesToValidate.stream().map(String::toUpperCase).collect(Collectors.toSet());
                convertedValuesToValidateAgainst = convertedValuesToValidateAgainst.stream().map(String::toUpperCase).collect(Collectors.toSet());
            }

            boolean doesContain = false;

            for (String valueToValidate : convertedValuesToValidate) {
                doesContain = ConstraintPredicateUtil.checkContains(convertedValuesToValidateAgainst).test(valueToValidate);
                if (!doesContain) {
                    setMessage(context, "Please provide valid " + fieldName);
                    break;
                }
            }

            return doesContain;
        };
    }
}
