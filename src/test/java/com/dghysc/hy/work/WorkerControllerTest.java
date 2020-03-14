package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.UserService;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.TestUtil;
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
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkerControllerTest {

    private static final String baseUrl = "/api/worker";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private UserService userService;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_WORKER_MANAGER");
    }

    @Test
    public void checkWorker() {
        final Long id = testUtil.nextId(User.class);
        final String url = baseUrl + "/check?id=" + id;
        User user = userService.loadWithRolesById(id);
        boolean isWorker = false;

        for (Role role : user.getAuthorities()) {
            if ("ROLE_WORKER".equals(role.getRole())) {
                isWorker = true;
                break;
            }
        }

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        JSONObject response = checkResponse(responseEntity);
        assertEquals(isWorker, response.getBooleanValue("data"));
    }
}