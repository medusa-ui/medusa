package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class UIEventPageCallWrapper {

    private static final Logger logger = LoggerFactory.getLogger(UIEventPageCallWrapper.class);

    private static final String DEFAULT_SETUP = "setupAttributes";

    private Method setupAttributesMethod;

    private final Object controller;


    public UIEventPageCallWrapper(Object controller) {
        this.controller = controller;
        UIEventPage annotation = getAnnotation(controller);
        Method matchingMethod = determineSetupMethod(controller, annotation);
        if(null != matchingMethod) {
            this.setupAttributesMethod = matchingMethod;
        } else if(annotation != null && !DEFAULT_SETUP.equals(annotation.setup())){
            throw new IllegalArgumentException("Setup method '" + annotation.setup() + "' does not exist on " + controller.getClass());
        }
    }

    /**
     * Standardized way of calling setupAttributes()
     *
     * Uses the pre-determined type of invocation to then internally call the right type of setup method
     *
     * @param request incoming {@link ServerRequest}, optional in use internally
     * @param session incoming {@link Session}, optional in use internally
     * @return List of Attribute objects
     */
    public Mono<List<Attribute>> setupAttributes(ServerRequest request, Session session){ //TODO
        try {
            if(setupAttributesMethod == null) { return Mono.just(new ArrayList<>()); }

            if(setupAttributesMethod.getParameterCount() == 0) {
                return (Mono<List<Attribute>>) setupAttributesMethod.invoke(controller);
            }

            List<Object> arguments = new ArrayList<>();
            for(Class<?> clazz : setupAttributesMethod.getParameterTypes()) {
                if(isClass(clazz, ServerRequest.class)) {
                    arguments.add(request);
                } else if(isClass(clazz, Session.class)) {
                    arguments.add(session);
                }
            }
            return (Mono<List<Attribute>>) setupAttributesMethod.invoke(controller, arguments.toArray());
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("setup attributes failed due to: " + e.getMessage(), e);
            throw new IllegalStateException("setup attributes failed", e);
        }
    }

    private boolean isClass(Class<?> originClass, Class<?> classToMatch) {
        return classToMatch.isAssignableFrom(originClass);
    }

    private UIEventPage getAnnotation(Object controller) {
        if(controller == null) { return null; }
        return controller.getClass().getAnnotation(UIEventPage.class);
    }

    private Method determineSetupMethod(Object uiEventPageObject, UIEventPage uiEventPage) {
        if(uiEventPage == null) {
            return null;
        }
        String setupMethodName = uiEventPage.setup();
        for(Method method : uiEventPageObject.getClass().getMethods()) {
            if (method.getName().equals(setupMethodName)) {
                if (!returningAttributes(method)) {
                    throw new RuntimeException(uiEventPageObject.getClass().getName() + "." + method.getName() + " should return a Mono<List<Attribute>> or List<Attribute> but was " + method.getReturnType());
                }
                return method;
            }
        }
        return null;
    }

    private boolean returningAttributes(Method method) {
        return method.getGenericReturnType().getTypeName().equals("reactor.core.publisher.Mono<java.util.List<io.getmedusa.medusa.core.attributes.Attribute>>")
        || method.getGenericReturnType().getTypeName().equals("java.util.List<io.getmedusa.medusa.core.attributes.Attribute>");
    }

    public String toFQDN() {
        return controller.getClass().getName();
    }

    public Object getController() {
        return controller;
    }

}
