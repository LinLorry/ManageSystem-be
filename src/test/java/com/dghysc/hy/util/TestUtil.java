package com.dghysc.hy.util;

import com.dghysc.hy.until.TokenUtil;
import com.dghysc.hy.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {
    @Value("${manage.authentication.name}")
    private String authenticationName;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserService userService;

    public HttpHeaders getTokenHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",
                authenticationName + " " + tokenUtil.generateToken(
                        userService.loadUserByUsername("LinLorry")
                )
        );

        return headers;
    }
}
