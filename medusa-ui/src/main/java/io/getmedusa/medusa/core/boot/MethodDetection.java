package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.session.Session;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum MethodDetection {

    INSTANCE;

    private static final String LIST_OF_ATTRIBUTES = "java.util.List<io.getmedusa.medusa.core.attributes.Attribute>";
    private static final String VOID = "void";

    private final Map<String, List<String>> methodsStartWithSession = new HashMap<>();
    private final Map<String, List<String>> methodsEndWithSession = new HashMap<>();

    public void consider(Object bean) {
        final UIEventPage annotation = retrieveAnnotation(bean);
        if(null != annotation) {
            for(Method method : bean.getClass().getMethods()) {
                if(method.getDeclaringClass().equals(bean.getClass()) && isMethodRelevant(annotation, method) && method.getParameterCount() > 0) {
                    if(firstMethodParamIsSession(method)) {
                        List<String> methods = methodsStartWithSession.getOrDefault(bean.getClass().getName(), new ArrayList<>());
                        methods.add(method.getName());
                        methodsStartWithSession.put(bean.getClass().getName(), methods);
                    } else if(lastMethodParamIsSession(method)) {
                        List<String> methods = methodsEndWithSession.getOrDefault(bean.getClass().getName(), new ArrayList<>());
                        methods.add(method.getName());
                        methodsEndWithSession.put(bean.getClass().getName(), methods);
                    }
                }
            }
        }
    }

    public boolean shouldBeginWithSession(String clazz, String method) {
        return methodsStartWithSession.getOrDefault(clazz, new ArrayList<>()).contains(method);
    }

    public boolean shouldEndWithSession(String clazz, String method) {
        return methodsEndWithSession.getOrDefault(clazz, new ArrayList<>()).contains(method);
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

        return !methodName.equals(annotation.setup()) && (LIST_OF_ATTRIBUTES.equals(returnType) || VOID.equals(returnType));
    }

    private UIEventPage retrieveAnnotation(Object bean) {
        return bean.getClass().getAnnotation(UIEventPage.class);
    }
}
