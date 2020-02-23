package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.WechatConfigWrongException;
import com.dghysc.hy.exception.WechatServiceDownException;
import com.dghysc.hy.wechat.model.WechatAccessToken;
import com.dghysc.hy.wechat.repo.WechatAccessTokenRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * Wechat Server
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class WechatServer {

    private WechatAccessToken token;

    private final String appId;

    private final String secret;

    private final Log logger;

    private final URI accessTokenUri;

    private final RestTemplate restTemplate;

    private final WechatAccessTokenRepository accessTokenRepository;

    public WechatServer(
            WechatAccessTokenRepository accessTokenRepository,
            @Value("${manage.wechat.appId}") String appId,
            @Value("${manage.wechat.accessTokenURL}") String url,
            @Value("${manage.wechat.secret}") String secret
    ) throws WechatServiceDownException, WechatConfigWrongException {
        this.accessTokenRepository = accessTokenRepository;
        this.appId = appId;
        this.secret = secret;

        logger = LogFactory.getLog(this.getClass());
        restTemplate = new RestTemplate();
        accessTokenUri = URI.create(
                url +
                "?grant_type=client_credential" +
                "&appid=" + appId +
                "&secret=" + secret
        );

        token = accessTokenRepository.findById(appId)
                .orElse(new WechatAccessToken(appId));

        if (
                token.getExpiresTime() == null ||
                token.getExpiresTime().getTime() < System.currentTimeMillis()
        ) {
            refreshToken();
        }
    }

    public String loadToken() throws WechatServiceDownException, WechatConfigWrongException {
        if (token.getExpiresTime().getTime() < System.currentTimeMillis()) {
            refreshToken();
        }

        return token.getAccessToken();
    }

    void refreshToken() throws WechatServiceDownException, WechatConfigWrongException {
        ResponseEntity<JSONObject> responseEntity = restTemplate
                .exchange(accessTokenUri, HttpMethod.GET, null, JSONObject.class);
        long now = System.currentTimeMillis();

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            String message = "Request wechat access token failed.\n" +
                    "Return status: " + responseEntity.getStatusCode();
            logger.error(message);
            throw new WechatServiceDownException(message);
        }

        JSONObject response = Optional.ofNullable(responseEntity.getBody())
                .orElseThrow(() -> {
                    String message = "Get access token response body null.";
                    logger.error(message);
                    return new WechatServiceDownException(message);
                });

        String accessToken = Optional.ofNullable(response.getString("access_token"))
                .orElseThrow(() -> {
                    String message = "Get access token failed.\n" +
                            "Response: " + response;
                    logger.error(message);
                    return new WechatConfigWrongException(message);
                });

        long expiresIn = response.getLongValue("expires_in") * 1000L;

        token.setAccessToken(accessToken);
        token.setExpiresTime(new Timestamp(now + expiresIn));

        accessTokenRepository.save(token);
    }

    String getAppId() {
        return appId;
    }

    String getSecret() {
        return secret;
    }
}
