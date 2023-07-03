package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.session.Session;

import java.lang.reflect.Method;
import java.util.*;

public enum MethodDetection {

    INSTANCE;

    private static final String LIST_OF_ATTRIBUTES = "java.util.List<io.getmedusa.medusa.core.attributes.Attribute>";
    private static final String MONO_LIST_OF_ATTRIBUTES = "reactor.core.publisher.Mono<java.util.List<io.getmedusa.medusa.core.attributes.Attribute>>";
    private static final String VOID = "void";

    private static final String ERROR_MESSAGE = "'%s' has multiple callable methods named '%s' that could be mapped to a Medusa action. All callable method names must be unique.";

    private final Map<String, Set<String>> methodsStartWithSession = new HashMap<>();
    private final Map<String, Set<String>> methodsEndWithSession = new HashMap<>();

    public void consider(Object bean) {
        final UIEventPage annotation = retrieveAnnotation(bean);
        Set<String> callableMethods = new HashSet<>();
        if(null != annotation) {
            Class beanClass = bean.getClass();
            for(Method method : beanClass.getMethods()) {
                if(method.getDeclaringClass().equals(beanClass) && isMethodRelevant(annotation, method) && method.getParameterCount() > 0) {
                    String beanClassName = beanClass.getName();
                    String methodName = method.getName();
                    if(callableMethods.contains(methodName)) {
                        throw new IllegalArgumentException(ERROR_MESSAGE.formatted(beanClassName, methodName));
                    }
                    callableMethods.add(methodName);
                    if(firstMethodParamIsSession(method)) {
                        Set<String> methods = methodsStartWithSession.getOrDefault(beanClassName, new HashSet<>());
                        methods.add(methodName);
                        methodsStartWithSession.put(beanClassName, methods);
                    } else if(lastMethodParamIsSession(method)) {
                        Set<String> methods = methodsEndWithSession.getOrDefault(beanClassName, new HashSet<>());
                        methods.add(methodName);
                        methodsEndWithSession.put(beanClassName, methods);
                    }
                }
            }
        }
    }

    public boolean shouldBeginWithSession(String clazz, String method) {
        return methodsStartWithSession.getOrDefault(clazz, new HashSet<>()).contains(method);
    }

    public boolean shouldEndWithSession(String clazz, String method) {
        return methodsEndWithSession.getOrDefault(clazz, new HashSet<>()).contains(method);
    }

    private boolean firstMethodParamIsSession(Method method) {
        return method.getParameterTypes()[0].equals(Session.class);
    }

    private boolean lastMethodParamIsSession(Method method) {
        return method.getParameterTypes()[method.getParameterCount() - 1].equals(Session.class);
    }

    //consider void methods and methods that return a List of Attributes
    private boolean isMethodRelevant(UIEventPage annotation, Method method) {
        final String returnType = method.getAnnotatedReturnType().toString();
        final String methodName = method.getName();

        return !methodName.equals(annotation.setup()) && (LIST_OF_ATTRIBUTES.equals(returnType) || VOID.equals(returnType) || MONO_LIST_OF_ATTRIBUTES.equals(returnType));
    }

    private UIEventPage retrieveAnnotation(Object bean) {
        return bean.getClass().getAnnotation(UIEventPage.class);
    }
}
