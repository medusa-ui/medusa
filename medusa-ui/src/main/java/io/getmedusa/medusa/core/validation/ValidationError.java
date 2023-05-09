package io.getmedusa.medusa.core.validation;

import io.getmedusa.medusa.core.boot.ValidationDetection;

public record ValidationError(String field, String validation, String message) {
    public ValidationError(ValidationDetection.ParamWithValidation param, ValidationDetection.Validation validation) {
        this(param.field(), validation.type().getSimpleName(), validation.message());
    }
}