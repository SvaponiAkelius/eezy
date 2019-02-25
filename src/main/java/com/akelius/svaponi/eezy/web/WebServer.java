package com.akelius.svaponi.eezy.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
public class WebServer {

    private final HttpServer server;
    private final PathMatcher antPathMatcher = new AntPathMatcher();
    private final Set<Mapping> mappings = new HashSet<>();

    public WebServer(final int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RequestHandler()); // handles any request /**
    }

    public WebServer start() {
        server.start();
        return this;
    }

    public void stop() {
        server.stop(0);
    }

    public WebServer addMapping(final String pathPattern, final int order, final WebController controller) {
        mappings.add(new Mapping()
                .order(order)
                .controller(controller)
                .pathPattern(pathPattern));
        log.info("add mapping '{}' >> {}", pathPattern, controller.getClass().getSimpleName());
        return this;
    }

    private class RequestHandler implements HttpHandler {

        WebResponse NOT_FOUND = new WebResponse().statusCode(404);

        @Override
        public void handle(final HttpExchange httpExchange) throws IOException {

            log.info("WebServer <<< {} {}", httpExchange.getRequestMethod(), httpExchange.getRequestURI());

            try {

                final RequestProcessor processor = new RequestProcessor(httpExchange);
                final WebResponse response = mappings.stream()
                        .filter(processor)
                        .sorted((Comparator.comparing(Mapping::getOrder)))
                        .findFirst()
                        .map(processor)
                        .orElse(NOT_FOUND);

                log.info("WebServer >>> {} {} {}", response.getStatusCode(), new String(response.getBody()), response.getHeaders());

                response.getHeaders().entrySet().stream().forEach(e -> httpExchange.getResponseHeaders().set(e.getKey(), e.getValue()));
                httpExchange.sendResponseHeaders(response.getStatusCode(), response.getBody().length);
                httpExchange.getResponseBody().write(response.getBody());

            } catch (final Exception e) {
                log.error("error: {}", e.getMessage());
                httpExchange.sendResponseHeaders(500, 0);
                httpExchange.getResponseHeaders().add("X-Application-Error", e.getMessage());
            } finally {
                httpExchange.getResponseBody().close();
            }
        }
    }

    @Getter
    @EqualsAndHashCode(of = {"order", "pathPattern"})
    private class Mapping {

        int order;
        String pathPattern;
        WebController controller;

        Mapping order(final int order) {
            this.order = order;
            return this;
        }

        Mapping pathPattern(final String pathPattern) {
            Assert.hasText(pathPattern, "invalid pathPattern");
            this.pathPattern = pathPattern;
            return this;
        }

        Mapping controller(final WebController controller) {
            Assert.notNull(controller, "null controller");
            this.controller = controller;
            return this;
        }
    }

    private class RequestProcessor implements Predicate<Mapping>, Function<Mapping, WebResponse> {

        private final HttpExchange httpExchange;
        private final String path;

        public RequestProcessor(final HttpExchange httpExchange) {
            this.httpExchange = httpExchange;
            this.path = httpExchange.getRequestURI().getRawSchemeSpecificPart();
        }

        @Override
        public boolean test(final Mapping mapping) {
            log.trace("test pattern match '{}' <~> '{}'", mapping.pathPattern, path);
            return antPathMatcher.match(mapping.pathPattern, path);
        }

        @Override
        public WebResponse apply(final Mapping mapping) {
            final Map<String, String> pathVariable = antPathMatcher.extractUriTemplateVariables(mapping.pathPattern, path);
            try {
                return mapping.controller.process(httpExchange, pathVariable);
            } catch (final Exception e) {
                throw new WebException(e);
            }
        }
    }
}
