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
import java.sql.Timestamp;

import static org.junit.Assert.*;
import static com.dghysc.hy.util.TestUtil.checkResponse;

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

        if (productRepository.count() == 0) {
            create();
        }
    }

    @Test
    public void create() {
        JSONObject requestBody = new JSONObject();

        requestBody.put("serial", testUtil.nextString());
        requestBody.put("IGT", testUtil.nextString());
        requestBody.put("ERP", testUtil.nextString());
        requestBody.put("central", testUtil.nextString());
        requestBody.put("area", testUtil.nextString());
        requestBody.put("design", testUtil.nextString());
        requestBody.put("beginTime", new Timestamp(System.currentTimeMillis()));
        requestBody.put("demandTime",  new Timestamp(System.currentTimeMillis()));
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
        requestBody.put("IGT", testUtil.nextString());
        requestBody.put("ERP", testUtil.nextString());
        requestBody.put("central", testUtil.nextString());
        requestBody.put("area", testUtil.nextString());
        requestBody.put("design", testUtil.nextString());
        requestBody.put("beginTime", new Timestamp(System.currentTimeMillis()));
        requestBody.put("demandTime",  new Timestamp(System.currentTimeMillis()));
        requestBody.put("endTime", new Timestamp(System.currentTimeMillis()));

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                baseUrl, HttpMethod.POST, request, JSONObject.class
        );

        checkResponse(responseEntity);
    }

    @Test
    public void get() {
        String url = baseUrl;

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        JSONObject response = checkResponse(responseEntity);
        assertNotNull(response.getJSONObject("data"));
        assertNotNull(response.getJSONObject("data").getInteger("total"));

        url += "?id=" + testUtil.nextId(Product.class);

        responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, request, JSONObject.class
        );

        checkResponse(responseEntity);

        url += "&withProcesses=1";
        responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, request, JSONObject.class
        );

        response = checkResponse(responseEntity);
        JSONObject product = response.getJSONObject("data");
        assertNotNull(product);
        assertNotNull(product.get("processes"));
    }

    @Test
    public void todayCreate() {
        final String url = baseUrl + "?create=1";

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        checkResponse(responseEntity);
    }

    @Test
    public void accordEnd() {
        final String url = baseUrl + "?end=1";

        String tomorrow = url + "&accord=1";
        String dayAfterTomorrow = url + "&accord=2";

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> todayResponseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);
        ResponseEntity<JSONObject> tomorrowResponseEntity = restTemplate
                .exchange(tomorrow, HttpMethod.GET, request, JSONObject.class);
        ResponseEntity<JSONObject> dayAfterTomorrowResponseEntity = restTemplate
                .exchange(dayAfterTomorrow, HttpMethod.GET, request, JSONObject.class);

        JSONObject todayResponse = checkResponse(todayResponseEntity);
        JSONObject tomorrowResponse = checkResponse(tomorrowResponseEntity);
        JSONObject dayAfterTomorrowResponse = checkResponse(dayAfterTomorrowResponseEntity);

        System.out.println("today:");
        for (Object obj : todayResponse.getJSONObject("data").getJSONArray("products")) {
            JSONObject json = (JSONObject) obj;
            System.out.println(json.getInteger("id") + ": " + json.getTimestamp("endTime"));
        }

        System.out.println("tomorrow:");
        for (Object obj : tomorrowResponse.getJSONObject("data").getJSONArray("products")) {
            JSONObject json = (JSONObject) obj;
            System.out.println(json.getInteger("id") + ": " + json.getTimestamp("endTime"));
        }

        System.out.println("dayAfterTomorrow:");
        for (Object obj : dayAfterTomorrowResponse.getJSONObject("data").getJSONArray("products")) {
            JSONObject json = (JSONObject) obj;
            System.out.println(json.getInteger("id") + ": " + json.getTimestamp("endTime"));
        }
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
    public void getProcesses() {
        Long id = testUtil.nextId(Product.class);
        final String baseUrl = ProductControllerTest.baseUrl + "/processes";
        String url = baseUrl + "?id=" + id;

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        checkResponse(responseEntity);
    }
}