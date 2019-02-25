package com.akelius.svaponi.eezy.controller;

import com.akelius.svaponi.eezy.util.MapBuilder;
import com.akelius.svaponi.eezy.web.WebController;
import com.akelius.svaponi.eezy.web.WebControllerMapping;
import com.akelius.svaponi.eezy.web.WebResponse;
import com.akelius.svaponi.eezy.web.WebJsonResponse;
import com.sun.net.httpserver.HttpExchange;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@WebControllerMapping("/**")
public class InfoController implements WebController {

    @Override
    public WebResponse process(final HttpExchange httpExchange, final Map<String, String> pathVariables) {
        return new WebJsonResponse()
                .body(new MapBuilder()
                        .put("method", httpExchange.getRequestMethod())
                        .put("uri", httpExchange.getRequestURI())
                        .put("headers", httpExchange.getRequestHeaders())
                        .put("mapping", getClass().getAnnotation(WebControllerMapping.class).value())
                        .put("pathVariables", pathVariables)
                        .build()
                )
                .statusCode(200)
                ;
    }
}
