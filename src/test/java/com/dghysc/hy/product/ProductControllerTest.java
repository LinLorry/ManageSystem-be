package com.dghysc.hy.product;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Work;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    private static final String baseUrl = "/api/product";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_PRODUCT_MANAGER");
    }

    @Test
    public void create() {
        JSONObject requestBody = new JSONObject();

        requestBody.put("serial", testUtil.nextString());
        requestBody.put("endTime", new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 28));
        requestBody.put("workId", testUtil.nextId(Work.class));

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                baseUrl, HttpMethod.POST, request, JSONObject.class
        );

        checkResponse(responseEntity);
    }

    @Test
    public void update() {
        JSONObject requestBody = new JSONObject();

        requestBody.put("id", testUtil.nextId(Product.class));
        requestBody.put("serial", testUtil.nextString());
        requestBody.put("endTime", new Timestamp(System.currentTimeMillis()));

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                baseUrl, HttpMethod.POST, request, JSONObject.class
        );

        checkResponse(responseEntity);
    }

    @Test
    public void find() throws URISyntaxException {
        final String url = baseUrl + "/find";

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @Transactional(readOnly = true)
    public void complete() {
        Long id = testUtil.nextId(Product.class);
        final String url = baseUrl + "/complete?id=" + id;

        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        boolean complete = product.getWork().getWorkProcesses().size() == product.getProductProcesses().size();

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, request, JSONObject.class
        );

        System.out.println(responseEntity.getBody());
        assertNotNull(responseEntity.getBody());
        JSONObject response = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCodeValue());

        if (complete) {
            assertEquals(1, response.getIntValue("status"));
        } else {
            assertEquals(0, response.getIntValue("status"));
        }
    }

    @Test
    public void todayCreate() throws URISyntaxException {
        final String url = baseUrl + "/todayCreate";

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void accordEnd() throws URISyntaxException {
        final String url = baseUrl + "/accordEnd";

        URI today = new URI(url);
        URI tomorrow = new URI(url + "?accord=1");
        URI dayAfterTomorrow = new URI(url + "?accord=2");

        HttpEntity<JSONObject> todayRequest = new HttpEntity<>(testUtil.getTokenHeader());
        HttpEntity<JSONObject> tomorrowRequest = new HttpEntity<>(testUtil.getTokenHeader());
        HttpEntity<JSONObject> dayAfterTomorrowRequest = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> todayResponse = restTemplate
                .exchange(today, HttpMethod.GET, todayRequest, JSONObject.class);
        ResponseEntity<JSONObject> tomorrowResponse = restTemplate
                .exchange(tomorrow, HttpMethod.GET, tomorrowRequest, JSONObject.class);
        ResponseEntity<JSONObject> dayAfterTomorrowResponse = restTemplate
                .exchange(dayAfterTomorrow, HttpMethod.GET, dayAfterTomorrowRequest, JSONObject.class);

        assertEquals(200, todayResponse.getStatusCodeValue());
        assertEquals(200, tomorrowResponse.getStatusCodeValue());
        assertEquals(200, dayAfterTomorrowResponse.getStatusCodeValue());
        System.out.println(todayResponse.getBody());

        System.out.println("today:");
        for (Object obj : Objects.requireNonNull(todayResponse.getBody())
                .getJSONObject("data").getJSONArray("products")) {
            JSONObject json = (JSONObject) obj;
            System.out.println(json.getInteger("id") + ": " + json.getTimestamp("endTime"));
        }

        System.out.println("tomorrow:");
        for (Object obj : Objects.requireNonNull(tomorrowResponse.getBody())
                .getJSONObject("data").getJSONArray("products")) {
            JSONObject json = (JSONObject) obj;
            System.out.println(json.getInteger("id") + ": " + json.getTimestamp("endTime"));
        }

        System.out.println("dayAfterTomorrow:");
        for (Object obj : Objects.requireNonNull(dayAfterTomorrowResponse.getBody())
                .getJSONObject("data").getJSONArray("products")) {
            JSONObject json = (JSONObject) obj;
            System.out.println(json.getInteger("id") + ": " + json.getTimestamp("endTime"));
        }
    }

    @Test
    public void delete() throws URISyntaxException {
        final String url = baseUrl + "/delete";

        URI uri = new URI(url);

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", testUtil.nextId(Product.class));

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, JSONObject.class);

        System.out.println(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    private void checkResponse(ResponseEntity<JSONObject> responseEntity) {
        JSONObject response = responseEntity.getBody();
        assertNotNull(response);
        System.out.println(response);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1, response.getIntValue("status"));
    }
}