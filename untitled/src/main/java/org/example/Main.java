package org.example;

import java.sql.Array;
import java.util.*;
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
        LimitedSizeCache<String, Integer> lastFiveRequests = new LimitedSizeCache<>(5);

        for (String request : requests) {
            int isBlocked = 0;
            for (Pattern pattern : patterns) {
                if (pattern.matcher(request).find()) {
                    isBlocked = 1;
                }
            }

            // check for at least 2 requests among 5 latest
            int blocked = lastFiveRequests.getGroupedValue(request, 1); // 1 - blocked
            int nonBlocked = lastFiveRequests.getGroupedValue(request, 0); // 0 - nonBlocked

            if (nonBlocked >= 2 && blocked == 0) {
                isBlocked = 1;
            } else if (blocked >= 2) {
                isBlocked = 0;
            }

            lastFiveRequests.put(request, isBlocked);
            result.add(isBlocked);
        }

        return result;
    }

    public static String expandRegex(String rawRegex) {
        return "^" + rawRegex.replace(".", "\\.").replace("*", "(?:\\d{1,3}\\.?)*") + "$";
    }

    public static record Pair<T, V>(T key, V value) {}

    public static class LimitedSizeCache<T, V> {
        private final int size;
        private final Deque<Pair<T, V>> cache = new ArrayDeque<>();
        private final Map<Pair<T, V>, Integer> lookup = new HashMap<>();

        public LimitedSizeCache(int size) {
            this.size = size;
        }

        public void put(T key, V value) {

            if (cache.size() >= this.size) {
                Pair<T, V> oldestEntry = cache.removeFirst();
                int oldestEntryCount = lookup.get(oldestEntry);
                if (oldestEntryCount == 1) {
                    lookup.remove(oldestEntry);
                } else {
                    lookup.put(oldestEntry, oldestEntryCount - 1);
                }
            }

            var p = new Pair<T, V>(key, value);
            cache.addLast(p);
            lookup.put(p, lookup.getOrDefault(p, 0) + 1);
        }

        public int getGroupedValue(T key, V value) {
            var p = new Pair<T, V>(key, value);
            return lookup.getOrDefault(p, 0);
        }
    }
}