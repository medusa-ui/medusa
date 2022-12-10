package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Set;

class CustomDiffEngineComplexTest extends DiffEngineJSoup {

    final CustomDiffEngine diffEngine = new CustomDiffEngine();
    
    @Test
    void testComplex() {
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

        Set<JSReadyDiff> diffs = diffEngine.calculate(oldHTML, newHTML);
        //expect to see:
        //id added to first h5
        //text node change in section/div/p 1
        //new node between div 1 and h5 2
        //new node at end of section
        //removal of span 1
        System.out.println(diffs);

        applyAndTest(oldHTML, newHTML, new LinkedList<>(diffs));
    }

    @Test
    void testComplex1() {
        String oldHTML = """
                <section>
                    <p>A</p>
                    <p>B</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <p>A</p>
                    <div>
                        <p>2</p>
                    </div>
                    <p>B</p>
                </section>""";

        Set<JSReadyDiff> diffs = diffEngine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, new LinkedList<>(diffs));
    }

    @Test
    void testComplex1B() {
        String oldHTML = """
                <section>
                    <p>A</p>
                    <p>B</p>
                    <p>C</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <p>A</p>
                    <div>
                        <p>1</p>
                    </div>
                    <p>B</p>
                    <div>
                        <p>2</p>
                    </div>
                    <p>C</p>
                </section>""";

        Set<JSReadyDiff> diffs = diffEngine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, new LinkedList<>(diffs));
    }

    @Test
    void testComplex2() {
        String oldHTML = """
                <section>
                    <h5>1</h5>
                    <p>3</p>
                    <div>4 change</div>
                    <h5>5</h5>
                    <p>7</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <h5>1</h5>
                    <div><p>2</p></div>
                    <p>3</p>
                    <div>4 change</div>
                    <h5>5</h5>
                    <div><p>6</p></div>
                    <p>7</p>
                </section>
                """;

        Set<JSReadyDiff> diffs = diffEngine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, new LinkedList<>(diffs));
    }

}
