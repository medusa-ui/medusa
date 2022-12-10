package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

class CustomDiffEngineTextChangesTest extends DiffEngineJSoup {

    private static Stream<Arguments> textChangeParameters() {
        return Stream.of(Arguments.of(
            """
                    <section>
                        <h5>1</h5>
                    </section>
                    """,
                    """
                    <section>
                        <h5>123</h5>
                    </section>
                    """
                ),
                Arguments.of(
                    """
                    <section>
                        <h5 />
                    </section>
                    """,
                    """
                    <section>
                        <h5>test-123</h5>
                    </section>
                    """
                ));
    }

    @ParameterizedTest
    @MethodSource("textChangeParameters")
    void testTextChange(String oldHTML, String newHTML) {
        Set<JSReadyDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, new ArrayList<>(diffs));
    }

}
