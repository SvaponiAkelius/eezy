package com.akelius.svaponi.eezy.web;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class WebResponse {

    private int statusCode;
    private byte[] body;
    private final Map<String, String> headers = new HashMap<>();

    public WebResponse statusCode(final int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public WebResponse body(final byte[] body) {
        this.body = body;
        return this;
    }

    public WebResponse addHeader(final String name, final String... values) {
        if (StringUtils.hasText(name)) {
            this.headers.put(name, Arrays.stream(values).filter(StringUtils::hasText).collect(Collectors.joining(",")));
        }
        return this;
    }
}
