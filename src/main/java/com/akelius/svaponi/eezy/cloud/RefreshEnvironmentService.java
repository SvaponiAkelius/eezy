package com.akelius.svaponi.eezy.cloud;

import com.akelius.intranet.util.rest.RestTemplateBuilder;
import com.akelius.svaponi.eezy.web.WebException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RefreshEnvironmentService {

    String baseUrl = "http://localhost:8888";
    String clientId = "property-service";
    String label = "development";
    RestTemplate restTemplate = RestTemplateBuilder.builder()
            .errorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(final ClientHttpResponse response) throws IOException {
                    return false;
                }

                @Override
                public void handleError(final ClientHttpResponse response) throws IOException {
                    throw new IllegalStateException("FOOBAR");
                }
            })
            .build();

    public Map<String, Object> fetchPropertyMap(final String... profiles) {
        final String profiles2 = Arrays.stream(profiles).collect(Collectors.joining(","));
        final URI uri;
        try {
            uri = new URI(String.format("%s/%s/%s/%s", baseUrl, clientId, profiles2, label));
        } catch (final URISyntaxException e) {
            throw new WebException(e);
        }
        final ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
        });
        switch (response.getStatusCodeValue()) {
            case 200:
                final Map body = response.getBody();
                return extractProperties(body);
        }
        throw new WebException("invalid environment request");
    }

    private Map<String, Object> extractProperties(final Map<String, Object> body) {
        final List<Map<String, ?>> propertySources = (List<Map<String, ?>>) body.get("propertySources");
        Assert.notNull(propertySources, "null propertySources");
        Assert.isAssignable(List.class, propertySources.getClass(), "propertySources not a List");
        return propertySources.stream()
                .filter(this::validPropertySource)
                .map(propertySource -> (Map<String, Object>) propertySource.get("source"))
                .flatMap(propertySourceMap -> propertySourceMap.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));
    }

    private boolean validPropertySource(final Map<String, ?> propertySource) {
        try {
            Assert.notNull(propertySource, "propertySource is null");
            Assert.isAssignable(List.class, propertySource.getClass(), "propertySource not a Map");
            Assert.isTrue(propertySource.containsKey("name"), "propertySource has not 'name' field");
            Assert.isTrue(propertySource.containsKey("source"), "propertySource has not 'source' field");
            return true;
        } catch (final Exception e) {
            log.debug("validPropertySource: {}", e.getMessage());
        }
        return false;
    }

    @Setter
    private static class Resp {
        String name;
        List<String> profiles;
        String label;
        String version;
        String state;
        List<PropertySourceResource> propertySources;

        public List<PropertySource> getPropertySources() {
            return propertySources.stream().map(PropertySourceResource::toMapPropertySource).collect(Collectors.toList());
        }
    }

    @Setter
    private static class PropertySourceResource {

        String name;
        Map<String, Object> source;

        private PropertySource toMapPropertySource() {
            return new MapPropertySource(name, source);
        }
    }

    public List<? extends PropertySource> fetchPropertySources(final String... profiles) {
        final String profiles2 = Arrays.stream(profiles).collect(Collectors.joining(","));
        final URI uri;
        try {
            uri = new URI(String.format("%s/%s/%s/%s", baseUrl, clientId, profiles2, label));
        } catch (final URISyntaxException e) {
            throw new WebException(e);
        }
        final ResponseEntity<Resp> response = restTemplate.exchange(uri, HttpMethod.GET, null, Resp.class);
        switch (response.getStatusCodeValue()) {
            case 200:
                final Resp body = response.getBody();
                return body.getPropertySources();
        }
        throw new WebException("invalid environment request");
    }
}
