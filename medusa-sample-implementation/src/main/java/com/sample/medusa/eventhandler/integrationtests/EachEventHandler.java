package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UIEventPage(file = "pages/integration-tests/each.html", path = "/each")
public class EachEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(EachEventHandler.class);
    List<String> first = new ArrayList<>(Arrays.asList("f1", "f2", "f3"));
    List<String> second = new ArrayList<>(Arrays.asList("s1", "s2", "s3", "s4"));
    List<String> third = new ArrayList<>(Arrays.asList("t1", "t2"));

    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        return new PageAttributes()
                .with("first", first)
                .with("second", second)
                .with("third",third);
    }

    public DOMChanges first(){
        int next = first.size() + 1;
        first.add("f" + next);
        logger.debug("next:{}, first:{}", next, first);
        return DOMChanges.of("first", first);
    }

    public DOMChanges second(){
        int next = second.size() + 1;
        second.add("s" + next);
        logger.debug("next:{}, second:{}", next, second);
        return DOMChanges.of("second", second);
    }

    public DOMChanges third(){
        int next = third.size() + 1;
        third.add("t" + next);
        logger.debug("next:{}, third:{}", next, third);
        return DOMChanges.of("third", third);
    }

}
