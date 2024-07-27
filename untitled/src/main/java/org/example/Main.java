package org.example;

import java.sql.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    public static List<Integer> processRequests(List<String> ipRegexes, List<String> requests) {
        List<Pattern> patterns = ipRegexes.stream()
                .map(Main::expandRegex)
                .map(Pattern::compile)
                .toList();

        List<Integer> result = new ArrayList<>();
        Deque<Pair> lastFiveRequests = new ArrayDeque<>();
        for (String request : requests) {
            int isBlocked = 0;
            for (Pattern pattern : patterns) {
                if (pattern.matcher(request).find()) {
                    isBlocked = 1;
                }
            }

            // check for at least 2 requests among 5 latest
            int blocked = 0, nonBlocked = 0;
            for (Pair p : lastFiveRequests) {
                if (p.key().equals(request)) {
                    if (p.value() == 1) {
                        blocked++;
                    } else {
                        nonBlocked++;
                    }
                }
            }

            if (nonBlocked >= 2 && blocked == 0) {
                isBlocked = 1;
            } else if (blocked >= 2) {
                isBlocked = 0;
            }

            // keep latest 5 requests
            lastFiveRequests.addLast(new Pair(request, isBlocked));
            if (lastFiveRequests.size() > 5) {
                lastFiveRequests.removeFirst();
            }

            result.add(isBlocked);
        }

        return result;
    }

    public static String expandRegex(String rawRegex) {
        return "^" + rawRegex.replace(".", "\\.").replace("*", "(?:\\d{1,3}\\.?)*") + "$";
    }

    public static record Pair(String key, Integer value) {}
}