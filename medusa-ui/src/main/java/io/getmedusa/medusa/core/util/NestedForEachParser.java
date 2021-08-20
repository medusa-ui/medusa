package io.getmedusa.medusa.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Instead of using regex, resolve outer-blocks and inner-blocks
 * via the classic search in full text.
 */
public class NestedForEachParser {
    private final String FOR_EACH = "[$foreach";
    private final String END_FOR = "[$end for]";

    /**
     * main for demo
     */
    public static void main(String[] args) {
        NestedForEachParser parser =  new NestedForEachParser();
        List<String> loops = Arrays.asList(parser.single, parser.nested, parser.levels, parser.full);
        for(String html: loops) {
            List<ForEach> list = parser.parseString(html);
            list.stream().forEach(fe -> System.out.println(fe + "\n-- ~ --"));
            System.out.println("\n====\n");
        }
    }

    /**
     * Holder outer block(outer-loop) with children(inner-loops)
     */
    class ForEach {
        String block = "";
        List<ForEach> children = new ArrayList<>();

        @Override
        public String toString() {
            return "ForEach{" +
                    "block='" + block + '\'' +
                    ", children=" + children +
                    '}';
        }
    }

    /**
     * Provide a list off nested foreach loops
     * @param html to parse
     * @return list of loops
     */
    public List<ForEach> parseString(String html) {
        List<ForEach> result = new ArrayList<>();

        List<Integer> forEachIndexes = forEachIndexes(html);
        List<Integer> endForIndexes = endForIndexes(html);

        if(forEachIndexes.size() != endForIndexes.size()) {
            //error invalid loop or no end for foreach ref issue #17 => https://github.com/medusa-ui/medusa/issues/17
        }
        int index = 0;
        int number = forEachIndexes.size();
        List<Integer> indexesStart = new ArrayList<>();
        List<Integer> indexesEnd = new ArrayList<>();
        while(index < number) {
            int start = forEachIndexes.get(index);
            int end = endForIndexes.get(index);
            indexesStart.add(start);
            indexesEnd.add(end);
            index++;
            if(index == number || (index < number && end < forEachIndexes.get(index))) {
                // System.out.println("nested " + start + " ==> " + end);
                Collections.reverse(indexesEnd);
                result.add(forEach(indexesStart.get(0), indexesEnd.get(0), html));
                indexesStart = new ArrayList<>();
                indexesEnd = new ArrayList<>();
            }
        }
        return result;
    }

    /**
     * Create a ForEach with as block the outer-loop
     * and as children the inner-loops.
     *
     * Parse the inner-block of each child
     *
     * @param start index start outer-loop
     * @param end index end outer-loop
     * @param html to parse
     * @return resulting ForEach
     */
    private ForEach forEach(int start, int end, String html) {
        ForEach each = new ForEach();
        String block = html.substring(start, end);
        String innerBlock = block.substring(block.indexOf(']') + 1, block.length() - END_FOR.length());
        each.block = block;
        each.children.addAll(parseString(innerBlock));
        return each;
    }

    /**
     * Find all the indexes of end of a foreach-loops
     *
     * @param html to parse
     * @return list of indexes
     */
    private List<Integer> endForIndexes(String html) {
        List<Integer> indexes = new ArrayList<>();
        int endIndex = 0;

        String temp = html;
        if(null != temp ) {
            int index = temp.indexOf(END_FOR);
            while (index > 0) {
                temp = temp.substring(index + END_FOR.length());
                endIndex += (index + END_FOR.length());

                indexes.add(endIndex);
                index = temp.indexOf(END_FOR);
            }
        }
        return indexes;
    }

    /**
     * Find all the indexes of start of a foreach-loops
     *
     * @param html to parse
     * @return list of indexes
     */
    private List<Integer> forEachIndexes(String html) {
        List<Integer> indexes = new ArrayList<>();
        String temp = html;
        if(null != temp ) {
            int index = temp.indexOf(FOR_EACH);
            int forIndex = 0;
            while (index > 0) {
                temp = temp.substring(index + FOR_EACH.length());
                forIndex += index;

                indexes.add(forIndex);
                forIndex += FOR_EACH.length();
                index = temp.indexOf(FOR_EACH);
            }
        }
        return indexes;
    }

    /* Example html */
    String single = "blabla [$foreach $inner] bla [$end for] blabla";
    String nested = "0 [$foreach $outer] 1 [$foreach $inner] 2 [$end for] [$end for] 3 [$foreach $outer] 4 [$foreach $inner] 5 [$end for] [$end for] 6";
    String levels = "start [$foreach $lvl1] 1 [$foreach $lvl2] 2 [$end for] [$foreach $lvl2] 2 [$end for] [$end for] [$foreach $lvl1] 1 [$foreach $lvl2] 2 [$end for] [$foreach $lvl2] 2 [$foreach $lvl3] 3 [$foreach $lvl4] 4 [$end for] [$end for] [$end for] [$end for] end";

    String full = "<p>unknown [$each]</p>\n" +
            "\n" +
            "[$foreach $outer]\n" +
            "    <div class=\"outer-1\">[$each]</div>\n" +
            "    <div class=\"outer-2\">[$this.each]</div>\n" +
            "\n" +
            "    [$foreach $mid]\n" +
            "        <div class=\"mid-1\">[$this.each]</div>\n" +
            "    [$end for]\n" +
            "\n" +
            "[$end for]\n" +
            "\n" +
            "\n" +
            "    <p>unknown [$each]</p>\n" +
            "\n" +
            "[$foreach $outer]\n" +
            "    <div class=\"outer-1\">[$each]</div>\n" +
            "    <div class=\"outer-2\">[$this.each]</div>\n" +
            "\n" +
            "    [$foreach $mid]\n" +
            "        <div class=\"mid-1\">[$this.each]</div>\n" +
            "    [$end for]\n" +
            "\n" +
            "[$end for]\n" +
            "\n" +
            "<p>unknown [$each]</p>\n" +
            "\n" +
            "[$foreach $outer] [$end for]\n" +
            "\n" +
            "<p>unknown [$each]</p>\n"+
            "[$foreach $outer] \n" +
            "        blabla\n"+
            "[$end for]\n" +
            "\n" +
            "<p>unknown [$each]</p>\n"+
            "[$foreach $outer] \n" +
            "    [$foreach $mid]\n" +
            "        <div class=\"mid-1\">[$this.each]</div>\n" +
            "    [$end for]\n" +
            "    [$foreach $mid]\n" +
            "        <div class=\"mid-1\">[$this.each]</div>\n" +
            "    [$end for]\n" +
            "[$end for]\n" ;
}
