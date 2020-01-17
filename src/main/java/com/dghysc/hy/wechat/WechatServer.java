package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
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
    // TODO perfect exception.
    private WechatAccessToken token;

    private final Log logger;

    private final URI accessTokenUri;

    private final RestTemplate restTemplate;

    private final WechatAccessTokenRepository accessTokenRepository;

    public WechatServer(WechatAccessTokenRepository accessTokenRepository,
                        @Value("${manage.wechat.appId}") String appId,
                        @Value("${manage.wechat.accessTokenURL}") String url,
                        @Value("${manage.wechat.grantType}") String grantType,
                        @Value("${manage.wechat.secret}") String secret) throws Exception {
        this.accessTokenRepository = accessTokenRepository;

        logger = LogFactory.getLog(this.getClass());
        restTemplate = new RestTemplate();
        accessTokenUri = URI.create(url +
                "?grant_type=" + grantType +
                "&appid=" + appId +
                "&secret=" + secret
        );

        token = accessTokenRepository
                .findById(appId)
                .orElse(new WechatAccessToken(appId));

        if (
                token.getFailureTime() == null ||
                token.getFailureTime().getTime() < System.currentTimeMillis()
        ) {
            refreshToken();
        }
    }

    public String loadToken() throws Exception {
        if (token.getFailureTime().getTime() < System.currentTimeMillis()) {
            refreshToken();
        }

        return token.getAccessToken();
    }

    void refreshToken() throws Exception {
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(accessTokenUri, HttpMethod.GET, null, JSONObject.class);

        JSONObject response = Optional.ofNullable(responseEntity.getBody()).orElseThrow(Exception::new);

        String accessToken = response.getString("access_token");
        long expiresIn = response.getLongValue("expires_in") * 1000L;

        if (accessToken != null) {
            token.setAccessToken(accessToken);
            token.setFailureTime(new Timestamp(System.currentTimeMillis() + expiresIn));

            accessTokenRepository.save(token);
        } else {
            logger.error(response);
            throw new Exception();
        }
    }
}
