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
                if(!(method.getReturnType().getName().equals(PageAttributes.class.getName()))){
                    throw new RuntimeException(uiEventPageObject.getClass().getName() + "." + method.getName() + " should return PageAttributes but was " + method.getReturnType().getClass());
                }
                List<Class<?>> parameterTypes = Arrays.asList(method.getParameterTypes());
                this.setupAttributes = method;

                if(parameterTypes.isEmpty()) {
                    type = Type.EMPTY;
                } else if( parameterTypes.size() == 1  && parameterTypes.get(0).getName().equals(SecurityContext.class.getName())){
                    type = Type.SECURED;
                } else if( parameterTypes.size() == 1  && parameterTypes.get(0).getName().equals(ServerRequest.class.getName())){
                    type = Type.REQUEST;
                } else if( parameterTypes.size() == 2  && parameterTypes.get(0).getName().equals(ServerRequest.class.getName()) && parameterTypes.get(1).getName().equals(SecurityContext.class.getName())){
                    type = Type.REQUEST_SECURED;
                } else if( parameterTypes.size() == 2  && parameterTypes.get(1).getName().equals(ServerRequest.class.getName()) && parameterTypes.get(0).getName().equals(SecurityContext.class.getName())){
                    type = Type.SECURED_REQUEST;
                } else {
                    throw new RuntimeException(uiEventPageObject.getClass().getName() + "." + method.getName() + " only parameters of type ServerRequest and SecurityContext accepted");
                }
            }
        }
        logger.debug("init {}, with path: '{}', file: '{}' and setup: '{}' is of type '{}'", uiEventPageObject.getClass().getSimpleName(),uiEventPage.path(), uiEventPage.file(), uiEventPage.setup() ,type);
    }

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext){
        logger.debug("request {} {}",request, type);
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
            e.printStackTrace();
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
