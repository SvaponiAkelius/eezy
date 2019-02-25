package com.akelius.svaponi.eezy.web;

import com.akelius.svaponi.eezy.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public interface WebJsonController extends WebController {

    @Override
    default WebResponse processPost(final HttpExchange httpExchange, final Map<String, String> pathVariables, final byte[] body) throws Exception {
        return processJsonPost(httpExchange, pathVariables, JsonUtil.parseJsonToMap(body));
    }

    @Override
    default WebResponse processPut(final HttpExchange httpExchange, final Map<String, String> pathVariables, final byte[] body) throws Exception {
        return processJsonPut(httpExchange, pathVariables, JsonUtil.parseJsonToMap(body));
    }

    default WebResponse processJsonPost(final HttpExchange httpExchange, final Map<String, String> pathVariables, final Map<String, Object> body) throws Exception {
        return UNSUPPORTED_METHOD;
    }

    default WebResponse processJsonPut(final HttpExchange httpExchange, final Map<String, String> pathVariables, final Map<String, Object> body) throws Exception {
        return UNSUPPORTED_METHOD;
    }
}
