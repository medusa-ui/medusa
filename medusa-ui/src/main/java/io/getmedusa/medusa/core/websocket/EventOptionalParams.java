package io.getmedusa.medusa.core.websocket;

import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class EventOptionalParams {

    public static boolean securityEnabled = false;
    private EventOptionalParams() {}

    public static String rebuildEventWithOptionalParams(Class<?> clazz, WebSocketSession session, String event) {
        String methodName = determineMethodName(event);
        Method method = determineMethod(clazz, methodName);
        if(method != null) event = rebuildEventWithOptionalParams(event, methodName, method, session);
        return event;
    }

    private static String rebuildEventWithOptionalParams(String event, String methodName, Method method, WebSocketSession session) {
        List<Class<?>> paramTypes = Arrays.asList(method.getParameterTypes());
        if(!(paramTypes.contains(SecurityContext.class) || paramTypes.contains(WebSocketSession.class))) return event;

        String[] existingParameters = determineExistingParams(event);

        StringBuilder parameterBuilder = new StringBuilder(methodName);
        parameterBuilder.append("(");
        int regularParamIndex = 0;
        boolean isFirst = true;
        for(Class<?> paramType : paramTypes) {
            if(!isFirst) {
                parameterBuilder.append(",");
            }
            if(paramType.equals(SecurityContext.class)) {
                if(securityEnabled) {
                    parameterBuilder.append("T(io.getmedusa.medusa.core.registry.ActiveSessionRegistry).getInstance().getSecurityContextById(\"_\")".replace("_", session.getId()));
                } else {
                    parameterBuilder.append("null");
                }
            } else if(paramType.equals(WebSocketSession.class)) {
                parameterBuilder.append("T(io.getmedusa.medusa.core.registry.ActiveSessionRegistry).getInstance().getWebsocketByID(\"_\")".replace("_", session.getId()));
            } else {
                parameterBuilder.append(existingParameters[regularParamIndex++]);
            }
            isFirst = false;
        }
        parameterBuilder.append(")");
        return parameterBuilder.toString();
    }

    private static String determineMethodName(String event) {
        String methodName = event;
        if(event.contains("(")) {
            methodName = event.substring(0, event.indexOf('('));
        }
        return methodName;
    }

    private static String[] determineExistingParams(String event) {
        return event.substring(event.indexOf('(') + 1, event.indexOf(')')).split(",");
    }

    private static Method determineMethod(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        for(Method method : methods) {
            if(methodName.equals(method.getName())) return method;
        }
        return null;
    }

}
