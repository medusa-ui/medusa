package io.getmedusa.medusa.core;

import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.ExpressionEval;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ActionsWithOptionalParamsTest {

    @Autowired
    MyController myController;

    @Test
    void executeEventWithoutOptionalParameters() {
        String event = rebuildEventWithOptionalParams("exampleWithoutSecurityButWithParam(7)");
        DOMChanges result = ExpressionEval.evalEventController(event, myController);

        Assertions.assertNotNull(result, "Expected to get a result back");
        Assertions.assertEquals("exampleWithoutSecurityButWithParam: 7", getVal(result));
    }

    @Test
    void executeEventWithOptionalParameters() {
        String event = rebuildEventWithOptionalParams("exampleWithSecurityAndParam(9, 6)");

        DOMChanges result = ExpressionEval.evalEventController(event, myController);

        Assertions.assertNotNull(result, "Expected to get a result back");
        Assertions.assertEquals("exampleWithSecurity and session and param: 9x6", getVal(result));
    }

    private String rebuildEventWithOptionalParams(String event) {
        String methodName = determineMethodName(event);
        Method method = determineMethod(methodName);
        if(method != null) event = rebuildEventWithOptionalParams(event, methodName, method, null, null);
        return event;
    }

    private String rebuildEventWithOptionalParams(String event, String methodName, Method method, SecurityContext securityContext, WebSocketSession session) {
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
                parameterBuilder.append("null");
            } else if(paramType.equals(WebSocketSession.class)) {
                parameterBuilder.append("null");
            } else {
                parameterBuilder.append(existingParameters[regularParamIndex++]);
            }
            isFirst = false;
        }
        parameterBuilder.append(")");
        return parameterBuilder.toString();
    }

    private String determineMethodName(String event) {
        String methodName = event;
        if(event.contains("(")) {
            methodName = event.substring(0, event.indexOf('('));
        }
        return methodName;
    }

    private String[] determineExistingParams(String event) {
        return event.substring(event.indexOf('(') + 1, event.indexOf(')')).split(",");
    }

    private Method determineMethod(String methodName) {
        Method[] methods = myController.getClass().getMethods();
        for(Method method : methods) {
            if(methodName.equals(method.getName())) return method;
        }
        return null;
    }

    private String getVal(DOMChanges domChanges) {
        return domChanges.build().get(0).getV().toString();
    }

}

@Component
class MyController {

    public DOMChanges exampleWithoutSecurityButWithParam(Integer test) {
        return DOMChanges.of("result", "exampleWithoutSecurityButWithParam: " + test);
    }

    public DOMChanges exampleWithSecurityAndParam(SecurityContext securityContext, Integer test, WebSocketSession session, Integer test2) {
        return DOMChanges.of("result", "exampleWithSecurity and session and param: " + test + "x" + test2);
    }

    public DOMChanges exampleWithSecurity(SecurityContext securityContext, WebSocketSession session) {
        return DOMChanges.of("result", "exampleWithSecurity and session");
    }

}