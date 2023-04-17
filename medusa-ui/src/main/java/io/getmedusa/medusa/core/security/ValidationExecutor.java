package io.getmedusa.medusa.core.security;

import io.getmedusa.medusa.core.boot.ValidationDetection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum ValidationExecutor {

    INSTANCE;

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

    public ValidationError validateParam(ValidationDetection.ParamWithValidation param, String valueToValidate) {
        if (param.contains(NotBlank.class) && validateNotBlank(valueToValidate)) {
            return new ValidationError(param.field(), "Value cannot be blank");
        } else if(param.contains(Pattern.class) && validatePattern("TODO", valueToValidate)) { //TODO
            return new ValidationError(param.field(), "Value does not match pattern");
        }
        return null;
    }

    private boolean validatePattern(String pattern, String valueToValidate) {
        return valueToValidate == null || valueToValidate.replaceFirst(pattern, "-").equals("-");
    }

    private boolean validateNotBlank(String valueToValidate) {
        return valueToValidate == null || valueToValidate.isBlank();
    }
}
