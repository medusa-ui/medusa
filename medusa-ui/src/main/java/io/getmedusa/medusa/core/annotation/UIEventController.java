package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.util.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static io.getmedusa.medusa.core.annotation.UIEventController.SetupAttributesMethodType.*;

public class UIEventController implements UIEventWithAttributes {
    private static final Logger logger = LoggerFactory.getLogger(UIEventController.class);

    private static final String DEFAULT_SETUP = "setupAttributes";

    private Method setupAttributesMethod;
    private SetupAttributesMethodType setupAttributesMethodType = NONE;
    private final Object eventHandler;

    public UIEventController(Object eventHandler) {
        this.eventHandler = eventHandler;
        UIEventPage uiEventPage = eventHandler.getClass().getAnnotation(UIEventPage.class);
        Method matchingMethod = determineSetupMethod(eventHandler, uiEventPage);
        if(null != matchingMethod) {
            this.setupAttributesMethod = matchingMethod;
            this.setupAttributesMethodType = setupMethodType(eventHandler, matchingMethod, Arrays.asList(matchingMethod.getParameterTypes()));
        } else {
            if(!DEFAULT_SETUP.equals(uiEventPage.setup())) throw new IllegalArgumentException("Setup method '" + uiEventPage.setup() + "' does not exist on " + eventHandler.getClass());
        }
    }

    private SetupAttributesMethodType setupMethodType(Object uiEventPageObject, Method matchingMethod, List<Class<?>> parameterTypes) {
        if(parameterTypes.isEmpty()) {
            return EMPTY;
        } else if( parameterTypes.size() == 1 && secureContextClass(parameterTypes.get(0))){
            return SECURED;
        } else if( parameterTypes.size() == 1 && serverRequestClass(parameterTypes.get(0))){
            return REQUEST;
        } else if( parameterTypes.size() == 2 && serverRequestClass(parameterTypes.get(0)) && secureContextClass(parameterTypes.get(1))){
            return REQUEST_SECURED;
        } else if( parameterTypes.size() == 2 && serverRequestClass(parameterTypes.get(1)) && secureContextClass(parameterTypes.get(0))){
            return SECURED_REQUEST;
        } else {
            throw new RuntimeException(uiEventPageObject.getClass().getName() + "." + matchingMethod.getName() + " only parameters of type ServerRequest and SecurityContext accepted");
        }
    }

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext){
        logger.debug("request {} {}", request, setupAttributesMethodType);
        try {
            switch (setupAttributesMethodType) {
                case EMPTY:
                    return (PageAttributes) setupAttributesMethod.invoke(eventHandler);
                case REQUEST:
                    return (PageAttributes) setupAttributesMethod.invoke(eventHandler, request);
                case SECURED:
                    return (PageAttributes) setupAttributesMethod.invoke(eventHandler, securityContext);
                case REQUEST_SECURED:
                    return (PageAttributes) setupAttributesMethod.invoke(eventHandler, request, securityContext);
                case SECURED_REQUEST:
                    return (PageAttributes) setupAttributesMethod.invoke(eventHandler, securityContext, request);
                default:
                    return new PageAttributes();
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("setup attributes failed due to: " + e.getMessage(), e );
        }
        return null;
    }

    private Method determineSetupMethod(Object uiEventPageObject, UIEventPage uiEventPage) {
        String setupMethodName = uiEventPage.setup();
        for(Method method :  uiEventPageObject.getClass().getMethods()) {
            if (method.getName().equals(setupMethodName)) {
                if (!returningPageAttributes(method)) {
                    throw new RuntimeException(uiEventPageObject.getClass().getName() + "." + method.getName() + " should return PageAttributes but was " + method.getReturnType());
                }
                return method;
            }
        }
        return null;
    }

    private boolean returningPageAttributes(Method method){
        return method.getReturnType().isAssignableFrom(PageAttributes.class);
    }

    private boolean secureContextClass(Class<?> clazz){
        return clazz.isAssignableFrom(SecurityContext.class);
    }

    private boolean serverRequestClass(Class<?> clazz){
        return clazz.isAssignableFrom(ServerRequest.class);
    }

    public Object getEventHandler() {
        return eventHandler;
    }

    public enum SetupAttributesMethodType {
        NONE, EMPTY, REQUEST, SECURED, REQUEST_SECURED, SECURED_REQUEST;
    }
}
