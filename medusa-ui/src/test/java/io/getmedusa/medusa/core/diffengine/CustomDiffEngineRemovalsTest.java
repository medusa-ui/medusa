package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

class CustomDiffEngineRemovalsTest extends DiffEngineJSoup {

    private static Stream<Arguments> removalParameters() {
        return Stream.of(Arguments.of(
            """
                    <section>
                        <div>1</div>
                        <div>2</div>
                        <div>3</div>
                    </section>
                    """,
                    """
                    <section>
                        <div>1</div>
                        <div>3</div>
                    </section>
                    """
                ),
                Arguments.of(
                    """
                    <section>
                        <div>1</div>
                        <div>2</div>
                        <div>3</div>
                    </section>
                    """,
                    """
                    <section></section>
                    """
                ));
    }

    @ParameterizedTest
    @MethodSource("removalParameters")
    void testRemoval(String oldHTML, String newHTML) {
        Set<JSReadyDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, new ArrayList<>(diffs));
    }

}
