package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

class CustomDiffEngineBaseElementChangesTest extends DiffEngineJSoup {

    private static Stream<Arguments> baseElementChanges() {
        return Stream.of(Arguments.of(
            """
                    <section>
                        <h5>1</h5>
                    </section>
                    """,
                    """
                    <section>
                        <div>1</div>
                    </section>
                    """
                ));
    }

    String oldHTML =
            "<section>" +
                    "   <h5>1</h5>" +
                    "   <div>" +
                    "    <p>2</p>" +
                    "   </div>" +
                    "   <h5>5</h5>" +
                    "   <div>" +
                    "    <p>6</p>" +
                    "   </div>" +
                    "   <span>x</span>" +
                    "  </section>";

    final String newHTML =
            "<section>" +
                    "   <h5 id=\"test\">1</h5>" +
                    "   <div>" +
                    "    <p>A</p>" +
                    "   </div>" +
                    "   <p>3</p>" +
                    "   <h5>5</h5>" +
                    "   <div>" +
                    "    <p>6</p>" +
                    "   </div>" +
                    "   <p>7</p>" +
                    "  </section>";

    @ParameterizedTest
    @MethodSource("baseElementChanges")
    void testBaseElementChange(String oldHTML, String newHTML) {
        Set<JSReadyDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, new ArrayList<>(diffs));
    }

}
