package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.repo.ProcessRepository;
import com.dghysc.hy.work.repo.WorkRepository;
import org.junit.Assert;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkProcessControllerTest {

    private static final String baseUrl = "/api/workProcess";

    private static final Random random = new Random();

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Test
    public void create() throws URISyntaxException {
        final String url = baseUrl + "/create";
        final int workNumber = (int) workRepository.count();
        final int processNumber = (int) processRepository.count();
        URI uri = new URI(url);

        JSONObject requestBody = new JSONObject();
        requestBody.put("workId", random.nextInt(workNumber));
        requestBody.put("processId", random.nextInt(processNumber));

        requestBody.put("sequenceNumber", random.nextInt(10));

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate.postForEntity(uri, request, JSONObject.class);

        System.out.println(response.getBody());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void update() throws URISyntaxException {
        final String url = baseUrl + "/update";
        final int workNumber = (int) workRepository.count();
        final int processNumber = (int) processRepository.count();
        URI uri = new URI(url);

        JSONObject requestBody = new JSONObject();
        requestBody.put("workId", random.nextInt(workNumber));
        requestBody.put("processId", random.nextInt(processNumber));
        requestBody.put("sequenceNumber", random.nextInt(10));

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate.postForEntity(uri, request, JSONObject.class);

        System.out.println(response.getBody());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void find() throws URISyntaxException {
        final String url = baseUrl + "/find";

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void delete() throws URISyntaxException {
        final String url = baseUrl + "/delete";
        URI uri = new URI(url);

        JSONObject requestBody = new JSONObject();
        requestBody.put("workId", 1);
        requestBody.put("processId", 6);

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, JSONObject.class);

        System.out.println(response.getBody());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }
}