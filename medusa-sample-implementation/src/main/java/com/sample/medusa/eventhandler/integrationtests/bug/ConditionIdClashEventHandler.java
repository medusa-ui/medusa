package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

@UIEventPage(file = "pages/integration-tests/bug/conditional-id-clash.html", path = "/test/bug/conditional-id-clash")
public class ConditionIdClashEventHandler {

    List<Expression> products = List.of(
            new Expression("1 == 1",  true),
            new Expression("'abc' == 'abc'",  true),
            new Expression("true == 1",  false),
            new Expression("false == false",  true),
            new Expression("true != true",  false)
    );

    public PageAttributes setupAttributes(ServerRequest request){
        return new PageAttributes()
                .with("expressions", products)
                .with("allTrue", request.queryParam("allTrue").orElse("false"));
    }

    public DOMChanges toggleShow(boolean show) {
        return DOMChanges.of("allTrue", show);
    }

}

record Expression(String expression, boolean result) { }
