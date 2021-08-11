package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.util.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class UIEventController implements UIEventWithAttributes {
    private static final Logger logger = LoggerFactory.getLogger(UIEventController.class);
    private Object uiEventPageController;
    private Method setupAttributes;
    private Type type = Type.NONE;

    public UIEventController(Object uiEventPageObject) {
        this.uiEventPageController = uiEventPageObject;
        Class<?> uiEventPageObjectClass = uiEventPageObject.getClass();
        UIEventPage uiEventPage = uiEventPageObjectClass.getAnnotation(UIEventPage.class);
        String setupMethodName = uiEventPage.setup();
        for(Method method : uiEventPageObjectClass.getMethods()) {
            if(method.getName().equals(setupMethodName)){
                if(!returningPageAttributes(method)){
                    throw new RuntimeException(uiEventPageObject.getClass().getName() + "." + method.getName() + " should return PageAttributes but was " + method.getReturnType().getClass());
                }
                List<Class<?>> parameterTypes = Arrays.asList(method.getParameterTypes());
                this.setupAttributes = method;

                if(parameterTypes.isEmpty()) {
                    type = Type.EMPTY;
                } else if( parameterTypes.size() == 1  && secureContextClass(parameterTypes.get(0))){
                    type = Type.SECURED;
                } else if( parameterTypes.size() == 1  && serverRequestClass(parameterTypes.get(0))){
                    type = Type.REQUEST;
                } else if( parameterTypes.size() == 2  && serverRequestClass(parameterTypes.get(0)) && secureContextClass(parameterTypes.get(1))){
                    type = Type.REQUEST_SECURED;
                } else if( parameterTypes.size() == 2  && serverRequestClass(parameterTypes.get(1)) && secureContextClass(parameterTypes.get(0))){
                    type = Type.SECURED_REQUEST;
                } else {
                    throw new RuntimeException(uiEventPageObject.getClass().getName() + "." + method.getName() + " only parameters of type ServerRequest and SecurityContext accepted");
                }
            }
        }
        logger.debug("init {}, with path: '{}', file: '{}' and setup: '{}' is of type '{}'", uiEventPageObject.getClass().getSimpleName(),uiEventPage.path(), uiEventPage.file(), uiEventPage.setup() ,type);
    }

    private boolean returningPageAttributes(Method method){
        return method.getReturnType().getName()
                     .equals(PageAttributes.class.getName());
    }

    private boolean secureContextClass(Class<?> clazz){
        return clazz.getName()
                    .equals(SecurityContext.class.getName());
    }

    private boolean serverRequestClass(Class<?> clazz){
        return clazz.getName()
                    .equals(ServerRequest.class.getName());
    }

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext){
        logger.debug("request {} {}", request, type);
        try {
            switch (type) {
                case NONE:
                    return new PageAttributes();
                case EMPTY:
                    return (PageAttributes) setupAttributes.invoke(uiEventPageController);
                case REQUEST:
                    return (PageAttributes) setupAttributes.invoke(uiEventPageController, request);
                case SECURED:
                    return (PageAttributes) setupAttributes.invoke(uiEventPageController, securityContext);
                case REQUEST_SECURED:
                    return (PageAttributes) setupAttributes.invoke(uiEventPageController, request, securityContext);
                case SECURED_REQUEST:
                    return (PageAttributes) setupAttributes.invoke(uiEventPageController, securityContext, request);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("setup attributes failed due to: " + e.getMessage(), e );
        }
        return null;
    }

    public Object getUiEventPageController() {
        return uiEventPageController;
    }

    public enum Type {
        NONE, EMPTY, REQUEST, SECURED, REQUEST_SECURED, SECURED_REQUEST;
    }
}
