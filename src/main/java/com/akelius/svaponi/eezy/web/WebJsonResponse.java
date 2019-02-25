package com.akelius.svaponi.eezy.web;

import com.akelius.svaponi.eezy.util.JsonUtil;

import java.util.Map;

public class WebJsonResponse extends WebResponse {

    public WebJsonResponse() {
        addHeader("Content-Type", "application/json; charset=UTF-8");
    }

    public WebJsonResponse bodyMap(final Map body) {
        this.body(JsonUtil.toJson(body));
        return this;
    }

    public WebJsonResponse body(final Object body) {
        this.body(JsonUtil.toJson(body));
        return this;
    }
}
