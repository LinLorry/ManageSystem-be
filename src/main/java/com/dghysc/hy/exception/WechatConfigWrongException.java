package com.dghysc.hy.exception;

import javax.validation.constraints.NotNull;

public class WechatConfigWrongException extends Exception {
    public WechatConfigWrongException(@NotNull String message) {
        super(message);
    }
}
