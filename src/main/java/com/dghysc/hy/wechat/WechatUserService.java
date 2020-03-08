package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.*;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SpecificationUtil;
import com.dghysc.hy.wechat.model.WechatUser;
import com.dghysc.hy.wechat.repo.WechatUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

/**
 * The Wechat User Service
 *
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class WechatUserService {

    private final String wechatUserAccessTokenUrlBase;

    private final String wechatUserRefreshAccessTokenUrlBase;

    private final UriComponentsBuilder wechatUserInfoUriBuilderBase;

    private final RestTemplate restTemplate;

    private final WechatUserRepository wechatUserRepository;

    public WechatUserService(
            @Value("${manage.wechat.userAccessTokenURL}") String userAccessTokenUrl,
            @Value("${manage.wechat.refreshAccessTokenURL}") String refreshAccessTokenUrl,
            @Value("${manage.wechat.userInfoURL}") String userInfoUrl,
            WechatUserRepository wechatUserRepository,
            WechatServer wechatServer) {
        this.wechatUserRepository = wechatUserRepository;
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        wechatUserAccessTokenUrlBase = userAccessTokenUrl +
                "?appid=" + wechatServer.getAppId() +
                "&secret=" + wechatServer.getSecret() +
                "&grant_type=authorization_code&code=";

        wechatUserRefreshAccessTokenUrlBase = refreshAccessTokenUrl +
                "?appid=" + wechatServer.getAppId() +
                "&grant_type=refresh_token&refresh_token=";

        wechatUserInfoUriBuilderBase = UriComponentsBuilder.fromHttpUrl(userInfoUrl)
                .queryParam("lang", "zh_CN");

    }

    WechatUser update(@NotNull String id, @NotNull String name)
            throws WechatServiceDownException, WechatRefreshTokenExpireException {
        WechatUser wechatUser = wechatUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        Optional.of(name).ifPresent(wechatUser::setName);

        if (wechatUser.getTokenExpiresTime().getTime() > System.currentTimeMillis()) {
            wechatUser = refreshAccessToken(wechatUser);
        }

        UriComponentsBuilder builder = wechatUserInfoUriBuilderBase.cloneBuilder()
                .queryParam("openid", wechatUser.getId())
                .queryParam("access_token", wechatUser.getAccessToken());

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    builder.toUriString(), HttpMethod.GET, null, String.class);
        } catch (RestClientException e) {
            wechatUserRepository.save(wechatUser);
            throw new WechatServiceDownException();
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            wechatUserRepository.save(wechatUser);
            throw new WechatServiceDownException();
        }

        JSONObject response = JSONObject.parseObject(
                Optional.ofNullable(responseEntity.getBody())
                        .orElseThrow(WechatServiceDownException::new)
        );

        if (response.getString("errcode") == null) {
            wechatUser.setNickname(response.getString("nickname"));
            wechatUser.setSex(response.getInteger("sex"));
            wechatUser.setProvince(response.getString("province"));
            wechatUser.setCity(response.getString("city"));
            wechatUser.setCountry(response.getString("country"));
            wechatUser.setHeadImgUrl(response.getString("headimgurl"));
        }

        return wechatUserRepository.save(wechatUser);
    }

    /**
     * Add or Update WechatUser User
     * @param id the wechat user id.
     * @return the wechat user.
     * @throws EntityNotFoundException  if not wechat user id is {@code id}.
     */
    WechatUser addUser(@NotNull String id) {
        WechatUser wechatUser = wechatUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (wechatUser.getUser() == null) {
            wechatUser.setUser(new User());
            return wechatUserRepository.save(wechatUser);
        } else return wechatUser;
    }

    WechatUser loadByCode(String code)
            throws WechatServiceDownException, WechatUserCodeWrongException {
        final String url = wechatUserAccessTokenUrlBase + code;

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    url, HttpMethod.GET, null, String.class);
        } catch (RestClientException e) {
            throw new WechatServiceDownException();
        }

        long now = System.currentTimeMillis();

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new WechatServiceDownException();
        }

        JSONObject response = JSONObject.parseObject(
                Optional.ofNullable(responseEntity.getBody())
                        .orElseThrow(WechatServiceDownException::new)
        );

        String id = Optional.ofNullable(response.getString("openid"))
                .orElseThrow(WechatUserCodeWrongException::new);

        WechatUser wechatUser = wechatUserRepository.findById(id).orElse(new WechatUser(id));

        String accessToken = response.getString("access_token");
        String refreshToken = response.getString("refresh_token");
        long expiresIn = response.getLongValue("expires_in") * 1000L;

        wechatUser.setAccessToken(accessToken);
        wechatUser.setRefreshToken(refreshToken);
        wechatUser.setTokenExpiresTime(new Timestamp(now + expiresIn));

        wechatUserRepository.save(wechatUser);

        return wechatUser;
    }

    /**
     * Load Wechat User By Id
     * @param id the wechat user id.
     * @return the wechat user.
     * @throws NullPointerException {@code id} is {@literal null}
     * @throws EntityNotFoundException the wechat user not exist.
     */
    WechatUser loadById(@NotNull String id) {
        return wechatUserRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Load Wechat Users Service
     * @param likeMap {
     *      "the wechat user field": value will be equal by "%value%"
     * }
     * @param pageNumber the page number.
     * @param pageSize the page size.
     * @return the wechat users page.
     * @throws NullPointerException {@code likeMap} is {@literal null}
     */
    Page<WechatUser> load(@NotNull Map<String, Object> likeMap, int pageNumber, int pageSize) {
        SpecificationUtil specificationUtil = new SpecificationUtil();

        specificationUtil.addLikeMap(likeMap);

        return wechatUserRepository.findAll(specificationUtil.getSpecification(),
                PageRequest.of(pageNumber, pageSize));
    }

    WechatUser refreshAccessToken(WechatUser wechatUser)
            throws WechatServiceDownException, WechatRefreshTokenExpireException {
        final String url = wechatUserRefreshAccessTokenUrlBase + wechatUser.getRefreshToken();

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    url, HttpMethod.GET, null, String.class);
        } catch (RestClientException e) {
            throw new WechatServiceDownException();
        }

        long now = System.currentTimeMillis();

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new WechatServiceDownException();
        }

        JSONObject response = JSONObject.parseObject(
                Optional.ofNullable(responseEntity.getBody())
                        .orElseThrow(WechatServiceDownException::new)
        );

        if (response.getString("errcode") != null) {
            throw new WechatRefreshTokenExpireException();
        }

        String accessToken = response.getString("access_token");
        String refreshToken = response.getString("refresh_token");
        long expiresIn = response.getLongValue("expires_in") * 1000L;

        wechatUser.setAccessToken(accessToken);
        wechatUser.setRefreshToken(refreshToken);
        wechatUser.setTokenExpiresTime(new Timestamp(now + expiresIn));

        return wechatUserRepository.save(wechatUser);
    }
}
