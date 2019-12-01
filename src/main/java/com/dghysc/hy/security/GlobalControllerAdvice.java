package com.dghysc.hy.security;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global Controller Advice
 *  Handler Exception, don't allow return exception message
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 * @see org.springframework.web.bind.annotation.ExceptionHandler
 */
@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSONObject> globalExceptionHandler(Exception exception) {
        exception.printStackTrace();

        JSONObject json = new JSONObject();
        json.put("status", 0);
        json.put("message", "Error!");

        return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
