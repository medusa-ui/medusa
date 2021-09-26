package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EachParser {

    private final Pattern forEachPattern = Pattern.compile("\\[\\$foreach .+?]", Pattern.CASE_INSENSITIVE);
    private final Pattern endForPattern = Pattern.compile("\\[\\$end for]", Pattern.CASE_INSENSITIVE);

    public List<ForEachElement> buildDepthElements(String text) {
        List<Pair> starters = findAllStarters(text);
        List<Pair> stoppers = findAllStoppers(text);

        Collections.reverse(starters);

        List<Pair> alreadyUsedStarters = new ArrayList<>();
        List<Pair> alreadyUsedStoppers = new ArrayList<>();

        Map<Pair, ForEachElement> elements = new HashMap<>();

        //builds up a list of foreach blocks
        for(Pair stopper : stoppers) {
            for(Pair starter : starters) {
                if(hasNotBeenUsedBefore(starter, stopper, alreadyUsedStarters, alreadyUsedStoppers) && stopperMustAppearAfterStarter(starter, stopper)) {
                    alreadyUsedStarters.add(starter);
                    alreadyUsedStoppers.add(stopper);

                    String block = text.substring(starter.a, stopper.b);
                    String innerBlock = text.substring(starter.b, stopper.a);

                    elements.put(new Pair(starter.b, stopper.a), new ForEachElement(block, innerBlock));
                }
            }
        }

        //find parents by going over all pairs and finding the closest wrapping pair
        for(Map.Entry<Pair, ForEachElement> element : elements.entrySet()) {
            Pair parentPair = findClosestPair(element.getKey(), elements.keySet());
            if(parentPair != null) element.getValue().setParent(elements.get(parentPair));
        }

        return new ArrayList<>(elements.values());
    }

    Pair findClosestPair(Pair key, Set<Pair> allOptionsIncludingKey) {
        int smallestDistance = Integer.MAX_VALUE;
        Pair closest = null;

        for(Pair potentialWrapper : allOptionsIncludingKey) {
            if(!potentialWrapper.equals(key) && pairAWrapsPairB(potentialWrapper, key)) {
                int distance = (key.a - potentialWrapper.a) + (potentialWrapper.b - key.b);
                if(distance < smallestDistance) {
                    smallestDistance = distance;
                    closest = potentialWrapper;
                }
            }
        }

        return closest;
    }

    private boolean pairAWrapsPairB(Pair potentialWrapper, Pair potentialWrapped) {
        return potentialWrapper.a < potentialWrapped.a && potentialWrapper.b > potentialWrapped.b;
    }


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


    private boolean hasNotBeenUsedBefore(Pair starter, Pair stopper, List<Pair> alreadyUsedStarters, List<Pair> alreadyUsedStoppers) {
        return !(alreadyUsedStarters.contains(starter) || alreadyUsedStoppers.contains(stopper));
    }

    private boolean stopperMustAppearAfterStarter(Pair starter, Pair stopper) {
        return stopper.a > starter.b;
    }
}
