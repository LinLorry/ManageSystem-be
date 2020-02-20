package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.util.TestUtil;
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

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoleControllerTest {

    private final static String baseUrl = "/api/role";

    private HttpHeaders headers;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Before
    public void initTest() {
        if (headers == null) {
            testUtil.setAuthorities("ROLE_ADMIN");
        }

        headers = testUtil.getTokenHeader();

        if (roleRepository.count() == 1) {
            create();
        }

        Role role = roleRepository.findById(1).orElseThrow(EntityNotFoundException::new);

        if (!"ROLE_ADMIN".equals(role.getRole())) {
            roleService.update(1, "ROLE_ADMIN", null, null, null);
        }
    }

    @Test
    public void create() {
        Role role = roleRepository.findById(testUtil.nextId(Role.class))
                .orElseThrow(EntityNotFoundException::new);
        System.out.println(role.getCreateUser());
        System.out.println(role.getUpdateUser());

        System.out.println(role);
        JSONObject body = new JSONObject();

        body.put("role", testUtil.nextString());
        body.put("name", testUtil.nextString());

        HttpEntity<JSONObject> request = new HttpEntity<>(body, headers);

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(baseUrl, HttpMethod.POST, request, JSONObject.class);

        JSONObject responseBody = response.getBody();
        System.out.println(responseBody);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void update() {
        JSONObject body = new JSONObject();

        Integer id = testUtil.nextId(Role.class);
        List<Long> users = new ArrayList<>();
        List<Integer> menus = new ArrayList<>();

        users.add(testUtil.nextId(User.class));
        menus.add(testUtil.nextId(ChildMenu.class));

        while (id == 1) {
            id = testUtil.nextId(Role.class);
        }

        body.put("id", id);
        body.put("role", testUtil.nextString());
        body.put("name", testUtil.nextString());
        body.put("users", users);
        body.put("menus", menus);

        HttpEntity<JSONObject> request = new HttpEntity<>(body, headers);

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(baseUrl, HttpMethod.POST, request, JSONObject.class);

        JSONObject responseBody = response.getBody();
        System.out.println(responseBody);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getOne() {
        String url = baseUrl + "?id=" + testUtil.nextId(Role.class);

        HttpEntity<JSONObject> request = new HttpEntity<>(headers);

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        JSONObject responseBody = response.getBody();
        System.out.println(url);
        System.out.println(responseBody);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getAll() {
        HttpEntity<JSONObject> request = new HttpEntity<>(headers);

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(baseUrl, HttpMethod.GET, request, JSONObject.class);

        JSONObject responseBody = response.getBody();
        System.out.println(responseBody);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void delete() {
        String url = baseUrl + "?id=" + testUtil.nextId(Role.class);

        HttpEntity<JSONObject> request = new HttpEntity<>(headers);

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(url, HttpMethod.DELETE, request, JSONObject.class);

        JSONObject responseBody = response.getBody();
        System.out.println(responseBody);

        assertEquals(200, response.getStatusCodeValue());
    }
}
