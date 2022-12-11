package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

class CustomDiffEngineAttributeChangesTest extends DiffEngineJSoup {

    private static Stream<Arguments> attributeChangeParameters() {
        return Stream.of(Arguments.of(
            """
                    <section>
                        <h5 id="test">1</h5>
                    </section>
                    """,
                    """
                    <section>
                        <h5>1</h5>
                    </section>
                    """
                ),
                Arguments.of(
                    """
                    <section>
                        <h5 id="abc">1</h5>
                    </section>
                    """,
                    """
                    <section>
                        <h5 id="bdg123">1</h5>
                    </section>
                    """
                ),
                Arguments.of(
                        """
                        <section>
                            <h5 id="abc" class="red blue">1</h5>
                        </section>
                        """,
                        """
                        <section>
                            <h5 id="bdg123" class="orange">1</h5>
                        </section>
                        """
                ),
                Arguments.of(
                        """
                        <section>
                            <h5 id="abc" class="red blue" xyz="123">1</h5>
                        </section>
                        """,
                        """
                        <section>
                            <h5 id="bdg123" class="red blue" zzz="3333">1</h5>
                        </section>
                        """
                ),
                Arguments.of(
                    """
                    <section>
                        <h5>1</h5>
                    </section>
                    """,
                    """
                    <section>
                        <h5 id="test">1</h5>
                    </section>
                    """
                ));
    }

    @ParameterizedTest
    @MethodSource("attributeChangeParameters")
    void testAttributeChange(String oldHTML, String newHTML) {
        Set<JSReadyDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

}
