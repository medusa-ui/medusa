package io.getmedusa.medusa.core.boot;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ValidationDetectionTest {

    final TestControllerWithValid testControllerWithValid = new TestControllerWithValid();
    final TestControllerWithoutValid testControllerWithoutValid = new TestControllerWithoutValid();

    @BeforeEach
    void setup() {
        ValidationDetection.INSTANCE.consider(testControllerWithValid);
    }

    @Test
    void testDoesBeanHaveMethodsWithValidation() {
        Assertions.assertTrue(ValidationDetection.INSTANCE.doesBeanHaveMethodsWithValidation(testControllerWithValid));
        Assertions.assertFalse(ValidationDetection.INSTANCE.doesBeanHaveMethodsWithValidation(testControllerWithoutValid));
    }

    @Test
    void testDoesMethodHaveValidationParameters() {
        Assertions.assertTrue(ValidationDetection.INSTANCE.doesMethodHaveValidationParameters(testControllerWithValid, "testMethodWithValid"));
        Assertions.assertFalse(ValidationDetection.INSTANCE.doesMethodHaveValidationParameters(testControllerWithValid, "testMethodWithoutValid"));
    }

    @Test
    void testGetValueComplex() {
        final String complexValue = "{\"firstName\":\"\",\"lastName\":\"dfsdfdf\"}.lastName";
        ValidationDetection.ParamWithValidation paramValue = new ValidationDetection.ParamWithValidation("lastName", 0, List.of(new ValidationDetection.Validation(NotBlank.class, null, null, "x")));
        List<String> parameters = List.of(complexValue);
        final String foundValue = ValidationDetection.INSTANCE.getValue(paramValue, parameters);
        Assertions.assertEquals("dfsdfdf", foundValue);
    }

    @Test
    void testGetValueSimpleValue() {
        final String complexValue = "Zombab";
        ValidationDetection.ParamWithValidation paramValue = new ValidationDetection.ParamWithValidation("lastName", 0, List.of(new ValidationDetection.Validation(NotBlank.class, null, null, "x")));
        List<String> parameters = List.of(complexValue);
        final String foundValue = ValidationDetection.INSTANCE.getValue(paramValue, parameters);
        Assertions.assertEquals("Zombab", foundValue);
    }

    @Test
    void testGetValueSimpleObject() {
        final String complexValue = "{\"firstName\":\"\",\"lastName\":\"dfsdfdf\"}";
        ValidationDetection.ParamWithValidation paramValue = new ValidationDetection.ParamWithValidation("lastName", 0, List.of(new ValidationDetection.Validation(NotBlank.class, null, null, "x")));
        List<String> parameters = List.of(complexValue);
        final String foundValue = ValidationDetection.INSTANCE.getValue(paramValue, parameters);
        Assertions.assertEquals("dfsdfdf", foundValue);
    }


    public record FormSample(@NotBlank String helloWorld) { }

    public class TestControllerWithoutValid {

        public void testMethodWithoutValid(FormSample formSample) { }

    }

    public class TestControllerWithValid {

        public void testMethodWithoutValid(FormSample formSample) { }

        public void testMethodWithValid(@Valid FormSample formSample) { }

    }

}
