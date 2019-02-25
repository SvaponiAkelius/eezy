package com.akelius.svaponi.eezy.util;

import com.akelius.svaponi.eezy.web.WebException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Provides a continuation pattern to build multilevel Map objects.
 * NOTE: it is not possible to use generics since we need to navigate the map up and down, thus a value can assume different types.
 */

public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> parseJsonToMap(final byte[] body) {
        try {
            return mapper.readValue(body, Map.class);
        } catch (final IOException e) {
            throw new WebException(e);
        }
    }

    public static <T> T parseJson(final byte[] body, final Class<T> type) {
        try {
            return mapper.readValue(body, type);
        } catch (final IOException e) {
            throw new WebException(e);
        }
    }

    public static byte[] toJson(final Object json) {
        try {
            return mapper.writeValueAsBytes(json);
        } catch (final IOException e) {
            throw new WebException(e);
        }
    }
}