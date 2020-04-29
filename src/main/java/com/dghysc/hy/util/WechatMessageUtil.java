package com.dghysc.hy.util;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.WechatConfigWrongException;
import com.dghysc.hy.exception.WechatServiceDownException;
import com.dghysc.hy.wechat.WechatServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;

@Component
public class WechatMessageUtil {

    private final String messageURL;

    private final WechatServer wechatServer;

    private final RestTemplate restTemplate;

    public WechatMessageUtil(
            @Value("${manage.wechat.messageURL}") String messageURL,
            WechatServer wechatServer) {
        this.messageURL = messageURL;
        this.wechatServer = wechatServer;
        this.restTemplate = new RestTemplate();
    }

    public void sendTemplateMessage(
            @NotNull String templateId, @NotNull String wechatUserId,
            @Nullable String titleColor, @Nullable String url,
            @NotNull Map<String, JSONObject> data
    ) throws WechatServiceDownException, WechatConfigWrongException {
        JSONObject requestBody = new JSONObject();

        requestBody.put("template_id", Optional.of(templateId).get());
        requestBody.put("touser", Optional.of(wechatUserId).get());

        Optional.ofNullable(titleColor)
                .ifPresentOrElse(
                        v -> requestBody.put("topcolor", v),
                        () -> requestBody.put("topcolor", "#FF0000")
                );
        Optional.ofNullable(url).ifPresent(u -> requestBody.put("url", u));
        requestBody.put("data", Optional.of(data).get());

        final String messageURL = this.messageURL + wechatServer.loadToken();
        final HttpEntity<JSONObject> requestEntity = new HttpEntity<>(requestBody);

        final ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(messageURL, HttpMethod.POST, requestEntity, JSONObject.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new WechatServiceDownException("Send wechat template message failed.\n" +
                    "Return status: " + responseEntity.getStatusCode());
        }

        JSONObject response = Optional.ofNullable(responseEntity.getBody())
                .orElseThrow(() ->
                        new WechatServiceDownException("Send wechat template message response body null.")
                );


        int errcode = Optional.ofNullable(response.getInteger("errcode"))
                .orElseThrow(() ->
                        new WechatServiceDownException("Send wechat template message response defect 'errcode'.\n" +
                                "Response: " + response)
                );

        if (errcode != 0) {
            throw new WechatConfigWrongException("Send wechat template message failed. \n" +
                    "Errcode: " + errcode +
                    "\nErrmsg" + response.getString("errmsg"));
        }
    }

    public static JSONObject createData(@Nullable Object value) {
        JSONObject json = new JSONObject();

        json.put("value", Optional.ofNullable(value).orElse(""));
        json.put("color", "#173177");

        return json;
    }

    public static JSONObject createData(@Nullable Object value, @NotNull String color) {
        JSONObject json = new JSONObject();

        json.put("value", Optional.ofNullable(value).orElse(""));
        json.put("color", Optional.of(color).get());

        return json;
    }
}
