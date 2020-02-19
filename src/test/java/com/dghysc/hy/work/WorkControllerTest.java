package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.WorkRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkControllerTest {

    private static final String baseUrl = "/api/work";

    private static final Random random = new Random();

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private WorkRepository workRepository;

    @Test
    public void create() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("name", testUtil.nextString());
        requestBody.put("comment", testUtil.nextString());

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                baseUrl, HttpMethod.POST, request, JSONObject.class
        );

        JSONObject response = responseEntity.getBody();
        System.out.println(response);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(response);
        assertEquals(1, response.getIntValue("status"));
    }

    @Test
    public void update() {
        JSONObject requestBody = new JSONObject();

        requestBody.put("id", testUtil.nextId(Work.class));
        requestBody.put("name", testUtil.nextString());
        requestBody.put("comment", testUtil.nextString());

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                baseUrl, HttpMethod.POST, request, JSONObject.class
        );

        JSONObject response = responseEntity.getBody();
        System.out.println(response);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(response);
        assertEquals(1, response.getIntValue("status"));
    }

    @Test
    public void get() {
        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(baseUrl, HttpMethod.GET, request, JSONObject.class);

        JSONObject response = responseEntity.getBody();
        System.out.println(response);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(response);
        assertEquals(1, response.getIntValue("status"));

        String url = baseUrl + "?id=" + testUtil.nextId(Work.class);

        responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        response = responseEntity.getBody();
        System.out.println(response);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(response);
        assertEquals(1, response.getIntValue("status"));
    }

    @Test
    public void delete() throws URISyntaxException {
        final String url = baseUrl + "/delete";
        final int number = (int) workRepository.count();
        URI uri = new URI(url);

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", random.nextInt(number));

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }
}