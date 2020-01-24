package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.ParentMenu;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.repo.ChildMenuRepository;
import com.dghysc.hy.user.repo.ParentMenuRepository;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.util.TestUtil;
import net.bytebuddy.utility.RandomString;
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

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MenuControllerTest {

    private final static String baseUrl = "/api/menu";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ParentMenuRepository parentMenuRepository;

    @Autowired
    private ChildMenuRepository childMenuRepository;

    @Before
    public void initTest() throws Exception {
        if (parentMenuRepository.count() == 0) {
            createParent();
        }

        if (childMenuRepository.count() == 0) {
            createChild();
        }
    }

    @Test
    public void createParent() throws URISyntaxException {
        String url = baseUrl + "/parent";

        URI uri = new URI(url);

        JSONObject body = new JSONObject();
        body.put("name", RandomString.make());

        HttpEntity<JSONObject> request = new HttpEntity<>(body, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.POST, request, JSONObject.class);

        JSONObject responseBody = response.getBody();
        System.out.println(responseBody);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void updateParent() throws Exception {
        String url = baseUrl + "/parent";

        URI uri = new URI(url);

        JSONObject body = new JSONObject();
        body.put("id", testUtil.nextId(ParentMenu.class));
        body.put("name", RandomString.make());

        HttpEntity<JSONObject> request = new HttpEntity<>(body, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.POST, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getAllParent() throws URISyntaxException {
        String url = baseUrl + "/parent";

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getOneParent() throws Exception {
        String url = baseUrl + "/parent?id=" + testUtil.nextId(ParentMenu.class);

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void deleteParent() throws Exception {
        String url = baseUrl + "/parent?id=" + testUtil.nextId(ParentMenu.class);

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getAllChild() throws URISyntaxException {
        String url = baseUrl + "/child";

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getOneChild() throws Exception {
        String url = baseUrl + "/child?id=" + testUtil.nextId(ChildMenu.class);

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void createChild() throws Exception {
        String url = baseUrl + "/child";

        URI uri = new URI(url);

        JSONObject body = new JSONObject();
        body.put("name", RandomString.make());
        body.put("url", RandomString.make());
        body.put("parentId", testUtil.nextId(ParentMenu.class));

        final long roleCount = roleRepository.count();

        if (roleCount > 0) {
            JSONArray roleIds = new JSONArray();
            final long num = Math.abs(testUtil.nextLong() % roleCount) + 1;
            for (int i = 0; i < num; ++i) {
                roleIds.add(testUtil.nextId(Role.class));
            }
            body.put("roles", roleIds);
        }

        HttpEntity<JSONObject> request = new HttpEntity<>(body, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.POST, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void updateChild() throws Exception {
        String url = baseUrl + "/child";

        URI uri = new URI(url);

        JSONObject body = new JSONObject();
        body.put("id", testUtil.nextId(ChildMenu.class));
        body.put("name", RandomString.make());
        body.put("url", RandomString.make());
        body.put("parentId", testUtil.nextId(ParentMenu.class));

        final long roleCount = roleRepository.count();

        if (roleCount > 0) {
            JSONArray roleIds = new JSONArray();
            final long num = Math.abs(testUtil.nextLong() % roleCount) + 1;
            for (int i = 0; i < num; ++i) {
                roleIds.add(testUtil.nextId(Role.class));
            }
            body.put("roles", roleIds);
        }

        HttpEntity<JSONObject> request = new HttpEntity<>(body, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.POST, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void deleteChild() throws Exception {
        String url = baseUrl + "/child?id=" + testUtil.nextId(ChildMenu.class);

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }
}