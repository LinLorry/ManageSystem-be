package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.repo.ProcessRepository;
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserProcessControllerTest {

    private static final String baseUrl = "/api/userProcess";

    private static Iterator<User> users = null;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ProcessRepository processRepository;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_WORKER_MANAGER");
        if (users == null || !users.hasNext()) {
            users = testUtil.loadUsersByAuthority("ROLE_WORKER");
        }
    }

    @Test
    public void get() {
        String url = baseUrl + "?id=" + testUtil.nextId(User.class);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        JSONObject response = responseEntity.getBody();
        System.out.println(response);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(response);
        assertEquals(1, response.getIntValue("status"));
    }

    @Test
    public void post() {
        JSONObject requestBody = new JSONObject();

        Set<Integer> processes = new HashSet<>();
        while (processes.size() != processRepository.count() && processes.size() < 3) {
            processes.add(testUtil.nextId(Process.class));
        }

        requestBody.put("id", users.next().getId());
        requestBody.put("processes", processes);

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
}