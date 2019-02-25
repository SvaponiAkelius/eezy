package com.akelius.svaponi.eezy.controller;

import com.akelius.svaponi.eezy.util.MapBuilder;
import com.akelius.svaponi.eezy.web.WebController;
import com.akelius.svaponi.eezy.web.WebControllerMapping;
import com.akelius.svaponi.eezy.web.WebJsonResponse;
import com.akelius.svaponi.eezy.web.WebResponse;
import com.sun.net.httpserver.HttpExchange;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@WebControllerMapping("/sleep/{delay}")
public class SleepController implements WebController {

    @Override
    public WebResponse process(final HttpExchange httpExchange, final Map<String, String> pathVariables) throws Exception {
        final long start = System.currentTimeMillis();
        final String delay = pathVariables.getOrDefault("delay", "0");
        Thread.sleep(Long.parseLong(delay));
        return new WebJsonResponse()
                .body(new MapBuilder()
                        .put("delay", delay)
                        .put("real", System.currentTimeMillis() - start)
                        .build()
                )
                .statusCode(200)
                ;
    }
}
