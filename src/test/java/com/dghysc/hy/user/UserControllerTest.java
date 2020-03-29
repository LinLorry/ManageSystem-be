package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static com.dghysc.hy.util.TestUtil.checkResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private final String baseUrl = "/api/user";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_ADMIN");
    }

    @Test
    public void create() {
        String username = testUtil.nextString();
        String password = testUtil.nextString();
        String name = testUtil.nextString();

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("password", password);
        requestBody.put("name", name);

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                baseUrl, HttpMethod.POST, request, JSONObject.class
        );

        checkResponse(responseEntity);

        JSONObject response = checkResponse(responseEntity);
        Long id = response.getJSONObject("data").getLong("id");
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        assertEquals(username, user.getUsername());
        assertEquals(name, user.getName());
    }

    @Test
    public void update() {
        Long id;
        do {
            id = testUtil.nextId(User.class);
        } while (id == 1);
        String username = testUtil.nextString();
        String name = testUtil.nextString();

        Set<Integer> roles = new HashSet<>();
        while (roles.size() != roleRepository.count() && roles.size() < 3) {
            roles.add(testUtil.nextId(Role.class));
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", id);
        requestBody.put("username", username);
        requestBody.put("name", name);
        requestBody.put("roles", roles);

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                baseUrl, HttpMethod.POST, request, JSONObject.class
        );

        JSONObject response = checkResponse(responseEntity);
        assertEquals(id, response.getJSONObject("data").getLong("id"));

        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        assertEquals(username, user.getUsername());
        assertEquals(name, user.getName());
    }

    @Test
    public void get() {
        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(baseUrl, HttpMethod.GET, request, JSONObject.class);

        checkResponse(responseEntity);

        String url = baseUrl + "?id=" + testUtil.nextId(User.class);
        responseEntity = restTemplate
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
    public void getAuthority() {
        String url = baseUrl + "/authority?id=" + testUtil.nextId(User.class);
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
    public void disableUser() {
        String url = baseUrl + "/disable";

        Long id;
        User user = testUtil.getUser();

        do {
            id = testUtil.nextId(User.class);
        } while (user.getId().equals(id));

        JSONObject requestBody = new JSONObject();

        requestBody.put("id", id);
        requestBody.put("operation", true);

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, request, JSONObject.class
        );

        assertTrue(userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new).getDisable()
        );

        checkResponse(responseEntity);

        requestBody = new JSONObject();
        requestBody.put("id", id);
        requestBody.put("operation", false);

        request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());
        responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, request, JSONObject.class
        );

        assertFalse(userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new).getDisable()
        );

        checkResponse(responseEntity);

        requestBody = new JSONObject();
        requestBody.put("id", id);

        request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());
        responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, request, JSONObject.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}