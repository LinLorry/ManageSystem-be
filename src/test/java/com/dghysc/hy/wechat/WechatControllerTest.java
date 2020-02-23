package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.wechat.model.WechatUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WechatControllerTest {

    private static final String baseUrl = "/api/wechat";

    private HttpHeaders headers;

    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    public TestUtil testUtil;

    @Before
    public void setUp() {
        headers = testUtil.getTokenHeader();
    }

    @Test
    public void getOne() {
        String url = baseUrl + "?id=" + testUtil.nextId(WechatUser.class);

        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, JSONObject.class
        );

        JSONObject response = responseEntity.getBody();
        System.out.println(response);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assert response != null;
        assertEquals(1, response.getIntValue("status"));
    }

    @Test
    public void getAll() {
        HttpEntity<JSONObject> request = new HttpEntity<>(headers);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                baseUrl, HttpMethod.GET, request, JSONObject.class
        );

        JSONObject response = responseEntity.getBody();
        System.out.println(response);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assert response != null;
        assertEquals(1, response.getIntValue("status"));
    }

    @Test
    public void addOrUpdateWechatUser() {
        String id = testUtil.nextId(WechatUser.class);
        Long userId = testUtil.nextId(User.class);

        JSONObject request = new JSONObject();
        request.put("id", id);
        request.put("userId", userId);

        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                baseUrl, HttpMethod.POST, requestEntity, JSONObject.class
        );

        JSONObject response = responseEntity.getBody();
        System.out.println(response);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assert response != null;
    }
}