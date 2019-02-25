package com.akelius.svaponi.eezy.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Provides a continuation pattern to build multilevel Map objects.
 * NOTE: it is not possible to use generics since we need to navigate the map up and down, thus a value can assume different types.
 */

public class MapBuilder {

    private final Supplier<Map<String, Object>> supplier = () -> new HashMap<>();
    private final Map<String, Object> root;

    private Map<String, Object> current;

    public MapBuilder() {
        this.root = supplier.get();
        current = this.root;
    }

    public Map<String, Object> build() {
        return root;
    }

    public MapBuilder put(final String key, final Object value) {
        current.put(key, value);
        return this;
    }

    public MapBuilder add(final String key, final Object... values) {
        current.putIfAbsent(key, new ArrayList<>()); // to be sure oldValue is never null
        current.computeIfPresent(key, (k, oldValue) -> {
            if (Collection.class.isAssignableFrom(oldValue.getClass())) {
                final Collection collection = (Collection) oldValue;
                Arrays.stream(values).forEach(collection::add);
                return oldValue;
            } else {
                // if elem is not a collection, creates a new collection and add elem to it
                final Collection collection = new ArrayList<>();
                collection.add(oldValue);
                Arrays.stream(values).forEach(collection::add);
                return collection;
            }
        });
        return this;
    }

    public MapBuilder putIfAbsent(final String key, final Object value) {
        current.putIfAbsent(key, value);
        return this;
    }

    public MapBuilder putAll(final Map<String, Object> map) {
        current.putAll(map);
        return this;
    }

    public MapBuilder consume(final Consumer<Map<String, ?>> consumer) {
        consumer.accept(current);
        return this;
    }

    /**
     * builds a inner map with key and goes down a level. Keep building the map at that level, then use {@link #goUp()} to go back to the outer map.
     *
     * @param key
     * @return
     */
    public MapBuilder goDown(final String key) {
        final Map<String, Object> innerMap = supplier.get();
        current.put(key, innerMap);
        current = innerMap;
        return this;
    }

    /**
     * goes up a level to the outer map. To be used after a {@link #goDown(String)}
     *
     * @throws IllegalStateException when there is no more outer levels
     */
    public MapBuilder goUp() throws IllegalStateException {
        current = findOuterMap();
        return this;
    }

    // private

    private Map<String, Object> findOuterMap() {
        final Map<String, Object> result = recursiveFindOuterMap(root);
        if (result == null) {
            throw new IllegalStateException("messed up hierarchy"); // current is not present among the children
        }
        return result;
    }

    private Map<String, Object> recursiveFindOuterMap(final Map<String, Object> cursor) {
        if (cursor.containsValue(current)) {
            return cursor;
        }
        final List<Map<String, Object>> innerMaps = cursor.values().stream()
                .filter(Objects::nonNull)
                .filter(value -> this.root.getClass().isAssignableFrom(value.getClass()))
                .map(value -> (Map<String, Object>) value)
                .collect(Collectors.toList());
        for (final Map<String, Object> child : innerMaps) {
            final Map<String, Object> result = recursiveFindOuterMap(child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}