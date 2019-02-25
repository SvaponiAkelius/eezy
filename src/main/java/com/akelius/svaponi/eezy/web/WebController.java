package com.akelius.svaponi.eezy.web;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Map;

public interface WebController {

    WebResponse UNSUPPORTED_METHOD = new WebResponse().statusCode(405);

    default WebResponse process(final HttpExchange httpExchange, final Map<String, String> pathVariables) throws Exception {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                return processGet(httpExchange, pathVariables);
            case "DELETE":
                return processDelete(httpExchange, pathVariables);
            case "POST":
                return processPost(httpExchange, pathVariables, readBody(httpExchange));
            case "PUT":
                return processPut(httpExchange, pathVariables, readBody(httpExchange));
            default:
                return UNSUPPORTED_METHOD;
        }
    }

    default byte[] readBody(final HttpExchange httpExchange) {
        try {
            final byte[] body = IOUtils.toByteArray(httpExchange.getRequestBody());
            return body;
        } catch (final IOException e) {
            throw new WebException(e);
        }
    }

    default WebResponse processGet(final HttpExchange httpExchange, final Map<String, String> pathVariables) throws Exception {
        return UNSUPPORTED_METHOD;
    }

    default WebResponse processDelete(final HttpExchange httpExchange, final Map<String, String> pathVariables) throws Exception {
        return UNSUPPORTED_METHOD;
    }

    default WebResponse processPost(final HttpExchange httpExchange, final Map<String, String> pathVariables, final byte[] body) throws Exception {
        return UNSUPPORTED_METHOD;
    }

    default WebResponse processPut(final HttpExchange httpExchange, final Map<String, String> pathVariables, final byte[] body) throws Exception {
        return UNSUPPORTED_METHOD;
    }
}
