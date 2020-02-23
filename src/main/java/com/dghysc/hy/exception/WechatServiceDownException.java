package com.dghysc.hy.exception;

import javax.validation.constraints.NotNull;

public class WechatServiceDownException extends Exception {

    public WechatServiceDownException() {
    }

    public WechatServiceDownException(@NotNull String message) {
        super(message);
    }
}
