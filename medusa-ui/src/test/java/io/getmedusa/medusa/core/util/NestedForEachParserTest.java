package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class NestedForEachParserTest {

    private static final String HTML = """
            [$foreach $condition-1]
            \t[$foreach $condition-1a]
            \t\t[$foreach $condition-1aa]
            \t\t\t1aa
            \t\t[$end for]
            \t[$end for]
            \t[$foreach $condition-1b]
            \t\t[$foreach $condition-1b1]
            \t\t\t1b1
            \t\t[$end for]
            \t\t[$foreach $condition-1b2]
            \t\t\t1b2
            \t\t[$end for]
            \t[$end for]
            [$end for]

            [$foreach $condition-2]
              [$foreach $condition-2a]
              \t2a
              [$end for]
              [$foreach $condition-2b]
              \t2b
              [$end for]
            [$end for]

            [$foreach $condition-3]
              [$foreach $condition-3a]
            \t [$foreach $condition-3aa]
            \t\t3aa
            \t [$end for]
            \t \t [$foreach $condition-3ab]
            \t\t\t3ab
            \t [$end for]
              [$end for]
            [$end for]""";

    private static final EachParser PARSER = new EachParser();

    @Test
    void findAllStarters() {
        Assertions.assertEquals(13, PARSER.findAllStarters(HTML).size());
    }

    @Test
    void findAllStoppers() {
        Assertions.assertEquals(13, PARSER.findAllStoppers(HTML).size());
    }

    @Test
    void findSmallestMatchFirst() {
        String TEST = "[$foreach $condition-3][$foreach $condition-3a][$foreach $condition-3aa]3aa[$end for][$end for][$end for]";
        List<ForEachElement> smallestMatchFirst = PARSER.buildDepthElements(TEST);
        Assertions.assertEquals("[$foreach $condition-3aa]3aa[$end for]", smallestMatchFirst.get(0).blockHTML);
        Assertions.assertEquals("[$foreach $condition-3a][$foreach $condition-3aa]3aa[$end for][$end for]", smallestMatchFirst.get(1).blockHTML);
        Assertions.assertEquals("[$foreach $condition-3][$foreach $condition-3a][$foreach $condition-3aa]3aa[$end for][$end for][$end for]", smallestMatchFirst.get(2).blockHTML);
    }

    @Test
    void findSmallestMatchFirstOnComplexHTML() {
        List<ForEachElement> smallestMatchFirst = PARSER.buildDepthElements(HTML);

        boolean hasCompleted1aa = false;
        boolean hasCompleted2a = false;
        boolean hasCompleted2 = false;
        boolean hasCompleted3ab = false;
        boolean hasCompleted3a = false;
        boolean hasCompleted3 = false;

        Assertions.assertEquals(13, smallestMatchFirst.size());

        for(ForEachElement match : smallestMatchFirst) {
            String trimmedMatch = trim(match.blockHTML);
            hasCompleted1aa = trimmedMatch.startsWith("[$foreach $condition-1aa]");
            hasCompleted2a = trimmedMatch.startsWith("[$foreach $condition-2a]");
            hasCompleted2 = trimmedMatch.startsWith("[$foreach $condition-2]");
            hasCompleted3ab = trimmedMatch.startsWith("[$foreach $condition-3ab]");
            hasCompleted3a = trimmedMatch.startsWith("[$foreach $condition-3a]");
            hasCompleted3 = trimmedMatch.startsWith("[$foreach $condition-3]");
            System.out.println(match);

            if(hasCompleted2a) Assertions.assertTrue(hasCompleted1aa);
            if(hasCompleted2) Assertions.assertTrue(hasCompleted2a);
            if(hasCompleted3ab) Assertions.assertTrue(hasCompleted2);
            if(hasCompleted3a) Assertions.assertTrue(hasCompleted3ab);
            if(hasCompleted3) Assertions.assertTrue(hasCompleted3a);
        }
    }

    private String trim(String text) {
        return text.replace("\n", "").replace("\r", "").replace("\t", "").replace(" ", "");
    }

    @Test
    void findParents() {
        String TEST = "[$foreach $condition-3][$foreach $condition-3a][$foreach $condition-3aa]3aa[$end for][$end for][$end for][$foreach $condition-4]4[$end for]";
        List<ForEachElement> smallestMatchFirst = PARSER.buildDepthElements(TEST);

        Assertions.assertEquals(4, smallestMatchFirst.size());
        boolean hasExpectedElement3a = false;
        boolean hasExpectedElement4 = false;
        for(ForEachElement element : smallestMatchFirst) {
            if(element.blockHTML.startsWith("[$foreach $condition-3a]")) {
                hasExpectedElement3a = true;
                Assertions.assertNotNull(element.getParent(), "Expected condition 3a to have a parent (condition-3)");
            }

            if(element.blockHTML.startsWith("[$foreach $condition-4]")) {
                hasExpectedElement4 = true;
                Assertions.assertNull(element.getParent());
            }
        }
        Assertions.assertTrue(hasExpectedElement3a);
        Assertions.assertTrue(hasExpectedElement4);
    }

    @Test
    void testClosestPairWithMatch() {
        Pair key = new Pair(3,4);
        Set<Pair> options = new HashSet<>();
        options.add(key);
        options.add(new Pair(5, 10));
        options.add(new Pair(0, 10));
        options.add(new Pair(2, 6));
        options.add(new Pair(20, 25));

        Assertions.assertEquals(new Pair(2,6), PARSER.findClosestPair(key, options));
    }

    @Test
    void testClosestPairWithNoMatch() {
        Pair key = new Pair(3,4);
        Set<Pair> options = new HashSet<>();
        options.add(key);
        options.add(new Pair(5, 10));
        options.add(new Pair(20, 25));

        Assertions.assertNull(PARSER.findClosestPair(key, options));
    }
}
