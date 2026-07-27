package com.platform.shared.constrains.validator.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ConstraintPredicateUtil {

    public static <T> Predicate<T> checkNull() {
        return Objects::isNull;
    }

    public static <T> Predicate<T> checkBlank() {
        return (value) -> Objects.isNull(value) || value.toString().isBlank();
    }

    public static Predicate<Integer> checkGreaterThanOrEqualTo(Integer minLimit) {
        return (value) -> value >= minLimit;
    }

    public static Predicate<Integer> checkLessThanOrEqualTo(Integer maxLimit) {
        return (value) -> value <= maxLimit;
    }

    public static Predicate<Integer> checkSize(Integer min, Integer max) {
        return checkLessThanOrEqualTo(max).and(checkGreaterThanOrEqualTo(min));
    }

    public static Predicate<String> checkPatternMatch(Pattern pattern) {
        return (value) -> pattern.matcher(value).matches();
    }

    public static Predicate<LocalDate> checkDateInPast() {
        return (value) -> value.isBefore(LocalDate.now());
    }

    public static Predicate<LocalDate> checkDateInFuture() {
        return (value) -> value.isAfter(LocalDate.now());
    }

    public static Predicate<String> checkWhiteSpaceAt(Integer index) {
        return (value) -> Character.isWhitespace(value.charAt(index));
    }

    public static Predicate<String> checkFirstOrLastCharWhiteSpace(String value) {
        return checkWhiteSpaceAt(0).or(checkWhiteSpaceAt(value.length() - 1));
    }

    public static Predicate<Long> checkGreaterThanZero() {
        return (value) -> value > 0;
    }

    public static Predicate<BigDecimal> checkGreaterThanZeroBigDecimal() {
        return (value) -> value != null && value.signum() > 0;
    }

    public static Predicate<String> checkContains(List<String> values) {
        return values::contains;
    }

    public static <T extends Collection<?>> Predicate<T> checkBlankElementForCollection() {
        return collection -> collection.stream().anyMatch(ConstraintPredicateUtil.checkBlank());
    }

    public static <T extends Collection<?>> Predicate<T> checkBlankForCollection() {
        return collection -> collection == null || collection.isEmpty();
    }

    public static Predicate<String> checkContains(Set<String> values) {
        return values::contains;
    }
}
