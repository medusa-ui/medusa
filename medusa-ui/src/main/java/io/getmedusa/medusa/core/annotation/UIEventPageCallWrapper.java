package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UIEventPageCallWrapper {

    private static final Logger logger = LoggerFactory.getLogger(UIEventPageCallWrapper.class);

    private static final String DEFAULT_SETUP = "setupAttributes";

    private Method setupAttributesMethod;
    private SetupAttributesMethodType setupAttributesMethodType = SetupAttributesMethodType.NONE;
    private Object controller;
    private Method exitMethod;

    public UIEventPageCallWrapper(Object controller) {
        this.controller = controller;
        UIEventPage annotation = getAnnotation(controller);
        Method matchingMethod = determineSetupMethod(controller, annotation);
        if(null != matchingMethod) {
            this.setupAttributesMethod = matchingMethod;
            this.setupAttributesMethodType = setupMethodType(controller, matchingMethod, Arrays.asList(matchingMethod.getParameterTypes()));
        } else if(annotation != null && !DEFAULT_SETUP.equals(annotation.setup())){
            throw new IllegalArgumentException("Setup method '" + annotation.setup() + "' does not exist on " + controller.getClass());
        }

        this.exitMethod = determineExitMethod(controller);
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
    public List<Attribute> setupAttributes(ServerRequest request, Session session){
        logger.debug("request {} {}", request, setupAttributesMethodType);
        try {
            return (List<Attribute>) switch (setupAttributesMethodType) {
                case EMPTY ->  setupAttributesMethod.invoke(controller);
                case REQUEST -> setupAttributesMethod.invoke(controller, request);
                case SECURED -> setupAttributesMethod.invoke(controller, session);
                case REQUEST_SECURED -> setupAttributesMethod.invoke(controller, request, session);
                case SECURED_REQUEST -> setupAttributesMethod.invoke(controller, session, request);
                default -> new ArrayList<>();
            };
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("setup attributes failed due to: " + e.getMessage(), e);
            throw new IllegalStateException("setup attributes failed", e);
        }
    }

    private UIEventPage getAnnotation(Object controller) {
        if(controller == null) { return null; }
        return controller.getClass().getAnnotation(UIEventPage.class);
    }

    private Method determineSetupMethod(Object uiEventPageObject, UIEventPage uiEventPage) {
        if(uiEventPage == null) return null;
        String setupMethodName = uiEventPage.setup();
        for(Method method : uiEventPageObject.getClass().getMethods()) {
            if (method.getName().equals(setupMethodName)) {
                if (!returningAttributes(method)) {
                    throw new RuntimeException(uiEventPageObject.getClass().getName() + "." + method.getName() + " should return a List<Attribute> but was " + method.getReturnType());
                }
                return method;
            }
        }
        return null;
    }

    private SetupAttributesMethodType setupMethodType(Object uiEventPageObject, Method matchingMethod, List<Class<?>> parameterTypes) {
        if(parameterTypes.isEmpty()) {
            return SetupAttributesMethodType.EMPTY;
        } else if( parameterTypes.size() == 1 && secureContextClass(parameterTypes.get(0))){
            return SetupAttributesMethodType.SECURED;
        } else if( parameterTypes.size() == 1 && serverRequestClass(parameterTypes.get(0))){
            return SetupAttributesMethodType.REQUEST;
        } else if( parameterTypes.size() == 2 && serverRequestClass(parameterTypes.get(0)) && secureContextClass(parameterTypes.get(1))){
            return SetupAttributesMethodType.REQUEST_SECURED;
        } else if( parameterTypes.size() == 2 && serverRequestClass(parameterTypes.get(1)) && secureContextClass(parameterTypes.get(0))){
            return SetupAttributesMethodType.SECURED_REQUEST;
        } else {
            throw new RuntimeException(uiEventPageObject.getClass().getName() + "." + matchingMethod.getName() + " only parameters of type ServerRequest and SecurityContext accepted");
        }
    }

    private Method determineExitMethod(Object uiEventPageObject) {
        if(uiEventPageObject == null) { return null; }
        for(Method method : uiEventPageObject.getClass().getMethods()) {
            if ("exit".equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    private boolean returningAttributes(Method method){
        return method.getReturnType().isAssignableFrom(List.class);
    }

    private boolean secureContextClass(Class<?> clazz){
        return clazz.isAssignableFrom(Session.class);
    }

    private boolean serverRequestClass(Class<?> clazz){
        return clazz.isAssignableFrom(ServerRequest.class);
    }

    public String toFQDN() {
        return controller.getClass().getName();
    }

    public Object getController() {
        return controller;
    }

    private enum SetupAttributesMethodType {
        NONE, EMPTY, REQUEST, SECURED, REQUEST_SECURED, SECURED_REQUEST;
    }

}
