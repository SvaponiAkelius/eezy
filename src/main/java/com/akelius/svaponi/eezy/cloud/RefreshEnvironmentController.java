package com.akelius.svaponi.eezy.cloud;

import com.akelius.svaponi.eezy.Application;
import com.akelius.svaponi.eezy.util.EnvironmentUtil;
import com.akelius.svaponi.eezy.web.WebControllerMapping;
import com.akelius.svaponi.eezy.web.WebJsonController;
import com.akelius.svaponi.eezy.web.WebJsonResponse;
import com.akelius.svaponi.eezy.web.WebResponse;
import com.sun.net.httpserver.HttpExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@WebControllerMapping("/actuator/refresh")
public class RefreshEnvironmentController implements WebJsonController {

    @Autowired
    RefreshEnvironmentService refreshEnvironmentService;

    @Autowired
    Environment environment;

    @Autowired
    ContextRefresher contextRefresher;

    @Override
    public WebResponse processGet(final HttpExchange httpExchange, final Map<String, String> pathVariables) {
        final Set<String> keys = contextRefresher.refresh();
        return new WebJsonResponse().body(keys).statusCode(201);
    }

    @Override
    public WebResponse processJsonPost(final HttpExchange httpExchange, final Map<String, String> pathVariables, final Map<String, Object> body) {
        final Map<String, Object> properties = refreshEnvironmentService.fetchPropertyMap(getActiveProfiles());
        final MapPropertySource e = new MapPropertySource("e", properties);
        Application.getInstance().getConfigurableEnvironment().getPropertySources().addFirst(e);
        return new WebJsonResponse()
                .body(EnvironmentUtil.build(Application.getInstance().getConfigurableEnvironment()).getPropertyMap())
                .statusCode(201)
                ;
    }

    @Override
    public WebResponse processJsonPut(final HttpExchange httpExchange, final Map<String, String> pathVariables, final Map<String, Object> body) {
        final List<? extends PropertySource> properties = refreshEnvironmentService.fetchPropertySources(getActiveProfiles());
        properties.stream().forEach(propertySource -> Application.getInstance().getConfigurableEnvironment().getPropertySources().addFirst(propertySource));
        return new WebJsonResponse()
                .body(EnvironmentUtil.build(Application.getInstance().getConfigurableEnvironment()).getPropertyMap())
                .statusCode(201)
                ;
    }

    public String[] getActiveProfiles() {
        return Stream.of(
                Arrays.stream(environment.getActiveProfiles()).collect(Collectors.toList()),
                environment.getProperty("spring.profiles.active", List.class, Collections.emptyList()),
                environment.getProperty("spring.profiles.include", List.class, Collections.emptyList())
        )
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Objects::toString)
                .toArray(String[]::new);
    }
}
