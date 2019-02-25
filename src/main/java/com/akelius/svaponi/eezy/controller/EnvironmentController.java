package com.akelius.svaponi.eezy.controller;

import com.akelius.svaponi.eezy.Application;
import com.akelius.svaponi.eezy.util.EnvironmentUtil;
import com.akelius.svaponi.eezy.util.MapBuilder;
import com.akelius.svaponi.eezy.web.WebControllerMapping;
import com.akelius.svaponi.eezy.web.WebJsonController;
import com.akelius.svaponi.eezy.web.WebJsonResponse;
import com.akelius.svaponi.eezy.web.WebResponse;
import com.sun.net.httpserver.HttpExchange;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@WebControllerMapping("/actuator/env")
public class EnvironmentController implements WebJsonController {

    EnvironmentEndpoint environmentEndpoint;

    public EnvironmentController(final Environment environment) {
        this.environmentEndpoint = new EnvironmentEndpoint(environment);
    }

    //@Override
    public WebResponse processGet2(final HttpExchange httpExchange, final Map<String, String> pathVariables) {
        return new WebJsonResponse()
                .body(EnvironmentUtil.build(Application.getInstance().getConfigurableEnvironment()).getPropertyMap())
                .statusCode(200)
                ;
    }

    @Override
    public WebResponse processGet(final HttpExchange httpExchange, final Map<String, String> pathVariables) {
        final String pattern = pathVariables.get("pattern");
        final EnvironmentEndpoint.EnvironmentDescriptor environmentDescriptor = environmentEndpoint.environment(pattern);
        return new WebJsonResponse()
                .body(environmentDescriptor)
                .statusCode(200)
                ;
    }

    @Override
    public WebResponse processJsonPost(final HttpExchange httpExchange, final Map<String, String> pathVariables, final Map<String, Object> body) {
        final String name = (String) body.get("name");
        if (StringUtils.hasText(name)) {
            Application.getInstance().getConfigurableEnvironment().getPropertySources()
                    .addFirst(new MapPropertySource("e", new MapBuilder()
                            .put(name, body.get("value"))
                            .build()));
            return new WebJsonResponse()
                    .body(EnvironmentUtil.build(Application.getInstance().getConfigurableEnvironment()).getPropertyMap())
                    .statusCode(201)
                    ;
        }
        return new WebJsonResponse()
                .body(EnvironmentUtil.build(Application.getInstance().getConfigurableEnvironment()).getPropertyMap())
                .statusCode(204)
                ;
    }
}
