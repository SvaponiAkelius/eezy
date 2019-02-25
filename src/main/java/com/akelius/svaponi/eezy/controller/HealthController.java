package com.akelius.svaponi.eezy.controller;

import com.akelius.svaponi.eezy.util.MapBuilder;
import com.akelius.svaponi.eezy.web.*;
import com.sun.net.httpserver.HttpExchange;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@WebControllerMapping("/actuator/health")
public class HealthController implements WebController {

    private static final long start = System.currentTimeMillis();

    @Override
    public WebResponse process(final HttpExchange httpExchange, final Map<String, String> pathVariables) {
        return new WebJsonResponse()
                .body(new MapBuilder()
                        .put("status", "UP")
                        .goDown("details")
                        .put("startTime", start)
                        .put("currentTime", System.currentTimeMillis())
                        .build()
                )
                .statusCode(200);
    }
}
