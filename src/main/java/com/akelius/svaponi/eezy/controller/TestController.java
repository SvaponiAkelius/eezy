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
@WebControllerMapping("/test/{id}")
public class TestController implements WebController {

    @Override
    public WebResponse process(final HttpExchange httpExchange, final Map<String, String> pathVariables) throws Exception {
        return new WebJsonResponse()
                .body(new MapBuilder()
                        .put("id", pathVariables.get("id"))
                        .put("method", httpExchange.getRequestMethod())
                        .put("uri", httpExchange.getRequestURI())
                        .put("mapping", getClass().getAnnotation(WebControllerMapping.class).value())
                        .build()
                )
                .statusCode(200)
                ;
    }
}
