package io.getmedusa.medusa.core.security;

import io.getmedusa.medusa.core.boot.ValidationDetection;
import io.getmedusa.medusa.sample.ValidatorController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ValidationExecutorTest {

    final ValidatorController bean = new ValidatorController();

    @BeforeEach
    void setup() {
        ValidationDetection.INSTANCE.consider(bean);
    }

    @Test
    void testExpressionInterpretation() {
        final String expression = "displayName(123, {\"firstName\":\"\",\"lastName\":\"\"}, ' ')";

        final Set<ValidationError> violations = ValidationExecutor.INSTANCE.validate(expression, bean);
        Assertions.assertEquals(2, violations.size());
    }

}
