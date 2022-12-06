package io.getmedusa.medusa.core.render;


import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Set;

public class WrapThIfTagsTest {

    private static final Logger logger = LoggerFactory.getLogger(WrapThIfTagsTest.class);
    public static final String LOG_RENDER_RESULT = "\nGiven: \n{}\nWith {} result will render in:\n\n{}\n";

    final Renderer renderer = new Renderer(Set.of(), null, "self");

    @Test
    void renderThIfBlock() {
        // given
        String hello = """
            <section>
                <th:block th:if="${eval}">
                    <span>Hello World</span>
                </th:block>
            </section>
            """;
        // when eval is false
        String resultFalse = renderSection(hello, new Attribute("eval",false));
        logger.info(LOG_RENDER_RESULT,hello,"'eval=false'",resultFalse);
        // then
        String expectedFalseResult = """
                <div class="med-wrap">
                </div>""";
        Assertions.assertEquals(resultFalse,expectedFalseResult);

        // and when eval is true
        String resultTrue = renderSection(hello, new Attribute("eval",true));
        logger.info(LOG_RENDER_RESULT,hello,"'eval=true'",resultTrue);
        // then
        String expectedTrueResult= """
                <div class="med-wrap"><span>Hello World</span>
                </div>""";
        Assertions.assertEquals(resultTrue, expectedTrueResult);
    }

    @Test
    @DisplayName("nested th:if tags will get us in trouble")
    void renderNestedIfs(){
        // given
        String nested = """
            <section>
                <th:block th:if="${eval}">
                    <span th:if="${eval}">Hello World</span>
                </th:block>
            </section>
            """;
        // when eval is false
        String resultFalse = renderSection(nested, new Attribute("eval",false));
        logger.info(LOG_RENDER_RESULT,nested,"'eval=false'",resultFalse);
        // then
        String expectedFalseResult = """
                   <div class="med-wrap">
                   </div>""";
        Assertions.assertEquals(resultFalse, expectedFalseResult); // <-- TODO trouble when nested tag: no 2 wrapping divs!

        // and when eval is true
        String resultTrue = renderSection(nested, new Attribute("eval",true));
        logger.info(LOG_RENDER_RESULT,nested,"'eval=true'",resultTrue);
        String expectedTrueResult= """
                    <div class="med-wrap">
                     <div class="med-wrap"><span>Hello World</span>
                     </div>
                    </div>""";
        Assertions.assertEquals(resultTrue,expectedTrueResult);
    }

    @Test
    @Disabled("trouble with auto close p-tag when it contains a div")
    void renderNestedPTag(){
        String nestedPTag = """
            <section>  
              <p th:if="${top}">
                 top
                 <span th:if="${middle}">+ middle</span>
              </p>
            </section>
            """;
        // when
        String resultFalse = renderSection(nestedPTag,  new Attribute("top",false));
        logger.info(LOG_RENDER_RESULT,nestedPTag,"'top=false'",resultFalse);
        // then
        String expectedFalseResult = """
                   <div class="med-wrap">
                   </div>""";
        Assertions.assertEquals(expectedFalseResult,resultFalse); // TODO fails!
        // TODO actual result is:
        //    <div class="med-wrap">
        //     <div class="med-wrap">
        //     </div>
        //     <p></p>
        //    </div>
        // auto close p-tag when it contains a div

        // and when
        String resultTrue = renderSection(nestedPTag, new Attribute("top",true));
        logger.info(LOG_RENDER_RESULT,nestedPTag,"'top=true'",resultTrue);
        // then
        String expectedTrueResult= """
                    <div class="med-wrap">
                     <div class="med-wrap"><span>Hello World</span>
                     </div>
                    </div>""";
        Assertions.assertEquals(resultTrue,expectedTrueResult);
    }

    private String renderSection(String template, Attribute... attributes) {
        return renderTag(template,"section",attributes);
    }

    private String renderTag(String template, String tag, Attribute... attributes) {
        Document document = Jsoup.parse(template);
        Session session = new Session();
        session.setLastParameters(Arrays.stream(attributes).toList());
        Flux<DataBuffer> flux = renderer.render(document.html(), session);
        String fluxResult = FluxUtils.dataBufferFluxToString(flux);
        return Jsoup.parse(fluxResult).selectFirst(tag).html();
    }

}
