package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

class CustomDiffEngineAdditionsTest extends DiffEngineJSoup {

    private static Stream<Arguments> additionParameters() {
        return Stream.of(Arguments.of(
            """
                    <section>
                        <div>1</div>
                    </section>
                    """,
                    """
                    <section>
                        <div>1</div>
                        <div>2</div>
                        <div>3</div>
                    </section>
                    """
                ),
                Arguments.of(
                    """
                    <section>
                    </section>
                    """,
                    """
                    <section>
                        <div>1</div>
                        <div>2</div>
                        <div>3</div>
                    </section>
                    """
                ),
                Arguments.of(
                        """
                        <section><p>Hello world</p></section>
                        """,
                        """
                        <section><p>Hello world</p><p>Hello world</p></section>
                        """
                ),
                Arguments.of(
                        """
                        <section>
                            <div>2</div>
                        </section>
                        """,
                        """
                        <section>
                            <div>1</div>
                            <div>2</div>
                            <div>3</div>
                        </section>
                        """
                ),
                Arguments.of(
                """
                        <section>
                            <h5>1</h5>
                            <p>3</p>
                            <div>4 change</div>
                        </section>
                        """,
                        """
                        <section>
                            <h5>1</h5>
                            <div><p>2</p></div>
                            <p>3</p>
                            <div>4 change</div>
                        </section>
                        """
                ));
    }

    @ParameterizedTest
    @MethodSource("additionParameters")
    void testAdditions(String oldHTML, String newHTML) {
        Set<JSReadyDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, new ArrayList<>(diffs));
    }

}
