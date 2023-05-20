package io.getmedusa.medusa.core.boot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.session.StandardSessionTagKeys;
import io.getmedusa.medusa.core.util.SpELUtils;
import io.getmedusa.medusa.core.validation.ValidationError;
import io.getmedusa.medusa.core.validation.ValidationExecutor;
import io.getmedusa.medusa.core.validation.ValidationMessageResolver;
import jakarta.validation.Valid;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public enum ValidationDetection {

    INSTANCE;

    public static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ValidationList classesWithValidMethods = new ValidationList(new ArrayList<>());

    private Cache<String, String> frontendValidationsCache = Caffeine.newBuilder()
            .maximumSize(2000L)
            .build();

    /*
    * on boot //
    * look through all controllers and find each of their templates (maybe this is already done)
    * if the template has a m:fragment="...", we need to store the relevant controller as ROOT
    * if the template has a m:ref="...", we need to store the relevant controller as REF
    *
    *
    * on render //
    * if we render a ROOT, and thus check its buildFrontendValidations with ROOT as String controller
    * then we need to see if any controllers are related as REF
    * if they are, we need to also go over them and attach these as FrontendValidations
    */
    public String buildFrontendValidations(Session session, ValidationMessageResolver resolver) {
        final String controller = session.getTag(StandardSessionTagKeys.CONTROLLER);
        return frontendValidationsCache.get(controller, c -> {
            try {
                List<FrontEndValidation> frontEndValidations = resolver.resolveMessages(classesWithValidMethods.findFrontEndValidationsForController(controller));
                findFragmentsForController(session, controller);
                return objectMapper.writeValueAsString(frontEndValidations);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<String> findFragmentsForController(Session session, String controller) {
        List<String> refsRelatedToController = FragmentDetection.INSTANCE.getRootFragmentsUsed().getOrDefault(controller, List.of());
        List<String> fragmentControllers = new ArrayList<>();
        for(String refRelatedToController : refsRelatedToController) {
            String parsedRef = SpELUtils.parseExpression(refRelatedToController, session);
            UIEventPageCallWrapper wrapper = RefDetection.INSTANCE.getRefToBeanMap().getOrDefault(parsedRef, null);
            if(wrapper != null) {
                fragmentControllers.add(wrapper.getController().getClass().getName());
            }
        }
        return fragmentControllers;
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
            Map map = SPEL_EXPRESSION_PARSER.parseExpression(field).getValue(Map.class);
            return map.getOrDefault(param.field, "").toString();
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

        public String getFormContext() {
            return formContext;
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
