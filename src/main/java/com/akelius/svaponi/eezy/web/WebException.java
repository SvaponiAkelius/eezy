package com.akelius.svaponi.eezy.web;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

public class WebException extends NestedRuntimeException {

    public WebException(final String msg) {
        super(msg);
    }

    public WebException(@Nullable final String msg, @Nullable final Throwable cause) {
        super(msg, cause);
    }

    public WebException(@Nullable final Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
