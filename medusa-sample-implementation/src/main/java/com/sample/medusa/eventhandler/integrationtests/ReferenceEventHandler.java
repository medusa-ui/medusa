package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UIEventPage(path = "/test/reference", file = "pages/integration-tests/reference")
public class ReferenceEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ReferenceEventHandler.class);

    public PageAttributes setupAttributes() {
        return new PageAttributes()
                .with("first", 1)
                .with("second", 2)
                .with("result" , "1 + 2 = 3");
    }

    public DOMChanges calc(String firstValue, String secondValue, String operation) {
        logger.debug("calc({}, {}, {})", firstValue, secondValue, operation);
        int first = 0;
        int second = 0;
        if(firstValue != null) first = Integer.parseInt(firstValue);
        if(secondValue != null) second = Integer.parseInt(secondValue);
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
