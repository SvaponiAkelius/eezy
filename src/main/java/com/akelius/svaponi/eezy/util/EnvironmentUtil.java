package com.akelius.svaponi.eezy.util;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EnvironmentUtil {

    /**
     * @param environment Spring environment
     * @return a new EnvironmentUtil instance
     */
    public static EnvironmentUtil build(final Environment environment) {
        return new EnvironmentUtil(environment);
    }

    /*
        instance ...
     */

    private final Environment env;

    // hidden constructor, use static build() instead
    private EnvironmentUtil(final Environment env) {
        this.env = env;
    }

    /**
     * @return a map with all property names in Spring Environment
     */
    public Set<String> getPropertyKeys() {
        return StreamSupport.stream(((AbstractEnvironment) env).getPropertySources().spliterator(), false)
                .filter(ps -> EnumerablePropertySource.class.isAssignableFrom(ps.getClass()))
                .map(EnumerablePropertySource.class::cast)
                .map(EnumerablePropertySource::getPropertyNames)
                .flatMap(Arrays::<String>stream)
                .collect(Collectors.toSet());
    }

    /**
     * @return a map with all properties in Spring Environment
     */
    public Map<String, String> getPropertyMap() {
        return getPropertyKeys().stream()
                .collect(Collectors.toMap(k -> k, k -> env.getProperty(k), (a, b) -> b, TreeMap::new));
    }
}
