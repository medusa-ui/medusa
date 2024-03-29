package io.getmedusa.medusa.core.boot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.getmedusa.medusa.core.annotation.MaxFileSize;
import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.session.StandardSessionTagKeys;
import io.getmedusa.medusa.core.util.SpELUtils;
import io.getmedusa.medusa.core.validation.ValidationError;
import io.getmedusa.medusa.core.validation.ValidationExecutor;
import io.getmedusa.medusa.core.validation.ValidationMessageResolver;
import jakarta.validation.Valid;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.unit.DataSize;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
        if(controller == null) return "[]";
        final String path = session.getTag(StandardSessionTagKeys.ROUTE);
        return frontendValidationsCache.get(path, c -> {
            try {
                List<FrontEndValidation> frontEndValidations = resolver.resolveMessages(classesWithValidMethods.findFrontEndValidationsForController(controller));
                findFragmentsForController(session, resolver, controller, frontEndValidations);
                return objectMapper.writeValueAsString(frontEndValidations);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void findFragmentsForController(Session session, ValidationMessageResolver resolver, String controller, List<FrontEndValidation> frontEndValidations) {
        List<String> controllerFragments = findFragmentsForController(session, controller);
        for(String controllerFragment : controllerFragments) {
            List<FrontEndValidation> additionalFrontEndValidations = resolver.resolveMessages(classesWithValidMethods.findFrontEndValidationsForController(controllerFragment));
            frontEndValidations.addAll(additionalFrontEndValidations);
        }
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

    public void consider(Object bean, ValidationMessageResolver resolver) {
        Class beanClass = bean.getClass();
        for (Method method : beanClass.getMethods()) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0, parameterAnnotationsLength = parameterAnnotations.length; i < parameterAnnotationsLength; i++) {
                Annotation[] paramAnnotations = parameterAnnotations[i];
                for (Annotation paramAnnotation : paramAnnotations) {
                    if (paramAnnotation.annotationType().equals(Valid.class)) {
                        classesWithValidMethods.add(beanClass, method, i, resolver);
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

    public String getValue(ParamWithValidation param, List<String> parameters) {
        String field = parameters.get(param.index).trim();
        if(field.startsWith("{") && field.contains("}")) {
            String objectPart = field.substring(0, field.indexOf("}") + 1);
            Map map = SPEL_EXPRESSION_PARSER.parseExpression(objectPart).getValue(Map.class);
            return map.getOrDefault(param.field, "").toString();
        }
        return field;
    }

    public long getMaxFileSize(Session session) {
        final String controller = session.getTag(StandardSessionTagKeys.CONTROLLER);
        Long validation = findMaxFileSizeByController(controller);
        if (validation != null) return validation;

        if(!session.getFragments().isEmpty()) {
            for(String fragment : session.getFragments()) {
                Long fragmentValidation = findMaxFileSizeByController(fragment);
                if (fragmentValidation != null) return fragmentValidation;
            }
        }

        return DataSize.ofMegabytes(10).toBytes(); //default
    }

    private Long findMaxFileSizeByController(String controller) {
        ClassWithValidation classWithValidation = classesWithValidMethods.findByClassName(controller);
        if(classWithValidation != null) {
            MethodWithValidation uploadChunk = classWithValidation.findByKey("uploadChunk");
            if(uploadChunk != null) {
                for (ParamWithValidation param : uploadChunk.params()) {
                    if (param.field().equals("dataChunk")) {
                        for (Validation validation : param.validations()) {
                            if (validation.type.equals(MaxFileSize.class)) {
                                return DataSize.parse(validation.value()).toBytes();
                            }
                        }
                    }
                }
            }
        }
        return null;
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

        public void add(Class beanClass, Method method, int i, ValidationMessageResolver resolver) {
            ClassWithValidation clazz = findByKey(beanClass);
            if(clazz == null) {
                final ClassWithValidation newClazz = new ClassWithValidation(beanClass.getName(), new ArrayList<>());
                newClazz.add(method, i, resolver);
                classes.add(newClazz);
            } else {
                clazz.add(method, i, resolver);
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

        public void add(Method method, int i, ValidationMessageResolver resolver) {
            MethodWithValidation m = findByKey(method);
            if(m == null) {
                final MethodWithValidation newMethod = new MethodWithValidation(method.getName(), new ArrayList<>());
                newMethod.add(method, i, resolver);
                methods.add(newMethod);
            } else {
                m.add(method, i, resolver);
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
        public void add(Method method, int i, ValidationMessageResolver resolver) {
            Class<?> paramType = method.getParameterTypes()[i];
            List<Annotation> paramAnnotations = findValidationsOnDirectParam(method.getParameterAnnotations()[i]);
            if(!paramAnnotations.isEmpty()) {
                Parameter parameter = method.getParameters()[i];
                ParamWithValidation paramWithValidation = new ParamWithValidation(parameter.getName(), i, new ArrayList<>());
                paramWithValidation.validations().addAll(AnnotationToValidation.findValidations(parameter, resolver));
                if (!paramWithValidation.validations.isEmpty()) {
                    params.add(paramWithValidation);
                }
            } else {
                Field[] declaredFields = paramType.getDeclaredFields();
                for (Field field : declaredFields) {
                    ParamWithValidation paramWithValidation = new ParamWithValidation(field.getName(), i, new ArrayList<>());
                    paramWithValidation.validations().addAll(AnnotationToValidation.findValidations(field, resolver));
                    if (!paramWithValidation.validations.isEmpty()) {
                        params.add(paramWithValidation);
                    }
                }
            }
        }

        private List<Annotation> findValidationsOnDirectParam(Annotation[] parameterAnnotation) {
            if(parameterAnnotation.length == 0) return List.of();
            List<Annotation> annotations = new ArrayList<>();
            for(Annotation annotation : parameterAnnotation) {
                if(annotation.annotationType().getPackageName().startsWith("jakarta.validation.constraints") ||
                        "io.getmedusa.medusa.core.annotation".equals(annotation.annotationType().getPackageName())) {
                    annotations.add(annotation);
                }
            }
            return annotations;
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
