package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;

@UIEventPage(path = "/test/refs", file = "pages/integration-tests/reference")
public class ReferenceEventHandler implements UIEventController {
    private static final Logger logger = LoggerFactory.getLogger(ReferenceEventHandler.class);

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        return new PageAttributes()
                .with("first", 1)
                .with("second", 2)
                .with("result" , "1 + 2 = 3");
    }

    public DOMChanges calc(int first, int second, String operation) {
        logger.debug("calc(" + first + ", " + second + ", '" + operation + "')");
        if(null != operation) {
            if (operation.equals("add")) return sum(first, second);
            if (operation.equals("minus")) return minus(first, second);
            if (operation.equals("multiply")) return multiply(first, second);
        }
        return DOMChanges
                .of("result", "invalid");
    }

    public DOMChanges minus(int first, int second) {
        String result = first +  " - " + second + " = " + (first - second);
        return DOMChanges
                .of("result", result);
    }

    public DOMChanges sum(int first, int second) {
        String result = first +  " + " + second + " = " + (first + second);
        return DOMChanges
                .of("result", result);
    }

    public DOMChanges multiply(int first, int second) {
        String result = first +  " x " + second + " = " + (first * second);
        return DOMChanges
                .of("result", result);
    }
}
