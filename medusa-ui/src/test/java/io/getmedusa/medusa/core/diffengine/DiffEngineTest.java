package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class DiffEngineTest {

    private final DiffEngine diffEngine = new DiffEngine();

    @Test
    void testDiffGeneration_Addition_Simple() {
        final String oldHTML = "<table><tr><td>1</td><td>2</td></tr></table>";
        final String newHTML = "<table><tr><td>1</td><td>2</td></tr><tr><td>3</td><td>4</td></tr></table>";

        StepVerifier
            .create(diffEngine.findDiffs(oldHTML, newHTML))
            .assertNext(listOf -> {
                final JSReadyDiff jsReadyDiff = listOf.get(0);
                Assertions.assertNotNull(jsReadyDiff);
                Assertions.assertTrue(jsReadyDiff.toString().contains("type=ADDITION"));
                Assertions.assertEquals("/table[1]/tr[1]", jsReadyDiff.getXpath()); //expect an existing xpath of the prev element
                Assertions.assertEquals("<tr><td>3</td><td>4</td></tr>", jsReadyDiff.getContent());
            })
            .expectComplete()
            .verify();
    }

    @Test
    void testDiffGeneration_Addition_Simple_FirstEntry() {
        final String oldHTML = "<table></table>";
        final String newHTML = "<table><tr><td>1</td><td>2</td></tr></table>";

        StepVerifier
                .create(diffEngine.findDiffs(oldHTML, newHTML))
                .assertNext(listOf -> {
                    final JSReadyDiff jsReadyDiff = listOf.get(0);
                    Assertions.assertNotNull(jsReadyDiff);
                    Assertions.assertTrue(jsReadyDiff.toString().contains("type=ADDITION"));
                    Assertions.assertEquals("/table[1]/::first", jsReadyDiff.getXpath()); //or explicitly calling out that it'd be the first entry
                    Assertions.assertEquals("<tr><td>1</td><td>2</td></tr>", jsReadyDiff.getContent());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testDiffGeneration_Removal_Simple() {
        final String oldHTML = "<table><tr><td>1</td><td>2</td></tr><tr><td>3</td><td>4</td></tr></table>";
        final String newHTML = "<table><tr><td>1</td><td>2</td></tr></table>";

        StepVerifier
            .create(diffEngine.findDiffs(oldHTML, newHTML))
            .assertNext(listOf -> {
                final JSReadyDiff jsReadyDiff = listOf.get(0);
                Assertions.assertNotNull(jsReadyDiff);
                Assertions.assertTrue(jsReadyDiff.toString().contains("type=REMOVAL"));
                Assertions.assertEquals("/table[1]/tr[2]", jsReadyDiff.getXpath());
                Assertions.assertNull(jsReadyDiff.getContent());
            })
            .expectComplete()
            .verify();
    }

    @Test
    void testDiffGeneration_Edit_Simple() {
        final String oldHTML = "<p>Hello world</p>";
        final String newHTML = "<p>Hello WORLD</p>";

        StepVerifier
                .create(diffEngine.findDiffs(oldHTML, newHTML))
                .assertNext(listOf -> {
                    final JSReadyDiff jsReadyDiff = listOf.get(0);
                    Assertions.assertNotNull(jsReadyDiff);
                    Assertions.assertTrue(jsReadyDiff.toString().contains("type=EDIT"));
                    Assertions.assertEquals("/p[1]", jsReadyDiff.getXpath());
                    Assertions.assertEquals("<p>Hello WORLD</p>", jsReadyDiff.getContent());
                })
                .expectComplete()
                .verify();

    }

    @Test
    void testDiffGeneration_Addition_MultipleEnd() {
        final String oldHTML = "<html><table><tr><td>1</td><td>2</td></tr></table></html>";
        final String newHTML = "<html><table><tr><td>1</td><td>2</td><td>3</td><td>4</td></tr></table><p>Hello world</p></html>";

        StepVerifier
                .create(diffEngine.findDiffs(oldHTML, newHTML))
                .assertNext(listOf -> {
                    final JSReadyDiff jsReadyDiff1 = listOf.get(0);
                    System.out.println(jsReadyDiff1);
                    Assertions.assertNotNull(jsReadyDiff1);
                    Assertions.assertTrue(jsReadyDiff1.toString().contains("type=ADDITION"));
                    Assertions.assertEquals("/html[1]/table[1]/tr[1]/td[3]", jsReadyDiff1.getXpath());
                    Assertions.assertEquals("<td>3</td>", jsReadyDiff1.getContent());

                    final JSReadyDiff jsReadyDiff2 = listOf.get(1);
                    System.out.println(jsReadyDiff2);
                    Assertions.assertNotNull(jsReadyDiff2);
                    Assertions.assertTrue(jsReadyDiff2.toString().contains("type=ADDITION"));
                    Assertions.assertEquals("/html[1]/table[1]/tr[1]/td[4]", jsReadyDiff2.getXpath());
                    Assertions.assertEquals("<td>4</td>", jsReadyDiff2.getContent());

                    final JSReadyDiff jsReadyDiff3 = listOf.get(2);
                    System.out.println(jsReadyDiff3);
                    Assertions.assertNotNull(jsReadyDiff3);
                    Assertions.assertTrue(jsReadyDiff3.toString().contains("type=ADDITION"));
                    Assertions.assertEquals("/html[1]/p[1]", jsReadyDiff3.getXpath());
                    Assertions.assertEquals("<p>Hello world</p>", jsReadyDiff3.getContent());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testDiffGeneration_Addition_MultipleStart() {
        final String oldHTML = "<html><table><tr><td>1</td><td>2</td></tr></table></html>";
        final String newHTML = "<html><p>Hello world</p><table><tr><td>3</td><td>4</td><td>1</td><td>2</td></tr></table></html>";

        StepVerifier
                .create(diffEngine.findDiffs(oldHTML, newHTML))
                .assertNext(listOf -> {
                    System.out.println(listOf);
                    //TODO
                })
                .expectComplete()
                .verify();
    }

}

