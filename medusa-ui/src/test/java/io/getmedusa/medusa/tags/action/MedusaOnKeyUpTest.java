package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MedusaOnKeyUpTest extends MedusaTagTest {

    final String onEnter = """
            <input type="text" m:enter="action(:{this})" th:value="${something}"></input>
            """;

    final String onSpaceKey = """
            <input type="text" m:key=" " m:keyup="action(:{this})" th:value="${something}"></input>
            """;

    final String onSpaceKeyCode = """
            <input type="text" m:key="32" m:keyup="action(:{this})" th:value="${something}"></input>
            """;

    final List<Attribute> something = List.of(new Attribute("something","hello world"));

    @Test
    void onEnterTest(){
        String expected = """
                <input type="text" value="hello world" onkeyup="_M.doActionOnKeyUp('Enter', event, '__FRAGMENT__', `action('${this.value}')`)" />
                """;

        // when
        session.setLastParameters(something);
        String template = FluxUtils.dataBufferFluxToString(renderer.render(onEnter, session));

        // then
        logger.debug(template);
        Assertions.assertTrue(template.contains(expected));
    }


    @Test
    void onKeyUpTest(){
        String expectedKey = """
                <input type="text" value="hello world" onkeyup="_M.doActionOnKeyUp(' ', event, '__FRAGMENT__', `action('${this.value}')`)" />
                """;

        String expectedKeyCode = """
                <input type="text" value="hello world" onkeyup="_M.doActionOnKeyUp(32, event, '__FRAGMENT__', `action('${this.value}')`)" />
                """;


        // when
        session.setLastParameters(something);
        String templateKey = FluxUtils.dataBufferFluxToString(renderer.render(onSpaceKey, session));
        String templateKeyCode = FluxUtils.dataBufferFluxToString(renderer.render(onSpaceKeyCode, session));

        // then
        logger.info(templateKey);
        Assertions.assertTrue(templateKey.contains(expectedKey));

        // and
        logger.info(templateKeyCode);
        Assertions.assertTrue(templateKeyCode.contains(expectedKeyCode));
    }
}
