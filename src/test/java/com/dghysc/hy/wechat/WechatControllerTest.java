package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.wechat.model.WechatUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.dghysc.hy.util.TestUtil.checkResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WechatControllerTest {

    private static final String baseUrl = "/api/wechat";

    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    public TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_ADMIN");
    }

    @Test
    public void getOne() {
        String url = baseUrl + "/user?id=" + testUtil.nextId(WechatUser.class);

        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, JSONObject.class
        );

        checkResponse(responseEntity);
    }

    @Test
    public void getAll() {
        String url = baseUrl + "/user";
        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, request, JSONObject.class
        );

        checkResponse(responseEntity);
    }

    @Test
    public void enableWechatUser() {
        String url = baseUrl + "/user/enable";
        String id = testUtil.nextId(WechatUser.class);

        JSONObject request = new JSONObject();
        request.put("id", id);

        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(request, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, JSONObject.class
        );

        checkResponse(responseEntity);
    }
}