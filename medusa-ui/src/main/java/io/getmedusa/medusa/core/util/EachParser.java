package io.getmedusa.medusa.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EachParser {

    private final Pattern forEachPattern = Pattern.compile("\\[\\$foreach .+?]", Pattern.CASE_INSENSITIVE);
    private final Pattern endForPattern = Pattern.compile("\\[\\$end for]", Pattern.CASE_INSENSITIVE);

    List<Pair> findAllStarters(String text) {
        return match(text, forEachPattern);
    }

    List<Pair> findAllStoppers(String text) {
        return match(text, endForPattern);
    }

    private List<Pair> match(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        final List<Pair> list = new ArrayList<>();
        while(matcher.find()) {
            list.add(new Pair(matcher.start(), matcher.end()));
        }
        return list;
    }

    List<String> findSmallestMatch(String text) {
        List<String> result = new ArrayList<>();
        List<Pair> starters = findAllStarters(text);
        List<Pair> stoppers = findAllStoppers(text);

        Collections.reverse(starters);

        List<Pair> alreadyUsedStarters = new ArrayList<>();
        List<Pair> alreadyUsedStoppers = new ArrayList<>();

        for(Pair stopper : stoppers) {
            for(Pair starter : starters) {
                if(hasNotBeenUsedBefore(starter, stopper, alreadyUsedStarters, alreadyUsedStoppers) && stopperMustAppearAfterStarter(starter, stopper)) {
                    result.add(text.substring(starter.a, stopper.b));
                    alreadyUsedStarters.add(starter);
                    alreadyUsedStoppers.add(stopper);

                    
                }
            }
        }

        return result;
    }

    private boolean hasNotBeenUsedBefore(Pair starter, Pair stopper, List<Pair> alreadyUsedStarters, List<Pair> alreadyUsedStoppers) {
        return !(alreadyUsedStarters.contains(starter) || alreadyUsedStoppers.contains(stopper));
    }

    private boolean stopperMustAppearAfterStarter(Pair starter, Pair stopper) {
        return stopper.a > starter.b;
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
            if (!(o instanceof NestedForEachParser.Pair)) return false;
            NestedForEachParser.Pair pair = (NestedForEachParser.Pair) o;
            return a == pair.a && b == pair.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }

        @Override
        public String toString() {
            return "{" + a + ", " + b + '}';
        }
    }
}
