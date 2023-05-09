package io.getmedusa.medusa.core.validation;

public enum StandardEnglishValidationMessages {

    ASSERT_FALSE("{} must be false"),
    ASSERT_TRUE("{} must be true"),
    DECIMAL_MAX("{} exceeds allowed maximum value"),
    DECIMAL_MIN("{} below allowed minimum value"),
    DIGITS("{} must be a number within accepted range"),
    EMAIL("{} is not a valid email"),
    FUTURE("{} must be in the future"),
    FUTURE_OR_PRESENT("{} must be in the present or future"),
    MAX("{} exceeds allowed maximum value"),
    MIN("{} below allowed minimum value"),
    NEGATIVE("{} must be negative"),
    NEGATIVE_OR_ZERO("{} must be negative or zero"),
    NOT_BLANK("{} must not be blank"),
    NOT_EMPTY("{} must not be empty"),
    NOT_NULL("{} must not be NULL"),
    NULL("{} must be NULL"),
    PAST("{} must be in the past"),
    PAST_OR_PRESENT("{} must be in the past or present"),
    PATTERN("{} fails the expected pattern"),
    POSITIVE("{} must be positive"),
    POSITIVE_OR_ZERO("{} must be positive or zero"),
    SIZE("{} exceeds expected size");

    private final String message;
    StandardEnglishValidationMessages(String message) {
        this.message = message;
    }

    public static String resolve(String key, String field, String defaultMessage) {
        for(StandardEnglishValidationMessages v : StandardEnglishValidationMessages.values()) {
            if(v.name().equalsIgnoreCase(camelCaseToUnderscore(key))) {
                return v.message.replace("{}", field);
            }
        }
        return defaultMessage;
    }

    private static final String regex = "([a-z])([A-Z]+)";
    private static final String replacement = "$1_$2";

    private static String camelCaseToUnderscore(String camelCase) {
        return camelCase.replaceAll(regex, replacement);
    }
}
