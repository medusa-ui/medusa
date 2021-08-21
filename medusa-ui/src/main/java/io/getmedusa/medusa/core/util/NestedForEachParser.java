package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;

import java.util.*;

/**
 * Instead of using regex, resolve outer-blocks and inner-blocks
 * via the classic search in full text.
 */
public class NestedForEachParser {

    private static final String FOR_EACH = "[$foreach";
    private static final String END_FOR = "[$end for]";

    /**
     * Provide a list off nested foreach loops
     * @param html to parse
     * @return list of loops
     */
    public Set<ForEachElement> buildDepthElements(String html) {
        Map<Pair, ForEachElement> result = new HashMap<>();

        List<Integer> forEachIndexes = forEachIndexes(html);
        List<Integer> endForIndexes = endForIndexes(html);

        if(forEachIndexes.size() != endForIndexes.size()) {
            //error invalid loop or no end for foreach ref issue #17 => https://github.com/medusa-ui/medusa/issues/17
            throw new IllegalStateException("Foreach is malformed");
        }

        Collections.reverse(endForIndexes);
        for (int i = 0; i < forEachIndexes.size(); i++) {
            final Integer beginIndex = forEachIndexes.get(i);
            final Integer endIndex = endForIndexes.get(i);

            final String forBlock = html.substring(beginIndex, endIndex);

            ForEachElement parent = null;
            if(i != 0) {
                final Integer possibleParentBeginIndex = forEachIndexes.get(i-1);
                final Integer possibleParentEndIndex = endForIndexes.get(i-1);

                boolean hasParent = (possibleParentBeginIndex < beginIndex) && (endIndex < possibleParentEndIndex);
                if(hasParent) {
                    parent = result.get(new Pair(possibleParentBeginIndex, possibleParentEndIndex));
                }
            }

            result.put(new Pair(beginIndex, endIndex), new ForEachElement(forBlock, parent));
        }

        return new TreeSet<>(result.values());
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
            while (index >= 0) {
                temp = temp.substring(index + FOR_EACH.length());
                forIndex += index;

                indexes.add(forIndex);
                forIndex += FOR_EACH.length();
                index = temp.indexOf(FOR_EACH);
            }
        }
        return indexes;
    }

    static class Pair {
        final int a;
        final int b;

        Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false;
            Pair pair = (Pair) o;
            return a == pair.a && b == pair.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}
