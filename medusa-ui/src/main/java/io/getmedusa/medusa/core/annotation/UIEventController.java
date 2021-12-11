package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.util.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static io.getmedusa.medusa.core.annotation.UIEventController.SetupAttributesMethodType.*;

/**
 * <p>
 * This class is a wrapper for a UIEventPage bean used in {@link io.getmedusa.medusa.core.registry.EventHandlerRegistry}
 * as part of {@link UIEventPostProcessor}.
 * </p><p>
 * Note: This class is not a bean, it is a POJO wrapper of a bean
 * </p><p>
 * This wrapper provides a way to reliably call setupAttributes() in internal code without requiring an {@link UIEventWithAttributes} interface
 * in the bean. We can determine up front during post-processing what kind of invocation is required.
 * </p><p>
 * Instead of storing the bean in the registry, we store this wrapper which contains the bean and the kind of invocation required.
 * </p>
 */
public class UIEventController implements UIEventWithAttributes {
    private static final Logger logger = LoggerFactory.getLogger(UIEventController.class);

    private static final String DEFAULT_SETUP = "setupAttributes";

    private Method setupAttributesMethod;
    private SetupAttributesMethodType setupAttributesMethodType = NONE;

    private Method exitMethod;

    private final Object eventHandler;

    /**
     * Wrap the bean and determine its type of setupAttributes() invocation
     * @param eventHandler bean to wrap
     */
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

        this.exitMethod = determineExitMethod(eventHandler);
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

    public void exit(WebSocketSession session) {
        if(this.exitMethod != null) {
            try {
                this.exitMethod.invoke(this.eventHandler, session);
            } catch (Throwable e) {
                logger.error("exit method failed due to: " + e.getMessage(), e );
            }
        }
    }

    /**
     * Standardized way of calling setupAttributes()
     *
     * Uses the pre-determined type of invocation to then internally call the right type of setup method
     *
     * @param request incoming {@link ServerRequest}, optional in use internally
     * @param securityContext incoming {@link SecurityContext}, optional in use internally
     * @return PageAttributes object
     */
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
            throw new IllegalStateException(e);
        }
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

    private Method determineExitMethod(Object uiEventPageObject) {
        for(Method method :  uiEventPageObject.getClass().getMethods()) {
            if ("exit".equals(method.getName())) {
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

    /**
     * Retrieve wrapped event handler bean
     * @return wrapped event handler
     */
    public Object getEventHandler() {
        return eventHandler;
    }

    public enum SetupAttributesMethodType {
        NONE, EMPTY, REQUEST, SECURED, REQUEST_SECURED, SECURED_REQUEST;
    }
}
