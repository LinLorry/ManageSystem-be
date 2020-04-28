package com.dghysc.hy.util;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.WechatConfigWrongException;
import com.dghysc.hy.exception.WechatServiceDownException;
import com.dghysc.hy.wechat.WechatServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private final Log logger;

    private final RestTemplate restTemplate;

    public WechatMessageUtil(
            @Value("${manage.wechat.messageURL}") String messageURL,
            WechatServer wechatServer) {
        this.messageURL = messageURL;
        this.wechatServer = wechatServer;
        this.logger = LogFactory.getLog(this.getClass());
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
            String message = "Send wechat template message failed.\n" +
                    "Return status: " + responseEntity.getStatusCode();
            logger.error(message);
            throw new WechatServiceDownException(message);
        }

        JSONObject response = Optional.ofNullable(responseEntity.getBody())
                .orElseThrow(() -> {
                    String message = "Send wechat template message response body null.";
                    logger.error(message);
                    return new WechatServiceDownException(message);
                });


        int errcode =
                Optional.ofNullable(response.getInteger("errcode"))
                .orElseThrow(() -> {
                    String message = "Send wechat template message response defect 'errcode'.\n" +
                            "Response: " + response;
                    logger.error(message);
                    return new WechatServiceDownException(message);
                });

        if (errcode != 0) {
            String message = "Send wechat template message failed. \n" +
                    "Errcode: " + errcode;
            logger.error(message);
            throw new WechatConfigWrongException(message);
        }
    }
}
