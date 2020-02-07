package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.*;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.wechat.model.WechatUser;
import com.dghysc.hy.wechat.repo.WechatUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * The Wechat User Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class WechatUserService {

    private final String urlBase;

    private final RestTemplate restTemplate = new RestTemplate();

    private final UserRepository userRepository;

    private final WechatUserRepository wechatUserRepository;

    public WechatUserService(
            @Value("${manage.wechat.userAccessTokenURL}") String url,
            UserRepository userRepository,
            WechatUserRepository wechatUserRepository,
            WechatServer wechatServer) {
        this.userRepository = userRepository;
        this.wechatUserRepository = wechatUserRepository;

        urlBase = url +
                "?appid=" + wechatServer.getAppId() +
                "&secret=" + wechatServer.getSecret() +
                "&grant_type=authorization_code&code=";
    }

    WechatUser update(@NotNull String id, @NotNull String name) {
        WechatUser wechatUser = wechatUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        Optional.of(name).ifPresent(wechatUser::setName);

        return wechatUserRepository.save(wechatUser);
    }

    WechatUser addOrUpdateUser(@NotNull String id, @Nullable User user)
            throws DuplicateUserException, UserNoFoundException {
        WechatUser wechatUser = wechatUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (user == null) {
            wechatUser.setUser(null);
        } else if (user.getId() == null) {
            wechatUser.setUser(user);
        } else if (wechatUserRepository.existsByUserId(user.getId())) {
            throw new DuplicateUserException();
        } else if (userRepository.existsById(user.getId())) {
            wechatUser.setUser(user);
        } else throw new UserNoFoundException();

        return wechatUserRepository.save(wechatUser);
    }

    WechatUser loadByCode(String code)
            throws WechatServiceDownException, WechatUserCodeWrongException {
        final String url = urlBase + code;
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

    WechatUser loadById(String id) {
        return wechatUserRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    Page<WechatUser> loadAll(Integer pageNumber, Integer pageSize) {
        return wechatUserRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    Page<WechatUser> loadAllByName(String name, Integer pageNumber, Integer pageSize) {
        return wechatUserRepository.findAllByName(name, PageRequest.of(pageNumber, pageSize));
    }
}
