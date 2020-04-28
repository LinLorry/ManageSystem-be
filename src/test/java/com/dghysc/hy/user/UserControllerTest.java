package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.util.TestUtil;
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
public class UserControllerTest {

    private static final String baseUrl = "/api/user";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Test
    public void getProfile() {
        String url = baseUrl + "/profile";

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);
        checkResponse(responseEntity);
    }

    @Test
    public void getDynamic() {
        String url = baseUrl + "/dynamic";

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);
        checkResponse(responseEntity);
    }

    @Test
    public void getLevel() {
        final String url = baseUrl + "/level";
        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);
        checkResponse(responseEntity);
    }

    @Test
    public void getProcesses() {
        String url = baseUrl + "/processes";

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        checkResponse(responseEntity);
    }

    @Test
    public void getSelfFinishProductProcesses() {
        testUtil.setAuthorities("ROLE_WORKER");

        String url = baseUrl + "/selfFinish";

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        checkResponse(responseEntity);
    }
}