package com.dghysc.hy.product;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.util.ZoneIdUtil;
import com.dghysc.hy.work.model.Process;
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
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import static com.dghysc.hy.util.TestUtil.checkResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    private static final String baseUrl = "/api/product";

    private static final ZoneId zoneId = ZoneIdUtil.UTC;

    private static final ZonedDateTime today = LocalDate.now().atStartOfDay(zoneId);

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
        LocalDate today = LocalDate.now();

        requestBody.put("serial", testUtil.nextString());
        requestBody.put("IGT", testUtil.nextString());
        requestBody.put("ERP", testUtil.nextString());
        requestBody.put("central", testUtil.nextString());
        requestBody.put("area", testUtil.nextString());
        requestBody.put("design", testUtil.nextString());
        requestBody.put("beginTime", Timestamp.valueOf(today.plusDays(-testUtil.nextInt(365)).atStartOfDay()));
        requestBody.put("demandTime", Timestamp.valueOf(today.plusDays(testUtil.nextInt(365)).atStartOfDay()));
        requestBody.put("endTime", Timestamp.valueOf(today.plusDays(testUtil.nextInt(365)).atStartOfDay()));
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
        LocalDate today = LocalDate.now();

        requestBody.put("id", testUtil.nextId(Product.class));
        requestBody.put("serial", testUtil.nextString());
        requestBody.put("IGT", testUtil.nextString());
        requestBody.put("ERP", testUtil.nextString());
        requestBody.put("central", testUtil.nextString());
        requestBody.put("area", testUtil.nextString());
        requestBody.put("design", testUtil.nextString());
        requestBody.put("beginTime", Timestamp.valueOf(today.plusDays(-testUtil.nextInt(365)).atStartOfDay()));
        requestBody.put("demandTime", Timestamp.valueOf(today.plusDays(testUtil.nextInt(365)).atStartOfDay()));
        requestBody.put("endTime", Timestamp.valueOf(today.plusDays(testUtil.nextInt(365)).atStartOfDay()));

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
    public void conditionsGet() {
        UriComponentsBuilder builder;
        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());
        ResponseEntity<JSONObject> responseEntity;

        builder = UriComponentsBuilder.fromUriString(baseUrl);
        addDateQueryParam(builder, "createTimeAfter", false);
        addDateQueryParam(builder, "createTimeBefore", true);

        System.out.println(builder.toUriString());

        responseEntity = restTemplate
                .exchange(builder.build().toString(), HttpMethod.GET, request, JSONObject.class);

        checkResponse(responseEntity);

        builder = UriComponentsBuilder.fromUriString(baseUrl);
        addDateQueryParam(builder, "beginTimeAfter", false);
        addDateQueryParam(builder, "beginTimeBefore", true);

        responseEntity = restTemplate
                .exchange(builder.build().toString(), HttpMethod.GET, request, JSONObject.class);
        checkResponse(responseEntity);

        builder = UriComponentsBuilder.fromUriString(baseUrl);
        addDateQueryParam(builder, "demandTimeAfter", false);
        addDateQueryParam(builder, "demandTimeBefore", true);

        responseEntity = restTemplate
                .exchange(builder.build().toString(), HttpMethod.GET, request, JSONObject.class);
        checkResponse(responseEntity);

        builder = UriComponentsBuilder.fromUriString(baseUrl);
        addDateQueryParam(builder, "endTimeAfter", false);
        addDateQueryParam(builder, "endTimeBefore", true);

        responseEntity = restTemplate
                .exchange(builder.build().toString(), HttpMethod.GET, request, JSONObject.class);
        checkResponse(responseEntity);

        builder = UriComponentsBuilder.fromUriString(baseUrl);
        addDateQueryParam(builder, "completeTimeAfter", false);
        addDateQueryParam(builder, "completeTimeBefore", true);
        builder.queryParam("complete", true);

        responseEntity = restTemplate
                .exchange(builder.build().toString(), HttpMethod.GET, request, JSONObject.class);
        checkResponse(responseEntity);

        builder = UriComponentsBuilder.fromUriString(baseUrl);

        addDateQueryParam(builder, "createTimeAfter", false);
        addDateQueryParam(builder, "createTimeBefore", true);
        addDateQueryParam(builder, "beginTimeAfter", false);
        addDateQueryParam(builder, "beginTimeBefore", true);
        addDateQueryParam(builder, "demandTimeAfter", false);
        addDateQueryParam(builder, "demandTimeBefore", true);
        addDateQueryParam(builder, "endTimeAfter", false);
        addDateQueryParam(builder, "endTimeBefore", true);

        responseEntity = restTemplate
                .exchange(builder.build().toString(), HttpMethod.GET, request, JSONObject.class);
        checkResponse(responseEntity);
    }

    @Test
    public void getComplete() {
        String url = baseUrl + "?complete=1";

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, request, JSONObject.class);

        JSONObject response = checkResponse(responseEntity);
        assertNotNull(response.getJSONObject("data"));
        assertNotNull(response.getJSONObject("data").getInteger("total"));
    }

    @Test
    public void completeProcess() {
        testUtil.setAuthorities("ROLE_WORKER");
        final String url = baseUrl + "/completeProcess";
        JSONObject requestBody = new JSONObject();

        requestBody.put("productId", testUtil.nextId(Product.class));
        requestBody.put("processId", testUtil.nextId(Process.class));

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, request, JSONObject.class
        );

        assert responseEntity.getBody() != null;
        JSONObject responseBody = responseEntity.getBody();
        System.out.println(responseBody);
    }

    @Test
    @Transactional(readOnly = true)
    public void unCompleteProcess() {
        final String url = baseUrl + "/unCompleteProcess";
        Product product;

        do {
            product = productRepository.findById(testUtil.nextId(Product.class))
                    .orElseThrow(EntityNotFoundException::new);
        } while (product.getProductProcesses().size() == 0);

        int processId = product.getProductProcesses().iterator().next().getProcessId();

        JSONObject requestBody = new JSONObject();

        requestBody.put("productId", product.getId());
        requestBody.put("processId", processId);

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, request, JSONObject.class
        );

        assert responseEntity.getBody() != null;
        JSONObject responseBody = responseEntity.getBody();
        System.out.println(responseBody);
    }

    @Test
    @Transactional(readOnly = true)
    public void complete() {
        Long id = testUtil.nextId(Product.class);
        final String url = baseUrl + "/complete";

        JSONObject requestBody = new JSONObject();

        requestBody.put("id", id);

        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        boolean complete = product.getWork().getWorkProcesses().size() == product.getProductProcesses().size();

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

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

    private void addDateQueryParam(UriComponentsBuilder builder, String param, boolean positive) {
        int num = testUtil.nextInt(365);
        if (!positive) num = -num;
        builder.queryParam(param, testUtil.formatTime(today.plusDays(num)));
    }
}