package io.getmedusa.medusa.core.boot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.validation.ValidationError;
import io.getmedusa.medusa.core.validation.ValidationExecutor;
import io.getmedusa.medusa.core.validation.ValidationMessageResolver;
import jakarta.validation.Valid;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public enum ValidationDetection {

    INSTANCE;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ValidationList classesWithValidMethods = new ValidationList(new ArrayList<>());

    public String buildFrontendValidations(String controller, ValidationMessageResolver resolver) {
        try {
            List<FrontEndValidation> frontEndValidations = resolver.resolveMessages(classesWithValidMethods.findFrontEndValidationsForController(controller));
            return objectMapper.writeValueAsString(frontEndValidations);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void consider(Object bean) {
        Class beanClass = bean.getClass();
        for (Method method : beanClass.getMethods()) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0, parameterAnnotationsLength = parameterAnnotations.length; i < parameterAnnotationsLength; i++) {
                Annotation[] paramAnnotations = parameterAnnotations[i];
                for (Annotation paramAnnotation : paramAnnotations) {
                    if (paramAnnotation.annotationType().equals(Valid.class)) {
                        classesWithValidMethods.add(beanClass, method, i);
                    }
                }
            }
        }
    }

    public boolean doesBeanHaveMethodsWithValidation(Object bean) {
        return null != classesWithValidMethods.findByKey(bean.getClass());
    }

    public boolean doesMethodHaveValidationParameters(Object bean, String method) {
        final ClassWithValidation classWithValidation = classesWithValidMethods.findByKey(bean.getClass());
        return null != classWithValidation && null != classWithValidation.findByKey(method);
    }

    public Set<ValidationError> findValidations(Object bean, String method, List<String> parameters) {
        final HashSet<ValidationError> set = new HashSet<>();

        final ClassWithValidation classWithValidation = classesWithValidMethods.findByKey(bean.getClass());
        if(null != classWithValidation) {
            final MethodWithValidation methodWithValidation = classWithValidation.findByKey(method);
            if(null != methodWithValidation) {
                for(ParamWithValidation param : methodWithValidation.params()) {
                    ValidationError violation = ValidationExecutor.INSTANCE.validateParam(methodWithValidation.method(), param, getValue(param, parameters));
                    if(null != violation) {
                        set.add(violation);
                    }
                }
            }
        }

        return set;
    }

    private String getValue(ParamWithValidation param, List<String> parameters) {
        String field = parameters.get(param.index).trim();
        if(field.startsWith("{") && field.endsWith("}")) {
            try {
                Map map = objectMapper.readValue(field, Map.class);
                return map.getOrDefault(param.field, "").toString();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return field;
    }

    record ValidationList(List<ClassWithValidation> classes) {
        public ClassWithValidation findByKey(Class className) {
            String name = className.getName();
            return findByClassName(name);
        }

        public ClassWithValidation findByClassName(String className) {
            for(ClassWithValidation clazz : classes) {
                if(clazz.clazz.equals(className)) {
                    return clazz;
                }
            }
            return null;
        }

        public void add(Class beanClass, Method method, int i) {
            ClassWithValidation clazz = findByKey(beanClass);
            if(clazz == null) {
                final ClassWithValidation newClazz = new ClassWithValidation(beanClass.getName(), new ArrayList<>());
                newClazz.add(method, i);
                classes.add(newClazz);
            } else {
                clazz.add(method, i);
            }
        }

        public List<FrontEndValidation> findFrontEndValidationsForController(String controller) {
            List<FrontEndValidation> list = new ArrayList<>();
            ClassWithValidation clazz = findByClassName(controller);

            if(null != clazz) {
                for (MethodWithValidation method : clazz.methods) {
                    for (ParamWithValidation param : method.params) {
                        String fieldName = param.field;

                        for (Validation validationDefinition : param.validations) {
                            String validation = validationDefinition.type().getSimpleName();
                            String message = validationDefinition.message;
                            list.add(new FrontEndValidation(method.method(), fieldName, validation, message, validationDefinition.value(), validationDefinition.value2()));
                        }
                    }
                }
            }

            return list;
        }
    }

    public static class FrontEndValidation {

        private final String formContext;
        private final String field;
        private final String validation;
        private String message;

        private String value1;
        private String value2;

        public FrontEndValidation(String formContext, String field, String validation, String message, String value1, String value2) {
            this.formContext = formContext;
            this.field = field;
            this.validation = validation;
            //TODO if Pattern, make sure we suggest UTF-8 checks vs [a-Z]
            this.message = message;
            this.value1 = value1;
            this.value2 = value2;
        }

        public String getField() {
            return field;
        }

        public String getValidation() {
            return validation;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }
    }

    record ClassWithValidation(String clazz, List<MethodWithValidation> methods) {

        public MethodWithValidation findByKey(Method method) {
            for(MethodWithValidation m : methods) {
                if(m.method.equals(method.getName())) {
                    return m;
                }
            }
            return null;
        }

        public void add(Method method, int i) {
            MethodWithValidation m = findByKey(method);
            if(m == null) {
                final MethodWithValidation newMethod = new MethodWithValidation(method.getName(), new ArrayList<>());
                newMethod.add(method, i);
                methods.add(newMethod);
            } else {
                m.add(method, i);
            }
        }

        public MethodWithValidation findByKey(String method) {
            for(MethodWithValidation m : methods) {
                if(m.method.equals(method)) {
                    return m;
                }
            }
            return null;
        }
    }

    record MethodWithValidation(String method, List<ParamWithValidation> params) {
        public void add(Method method, int i) {
            Class paramType = method.getParameterTypes()[i];
            if(paramType.isPrimitive()) {
                //TODO
            } else {
                Field[] declaredFields = paramType.getDeclaredFields();
                for (Field field : declaredFields) {
                    ParamWithValidation paramWithValidation = new ParamWithValidation(field.getName(), i, new ArrayList<>());
                    paramWithValidation.validations().addAll(AnnotationToValidation.findValidations(field));
                    if (!paramWithValidation.validations.isEmpty()) {
                        params.add(paramWithValidation);
                    }
                }
            }
        }
    }

    public record ParamWithValidation(String field, int index, List<Validation> validations) {
        public boolean contains(Class clazz) {
            final String className = clazz.getName();
            return validations.contains(className);
        }
    }

    public record Validation(Class type, String value, String value2, String message) {}
}
