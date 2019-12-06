package com.dghysc.hy.product;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.util.TestUtil;
import net.bytebuddy.utility.RandomString;
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
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    private static final String baseUrl = "/api/product";

    private static final Random random = new Random();

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void create() throws URISyntaxException {
        final String url = baseUrl + "/create";
        URI uri = new URI(url);

        JSONObject requestBody = new JSONObject();
        requestBody.put("serial", RandomString.make());
        requestBody.put("endTime", new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 28));
        requestBody.put("workId", 1);

        HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate.postForEntity(uri, request, JSONObject.class);

        System.out.println(response.getBody());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void update() throws URISyntaxException {
        final String url = baseUrl + "/update";
        URI uri = new URI(url);

        JSONObject requestBody = new JSONObject();

        requestBody.put("id", 1);
        requestBody.put("serial", RandomString.make());
        requestBody.put("endTime", new Timestamp(System.currentTimeMillis()));

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
    public void finish() throws URISyntaxException {
        int number = (int)productRepository.count();
        final String url = baseUrl + "/finish?id=" + random.nextInt(number);

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());


        ResponseEntity<JSONObject> response = restTemplate.postForEntity(uri, request, JSONObject.class);

        System.out.println(response.getBody());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void todayCreate() throws URISyntaxException {
        final String url = baseUrl + "/todayCreate";

        URI uri = new URI(url);

        HttpEntity<JSONObject> request = new HttpEntity<>(testUtil.getTokenHeader());

        ResponseEntity<JSONObject> response = restTemplate
                .exchange(uri, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        Assert.assertEquals(200, response.getStatusCodeValue());
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

        System.out.println("today:");
        for (Object obj : Objects.requireNonNull(todayResponse.getBody()).getJSONArray("data")) {
            JSONObject json = (JSONObject) obj;
            System.out.println(json.getInteger("id") + ": " + json.getTimestamp("endTime"));
        }

        System.out.println("tomorrow:");
        for (Object obj : Objects.requireNonNull(tomorrowResponse.getBody()).getJSONArray("data")) {
            JSONObject json = (JSONObject) obj;
            System.out.println(json.getInteger("id") + ": " + json.getTimestamp("endTime"));
        }

        System.out.println("dayAfterTomorrow:");
        for (Object obj : Objects.requireNonNull(dayAfterTomorrowResponse.getBody()).getJSONArray("data")) {
            JSONObject json = (JSONObject) obj;
            System.out.println(json.getInteger("id") + ": " + json.getTimestamp("endTime"));
        }

        Assert.assertEquals(200, todayResponse.getStatusCodeValue());
        Assert.assertEquals(200, tomorrowResponse.getStatusCodeValue());
        Assert.assertEquals(200, dayAfterTomorrowResponse.getStatusCodeValue());
    }
}