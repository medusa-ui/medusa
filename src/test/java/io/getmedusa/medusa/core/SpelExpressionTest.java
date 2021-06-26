package io.getmedusa.medusa.core;

import io.getmedusa.medusa.core.injector.DOMChange;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

@SpringBootTest
public class SpelExpressionTest {

    @Autowired SpelController myController;

    @Test
    public void ctrlSay() {
        // given
        String tag = "<button m:click='say(\"Hallo World\", 3)' /> ";
        SpelExpressionParser parser = new SpelExpressionParser();

        // when
        List<DOMChange> list = (List<DOMChange>) parser.parseExpression(fakeParsingTag(tag)).getValue(myController);
        System.out.println(list.get(0).getV());

        // then
        assertEquals("Hallo World, Hallo World, Hallo World", list.get(0).getV());
    }

    String fakeParsingTag(String tag){
        return tag.split("'")[1];
    }

}

@Component
class SpelController {

    public List<DOMChange> say(String message, Integer times) {
        StringBuilder sb = new StringBuilder();
        String appender = "";
        for (int i = 0; i < times; i++) {
            sb.append(appender);
            sb.append(message );
            appender = ", ";
        }
        return Collections.singletonList(new DOMChange("message", sb.toString()));
    }

}