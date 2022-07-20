package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DiffEngineTest {

    private final DiffEngine diffEngine = new DiffEngine();

    @Test
    void testDiffGeneration_Addition_Simple() {
        final String oldHTML = "<table><tr><td>1</td><td>2</td></tr></table>";
        final String newHTML = "<table><tr><td>1</td><td>2</td></tr><tr><td>3</td><td>4</td></tr></table>";

        final List<JSReadyDiff> listOf = diffEngine.findDiffs(oldHTML, newHTML);

        Assertions.assertEquals(1, listOf.size());
        final JSReadyDiff jsReadyDiff = listOf.get(0);
        Assertions.assertNotNull(jsReadyDiff);
        Assertions.assertTrue(jsReadyDiff.toString().contains("type=ADDITION"));
        Assertions.assertEquals("/table[1]/tr[1]", jsReadyDiff.getXpath()); //expect an existing xpath of the prev element
        Assertions.assertEquals("<tr><td>3</td><td>4</td></tr>", jsReadyDiff.getContent());
    }

    @Test
    void testDiffGeneration_Addition_Simple_FirstEntry() {
        final String oldHTML = "<table></table>";
        final String newHTML = "<table><tr><td>1</td><td>2</td></tr></table>";

        final List<JSReadyDiff> listOf = diffEngine.findDiffs(oldHTML, newHTML);
        Assertions.assertEquals(1, listOf.size());
        final JSReadyDiff jsReadyDiff = listOf.get(0);
        Assertions.assertNotNull(jsReadyDiff);
        Assertions.assertTrue(jsReadyDiff.toString().contains("type=ADDITION"));
        Assertions.assertEquals("/table[1]/::first", jsReadyDiff.getXpath()); //or explicitly calling out that it'd be the first entry
        Assertions.assertEquals("<tr><td>1</td><td>2</td></tr>", jsReadyDiff.getContent());
    }

    @Test
    void testDiffGeneration_Removal_Simple() {
        final String oldHTML = "<table><tr><td>1</td><td>2</td></tr><tr><td>3</td><td>4</td></tr></table>";
        final String newHTML = "<table><tr><td>1</td><td>2</td></tr></table>";

        final List<JSReadyDiff> listOf = diffEngine.findDiffs(oldHTML, newHTML);
        Assertions.assertEquals(1, listOf.size());

        final JSReadyDiff jsReadyDiff = listOf.get(0);
        Assertions.assertNotNull(jsReadyDiff);
        Assertions.assertTrue(jsReadyDiff.toString().contains("type=REMOVAL"));
        Assertions.assertEquals("/table[1]/tr[2]", jsReadyDiff.getXpath());
        Assertions.assertNull(jsReadyDiff.getContent());
    }

    @Test
    void testDiffGeneration_Edit_Simple() {
        final String oldHTML = "<p>Hello world</p>";
        final String newHTML = "<p>Hello WORLD</p>";

        final List<JSReadyDiff> listOf = diffEngine.findDiffs(oldHTML, newHTML);
        Assertions.assertEquals(1, listOf.size());

        final JSReadyDiff jsReadyDiff = listOf.get(0);
        Assertions.assertNotNull(jsReadyDiff);
        Assertions.assertTrue(jsReadyDiff.toString().contains("type=EDIT"));
        Assertions.assertEquals("/p[1]", jsReadyDiff.getXpath());
        Assertions.assertEquals("<p>Hello WORLD</p>", jsReadyDiff.getContent());
    }

    @Test
    void testDiffGeneration_Edit_Attribute_Simple() {
        final String oldHTML = "<p>Hello world</p>";
        final String newHTML = "<p class=\"red\">Hello world</p>";

        final List<JSReadyDiff> jsReadyDiffs = diffEngine.findDiffs(oldHTML, newHTML);

        for(JSReadyDiff diff : jsReadyDiffs) {
            System.out.println(diff);
        }

        Assertions.assertEquals( 1, jsReadyDiffs.size(), "expected only 1 edit");

        final JSReadyDiff jsReadyDiff1 = jsReadyDiffs.get(0);
        Assertions.assertNotNull(jsReadyDiff1);
        Assertions.assertTrue(jsReadyDiff1.toString().contains("type=EDIT"));
        Assertions.assertEquals("/p[1]", jsReadyDiff1.getXpath());
        Assertions.assertEquals("<p class=\"red\">Hello world</p>", jsReadyDiff1.getContent());
    }

    @Test
    void testDiffGeneration_Addition_MultipleEnd() {
        final String oldHTML = "<html><table><tr><td>1</td><td>2</td></tr></table></html>";
        final String newHTML = "<html><table><tr><td>1</td><td>2</td><td>3</td><td>4</td></tr></table><p>Hello world</p></html>";

        final List<JSReadyDiff> listOf = diffEngine.findDiffs(oldHTML, newHTML);

        final JSReadyDiff jsReadyDiff1 = listOf.get(0);
        System.out.println(jsReadyDiff1);
        Assertions.assertNotNull(jsReadyDiff1);
        Assertions.assertTrue(jsReadyDiff1.toString().contains("type=ADDITION"));
        Assertions.assertEquals("/html[1]/table[1]/tr[1]/td[2]", jsReadyDiff1.getXpath());
        Assertions.assertEquals("<td>3</td>", jsReadyDiff1.getContent());

        final JSReadyDiff jsReadyDiff2 = listOf.get(1);
        System.out.println(jsReadyDiff2);
        Assertions.assertNotNull(jsReadyDiff2);
        Assertions.assertTrue(jsReadyDiff2.toString().contains("type=ADDITION"));
        Assertions.assertEquals("/html[1]/table[1]/tr[1]/td[3]", jsReadyDiff2.getXpath());
        Assertions.assertEquals("<td>4</td>", jsReadyDiff2.getContent());

        final JSReadyDiff jsReadyDiff3 = listOf.get(2);
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

        final List<JSReadyDiff> diffs = diffEngine.findDiffs(oldHTML, newHTML);
        System.out.println(diffs);

        //TODO
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

        final List<JSReadyDiff> jsReadyDiffs = diffEngine.findDiffs(oldHTMLOK, newHTMLOK);
        System.out.println(jsReadyDiffs);
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

        final List<JSReadyDiff> jsReadyDiffs2 = diffEngine.findDiffs(oldHTMLBUG, newHTMLBUG);

        for(JSReadyDiff diff : jsReadyDiffs2) {
            System.out.println(diff);
        }

        Assertions.assertEquals( 1, jsReadyDiffs2.size(), "expected only 1 addition");

    }

}

