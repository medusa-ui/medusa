package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

class DiffEngineTest extends DiffEngineJSoup {

    @Test
    void testDiffGeneration_Addition_Simple() {
        final String oldHTML = "<table><tr><td>1</td><td>2</td></tr></table>";
        final String newHTML = "<table><tr><td>1</td><td>2</td></tr><tr><td>3</td><td>4</td></tr></table>";

        final Set<JSReadyDiff> listOf = getDiffs(oldHTML, newHTML);

        Assertions.assertEquals(1, listOf.size());
        final JSReadyDiff jsReadyDiff = listOf.stream().findFirst().get();
        Assertions.assertNotNull(jsReadyDiff);
        Assertions.assertTrue(jsReadyDiff.toString().contains("type=ADDITION"));
        Assertions.assertEquals("/table[1]/tr[1]", jsReadyDiff.getXpath()); //expect an existing xpath of the prev element
        Assertions.assertEquals("<tr><td>3</td><td>4</td></tr>", jsReadyDiff.getContent());
    }

    @Test
    void testDiffGeneration_Addition_Simple_FirstEntry() {
        final String oldHTML = "<table></table>";
        final String newHTML = "<table><tr><td>1</td><td>2</td></tr></table>";

        final Set<JSReadyDiff> listOf = getDiffs(oldHTML, newHTML);
        Assertions.assertEquals(1, listOf.size());
        final JSReadyDiff jsReadyDiff = listOf.stream().findFirst().get();
        Assertions.assertNotNull(jsReadyDiff);
        Assertions.assertTrue(jsReadyDiff.toString().contains("type=ADDITION"));
        Assertions.assertEquals("/table[1]/::first", jsReadyDiff.getXpath()); //or explicitly calling out that it'd be the first entry
        Assertions.assertEquals("<tr><td>1</td><td>2</td></tr>", jsReadyDiff.getContent());
    }

    @Test
    void testDiffGeneration_Removal_Simple() {
        final String oldHTML = "<table><tr><td>1</td><td>2</td></tr><tr><td>3</td><td>4</td></tr></table>";
        final String newHTML = "<table><tr><td>1</td><td>2</td></tr></table>";

        final Set<JSReadyDiff> listOf = getDiffs(oldHTML, newHTML);
        Assertions.assertEquals(1, listOf.size());

        final JSReadyDiff jsReadyDiff = listOf.stream().findFirst().get();
        Assertions.assertNotNull(jsReadyDiff);
        Assertions.assertTrue(jsReadyDiff.toString().contains("type=REMOVAL"));
        Assertions.assertEquals("/table[1]/tr[2]", jsReadyDiff.getXpath());
        Assertions.assertNull(jsReadyDiff.getContent());
    }

    @Test
    void testDiffGeneration_Edit_Simple() {
        final String oldHTML = "<p>Hello world</p>";
        final String newHTML = "<p>Hello WORLD</p>";

        final Set<JSReadyDiff> listOf = getDiffs(oldHTML, newHTML);
        Assertions.assertEquals(1, listOf.size());

        final JSReadyDiff jsReadyDiff = listOf.stream().findFirst().get();
        Assertions.assertNotNull(jsReadyDiff);
        Assertions.assertTrue(jsReadyDiff.toString().contains("type=EDIT"));
        Assertions.assertEquals("/p[1]", jsReadyDiff.getXpath());
        Assertions.assertEquals("<p>Hello WORLD</p>", jsReadyDiff.getContent());
    }

    @Test
    void testDiffGeneration_Edit_Attribute_Simple() {
        final String oldHTML = "<p>Hello world</p>";
        final String newHTML = "<p class=\"red\">Hello world</p>";

        final Set<JSReadyDiff> jsReadyDiffs = getDiffs(oldHTML, newHTML);

        for(JSReadyDiff diff : jsReadyDiffs) {
            System.out.println(diff);
        }

        Assertions.assertEquals( 1, jsReadyDiffs.size(), "expected only 1 edit");

        final JSReadyDiff jsReadyDiff1 = jsReadyDiffs.stream().findFirst().get();
        Assertions.assertNotNull(jsReadyDiff1);
        Assertions.assertTrue(jsReadyDiff1.toString().contains("type=EDIT"));
        Assertions.assertEquals("/p[1]", jsReadyDiff1.getXpath());
        Assertions.assertEquals("<p class=\"red\">Hello world</p>", jsReadyDiff1.getContent());
    }

    @Test
    void testDiffGeneration_Addition_MultipleEnd() {
        final String oldHTML = "<html><table><tr><td>1</td><td>2</td></tr></table></html>";
        final String newHTML = "<html><table><tr><td>1</td><td>2</td><td>3</td><td>4</td></tr></table><p>Hello world</p></html>";

        final Set<JSReadyDiff> listOf = getDiffs(oldHTML, newHTML);

        final JSReadyDiff jsReadyDiff1 = listOf.stream().findFirst().get();
        System.out.println(jsReadyDiff1);
        Assertions.assertNotNull(jsReadyDiff1);
        Assertions.assertTrue(jsReadyDiff1.toString().contains("type=ADDITION"));
        Assertions.assertEquals("/html[1]/table[1]/tr[1]/td[2]", jsReadyDiff1.getXpath());
        Assertions.assertEquals("<td>3</td>", jsReadyDiff1.getContent());

        final JSReadyDiff jsReadyDiff2 = listOf.stream().toList().get(1);
        System.out.println(jsReadyDiff2);
        Assertions.assertNotNull(jsReadyDiff2);
        Assertions.assertTrue(jsReadyDiff2.toString().contains("type=ADDITION"));
        Assertions.assertEquals("/html[1]/table[1]/tr[1]/td[3]", jsReadyDiff2.getXpath());
        Assertions.assertEquals("<td>4</td>", jsReadyDiff2.getContent());

        final JSReadyDiff jsReadyDiff3 = listOf.stream().toList().get(2);
        System.out.println(jsReadyDiff3);
        Assertions.assertNotNull(jsReadyDiff3);
        Assertions.assertTrue(jsReadyDiff3.toString().contains("type=ADDITION"));
        Assertions.assertEquals("/html[1]/::first", jsReadyDiff3.getXpath());
        Assertions.assertEquals("<p>Hello world</p>", jsReadyDiff3.getContent());

    }

    @Test
    void testDiffGeneration_Addition_MultipleStart() {
        final String oldHTML = "<html><table><tr><td>1</td><td>2</td></tr></table></html>";
        final String newHTML = "<html><p>Hello world</p><table><tr><td>3</td><td>4</td><td>1</td><td>2</td></tr></table></html>";

        getDiffs(oldHTML, newHTML);
    }

    @Test
    void testDiffGeneration_Addition_Bug() {
        //This is OK
        final String oldHTMLOK = """
                <div>
                    <div>
                        <p>Hello world</p>
                    </div>
                    <div>
                        <button onclick="_M.doAction(null, 'sayHi()')">Say Hi</button>
                    </div>
                </div>
                """;
        final String newHTMLOK = """
                <div>
                    <div>
                        <p>Hello world</p>
                        <p>Hi there</p>
                    </div>
                    <div>
                        <button onclick="_M.doAction(null, 'sayHi()')">Say Hi</button>
                    </div>
                </div>
                """;

        final Set<JSReadyDiff> jsReadyDiffs = getDiffs(oldHTMLOK, newHTMLOK);
        Assertions.assertEquals( 1, jsReadyDiffs.size(), "expected only 1 addition");

        // Here there is bug
        final String oldHTMLBUG = """
                <div>
                    <div>
                        <p>Hello world</p>
                        <button onclick="_M.doAction(null, 'sayHi()')">Say Hi</button>
                    </div>
                </div>
                """;
        final String newHTMLBUG = """
                <div>
                    <div>
                        <p>Hello world</p>
                        <p>Hi there</p>
                        <button onclick="_M.doAction(null, 'sayHi()')">Say Hi</button>
                    </div>
                </div>
                """;

        final Set<JSReadyDiff> jsReadyDiffs2 = getDiffs(oldHTMLBUG, newHTMLBUG);

        final Set<JSReadyDiff> jsReadyDiffsFinal = new LinkedHashSet<>();
        for(JSReadyDiff diff : jsReadyDiffs2) {
            System.out.println(diff);
            if(!diff.isSequenceChange()) {
                jsReadyDiffsFinal.add(diff);
            }
        }

        Assertions.assertEquals( 1, jsReadyDiffsFinal.size(), "expected only 1 addition");
    }

    private Set<JSReadyDiff> getDiffs(String oldHTML, String newHTML) {
        final Set<JSReadyDiff> jsReadyDiffs = engine.calculate(oldHTML, newHTML);
        System.out.println(jsReadyDiffs);
        return jsReadyDiffs;
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

        applyAndTest(oldHTML, newHTML, getDiffs(oldHTML, newHTML));
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

        applyAndTest(oldHTML, newHTML, getDiffs(oldHTML, newHTML));
    }

    @Test
    void testComplex2() {
        String oldHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <p>3</p>" +
                "   <div>4 change</div>" +
                "   <h5>5</h5>" +
                "   <p>7</p>" +
                "  </section>";

        final String newHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <div><p>2</p></div>" +
                "   <p>3</p>" +
                "   <div>4 change</div>" +
                "   <h5>5</h5>" +
                "   <div><p>6</p></div>" +
                "   <p>7</p>" +
                "  </section>";

        applyAndTest(oldHTML, newHTML, getDiffs(oldHTML, newHTML));
    }

    @Test
    void testComplex3() {
        String oldHTML = "<section>\n" +
                "   <h5>1</h5>\n" +
                "   <div>\n" +
                "    <p>2</p>\n" +
                "   </div>\n" +
                "   \n" +
                "   <h5>5</h5>\n" +
                "   <div>\n" +
                "    <p>6</p>\n" +
                "   </div>\n" +
                "   \n" +
                "  </section>";

        final String newHTML = "<section>\n" +
                "   <h5>1</h5>\n" +
                "   <div>\n" +
                "    <p>2</p>\n" +
                "   </div>\n" +
                "   <p>3</p>\n" +
                "   <h5>5</h5>\n" +
                "   <div>\n" +
                "    <p>6</p>\n" +
                "   </div>\n" +
                "   <p>7</p>\n" +
                "  </section>";

        applyAndTest(oldHTML, newHTML, getDiffs(oldHTML, newHTML));
    }

    @Test
    void testSimpleAdd() {
        String oldHTML = "<section></section>";
        final String newHTML = "<section><p>Hello world</p></section>";

        applyAndTest(oldHTML, newHTML, getDiffs(oldHTML, newHTML));
    }

    @Test
    void testSimpleAdd2() {
        String oldHTML = "<section><p>Hello world</p></section>";
        final String newHTML = "<section><p>Hello world</p><p>Hello world</p></section>";

        applyAndTest(oldHTML, newHTML, getDiffs(oldHTML, newHTML));
    }

    @Test
    void testToElement() {
        String html = "<p>Hello world</p>";
        Assertions.assertEquals(html, toElement(html).outerHtml());
    }

    @Test
    void testSimpleRemoval() {
        String oldHTML = "<section><p>Hello world</p></section>";
        final String newHTML = "<section></section>";

        applyAndTest(oldHTML, newHTML, getDiffs(oldHTML, newHTML));
    }

    @Test
    void testSimpleSequenceChange() {
        String oldHTML = """
                <section>
                    <p>3</p>
                    <span>1</span>
                    <button>2</button>
                </section>
                """;
        final String newHTML = """
                <section>
                    <span>1</span>
                    <button>2</button>
                    <p>3</p>
                </section>
                """;

        applyAndTest(oldHTML, newHTML, getDiffs(oldHTML, newHTML));
    }

    @Test
    void testSimpleSequenceChange2() {
        String oldHTML = """
                <section>
                    <div><button>3</button></div>
                    <p><span>1</span></p>
                    <button>2</button>
                </section>
                """;
        final String newHTML = """
                <section>
                    <p><span>1</span></p>
                    <button>2</button>
                    <div><button>3</button></div>
                </section>
                """;

        applyAndTest(oldHTML, newHTML, getDiffs(oldHTML, newHTML));
    }

}

