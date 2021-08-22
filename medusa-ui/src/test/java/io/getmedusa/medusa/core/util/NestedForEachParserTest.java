package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class NestedForEachParserTest {

    private String HTML = "[$foreach $condition-1]\n" +
            "\t[$foreach $condition-1a]\n" +
            "\t\t[$foreach $condition-1aa]\n" +
            "\t\t\t1aa\n" +
            "\t\t[$end for]\n" +
            "\t[$end for]\n" +
            "\t[$foreach $condition-1b]\n" +
            "\t\t[$foreach $condition-1b1]\n" +
            "\t\t\t1b1\n" +
            "\t\t[$end for]\n" +
            "\t\t[$foreach $condition-1b2]\n" +
            "\t\t\t1b2\n" +
            "\t\t[$end for]\n" +
            "\t[$end for]\n" +
            "[$end for]\n" +
            "\n" +
            "[$foreach $condition-2]\n" +
            "  [$foreach $condition-2a]\n" +
            "  \t2a\n" +
            "  [$end for]\n" +
            "  [$foreach $condition-2b]\n" +
            "  \t2b\n" +
            "  [$end for]\n" +
            "[$end for]\n" +
            "\n" +
            "[$foreach $condition-3]\n" +
            "  [$foreach $condition-3a]\n" +
            "\t [$foreach $condition-3aa]\n" +
            "\t\t3aa\n" +
            "\t [$end for]\n" +
            "\t \t [$foreach $condition-3ab]\n" +
            "\t\t\t3ab\n" +
            "\t [$end for]\n" +
            "  [$end for]\n" +
            "[$end for]";

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
        List<String> smallestMatchFirst = PARSER.findSmallestMatch(TEST);
        Assertions.assertEquals("[$foreach $condition-3aa]3aa[$end for]", smallestMatchFirst.get(0));
        Assertions.assertEquals("[$foreach $condition-3a][$foreach $condition-3aa]3aa[$end for][$end for]", smallestMatchFirst.get(1));
        Assertions.assertEquals("[$foreach $condition-3][$foreach $condition-3a][$foreach $condition-3aa]3aa[$end for][$end for][$end for]", smallestMatchFirst.get(2));
    }

    @Test
    void findSmallestMatchFirstOnComplexHTML() {
        List<String> smallestMatchFirst = PARSER.findSmallestMatch(HTML);

        boolean hasCompleted1aa = false;
        boolean hasCompleted2a = false;
        boolean hasCompleted2 = false;
        boolean hasCompleted3ab = false;
        boolean hasCompleted3a = false;
        boolean hasCompleted3 = false;

        Assertions.assertEquals(13, smallestMatchFirst.size());

        for(String match : smallestMatchFirst) {
            String trimmedMatch = trim(match);
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

}
