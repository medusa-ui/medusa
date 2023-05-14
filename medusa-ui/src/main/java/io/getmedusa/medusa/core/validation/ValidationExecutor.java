package io.getmedusa.medusa.core.validation;

import io.getmedusa.medusa.core.boot.ValidationDetection;
import io.getmedusa.medusa.core.util.JSONUtils;
import jakarta.validation.constraints.*;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormatSymbols;
import java.util.*;

public enum ValidationExecutor {

    INSTANCE;

    private static final String EMAIL_PATTERN = "^[\\p{L}+\\p{M}0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]+(\\.{1}[\\p{L}+\\p{M}0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]+)*[0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]*@[\\p{L}+\\p{M}0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]+[0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]*(\\.{1}[\\p{L}+\\p{M}0-9!#$%&’'\\\"*+\\/=?^_ `{|}~-]+)+$";
    private static final String DECIMAL_SYMBOL = getDecimalSymbol();

    public Set<ValidationError> validate(String expression, Object bean) {
        Set<ValidationError> violationSet = new HashSet<>();
        if(ValidationDetection.INSTANCE.doesBeanHaveMethodsWithValidation(bean)) {
            String[] split = expression.replace(")", "").split("\\(");
            final String method = split[0];

            if(ValidationDetection.INSTANCE.doesMethodHaveValidationParameters(bean, method)) {
                final List<String> parameters = List.of(split[1].split(",(?![^{}]*})"));
                violationSet.addAll(ValidationDetection.INSTANCE.findValidations(bean, method, parameters));
            }
        }

        return violationSet;
    }

    // I'm not sure if its best to make an implementation ourselves or use something like Hibernate Validator
    // you might think: no need to re-implement; but actually we need this information anyway for the front-end
    // so the effort to have manual validation is kind of minimal compared to all the up-front work, and we save a dependency?
    // (basically just this method)
    public ValidationError validateParam(String method, ValidationDetection.ParamWithValidation param, String valueToValidate) {
        for(ValidationDetection.Validation validation : param.validations()) {
            if (NotBlank.class.equals(validation.type()) && failsNotBlank(valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if (NotEmpty.class.equals(validation.type()) && failsNotEmpty(valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(Pattern.class.equals(validation.type()) && failsPattern(validation.value(), valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(Email.class.equals(validation.type()) && failsPattern(EMAIL_PATTERN, valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(AssertFalse.class.equals(validation.type()) && failsAssert(valueToValidate, false)) {
                return new ValidationError(method, param, validation);
            } else if(AssertTrue.class.equals(validation.type()) && failsAssert(valueToValidate, true)) {
                return new ValidationError(method, param, validation);
            } else if(NotNull.class.equals(validation.type()) && failsAssertNull(valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(Null.class.equals(validation.type()) && !failsAssertNull(valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(Max.class.equals(validation.type()) && failsMax(validation.value(), valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(Min.class.equals(validation.type()) && failsMin(validation.value(), valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(Digits.class.equals(validation.type()) && failsDigits(validation.value(), validation.value2(), valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(Positive.class.equals(validation.type()) && failsMin("0.00001", valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(PositiveOrZero.class.equals(validation.type()) && failsMin("0", valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(Negative.class.equals(validation.type()) && failsMax("-0.00001", valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(NegativeOrZero.class.equals(validation.type()) && failsMax("0", valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(DecimalMax.class.equals(validation.type()) && failsMax(validation.value(), valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(DecimalMin.class.equals(validation.type()) && failsMin(validation.value(), valueToValidate)) {
                return new ValidationError(method, param, validation);
            } else if(Future.class.equals(validation.type()) && failsFuture(valueToValidate, false)) {
                return new ValidationError(method, param, validation);
            } else if(Past.class.equals(validation.type()) && failsPast(valueToValidate, false)) {
                return new ValidationError(method, param, validation);
            } else if(FutureOrPresent.class.equals(validation.type()) && failsFuture(valueToValidate, true)) {
                return new ValidationError(method, param, validation);
            } else if(PastOrPresent.class.equals(validation.type()) && failsPast(valueToValidate, true)) {
                return new ValidationError(method, param, validation);
            } else if(Size.class.equals(validation.type()) && failsSize(valueToValidate, validation.value(), validation.value2())) {
                return new ValidationError(method, param, validation);
            }
        }
        return null;
    }

    private boolean failsSize(String valueToValidate, String minSize, String maxSize) {
        if(minSize == null) {
            minSize = "0";
        }
        valueToValidate = valueToValidate.trim();
        int length = valueToValidate.length();
        if (valueToValidate.startsWith("[")) {
            length = JSONUtils.deserialize(valueToValidate, Object[].class).length;
        } else if (valueToValidate.startsWith("{")) {
            length = JSONUtils.deserialize(valueToValidate, Map.class).size();
        }
        return length > Long.parseLong(maxSize) || Long.parseLong(minSize) > length;
    }

    private boolean failsFuture(String valueToValidate, boolean allowPresent) {
        try {
            if (valueToValidate == null || !NumberUtils.isParsable(valueToValidate.trim())) return true;
            long longToValidate = Long.parseLong(valueToValidate.trim());
            long now = System.currentTimeMillis();
            if(allowPresent) { now -= 10*1000; } //buffer of 10 seconds allowed
            return longToValidate <= now;
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    private boolean failsPast(String valueToValidate, boolean allowPresent) {
        try {
            if (valueToValidate == null || !NumberUtils.isParsable(valueToValidate.trim())) return true;
            long longToValidate = Long.parseLong(valueToValidate.trim());
            long now = System.currentTimeMillis();
            if(allowPresent) { now += 10*1000; } //buffer of 10 seconds allowed
            return longToValidate >= now;
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    private boolean failsDigits(String min, String max, String valueToValidate) {
        double minVal = Double.parseDouble(min);
        double maxVal = Double.parseDouble(max);
        return failsRange(minVal, maxVal, valueToValidate);
    }

    private boolean failsMin(String min, String valueToValidate) {
        double minVal = Double.parseDouble(min);
        return failsRange(minVal, null, valueToValidate);
    }

    private boolean failsMax(String max, String valueToValidate) {
        double maxVal = Double.parseDouble(max);
        return failsRange(null, maxVal, valueToValidate);
    }

    private boolean failsRange(Double minVal, Double maxVal, String valueToValidate) {
        if(valueToValidate == null || !NumberUtils.isParsable(valueToValidate.trim())) return true;

        if(valueToValidate.contains(DECIMAL_SYMBOL)) {
            double value = Double.parseDouble(valueToValidate.trim());
            return (maxVal != null && value > maxVal) || (minVal != null && value < minVal);
        } else {
            long value = Long.parseLong(valueToValidate.trim());
            return (maxVal != null && value > maxVal) || (minVal != null && value < minVal);
        }
    }

    private static String getDecimalSymbol() {
        Locale currentLocale = Locale.getDefault();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(currentLocale);
        return String.valueOf(symbols.getDecimalSeparator());
    }

    private boolean failsAssertNull(String valueToValidate) {
        return valueToValidate == null;
    }

    private boolean failsAssert(String valueToValidate, boolean assertion) {
        return valueToValidate == null || Boolean.parseBoolean(valueToValidate) != assertion;
    }

    private boolean failsPattern(String pattern, String valueToValidate) {
        return valueToValidate == null || !valueToValidate.replaceFirst(pattern, "-").equals("-");
    }

    private boolean failsNotBlank(String valueToValidate) {
        return valueToValidate == null || valueToValidate.isBlank();
    }

    private boolean failsNotEmpty(String valueToValidate) {
        return valueToValidate == null || valueToValidate.isEmpty();
    }
}
