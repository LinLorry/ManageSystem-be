package com.dghysc.hy.security;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

/**
 * Global Controller Advice
 *  Handler Exception, don't allow return exception message
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 * @see org.springframework.web.bind.annotation.ExceptionHandler
 */
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public JSONObject accessDeniedHandler() {
        JSONObject json = new JSONObject();
        json.put("status", 0);
        json.put("message", "Forbidden");
        return json;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JSONObject globalExceptionHandler(Exception exception) {
        exception.printStackTrace();

        JSONObject json = new JSONObject();
        json.put("status", 0);
        json.put("message", "Error!");

        return json;
    }
}
