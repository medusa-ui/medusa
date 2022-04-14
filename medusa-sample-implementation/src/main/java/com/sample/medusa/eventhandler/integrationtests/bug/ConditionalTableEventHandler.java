package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

@UIEventPage(path="/test/bug/conditional-table", file = "pages/integration-tests/bug/conditional-table")
public class ConditionalTableEventHandler {

    List<Expression> expressions = List.of(
            new Expression("1 === 1",  true),
            new Expression("'abc' === 'abc'",  true),
            new Expression("true === 1",  false),
            new Expression("!false !== true",  false),
            new Expression("false === false",  true),
            new Expression("true !== true",  false)
    );

    public PageAttributes setupAttributes(ServerRequest request){
        return new PageAttributes()
                .with("expressions", expressions)
                .with("filtered", request.queryParam("filtered").orElse("false"));
    }

    public DOMChanges filtered(boolean filtered) {
        return DOMChanges.of("filtered", filtered);
    }
}
