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

    WechatUser add(@NotNull String id, @Nullable String name) {
        WechatUser wechatUser = new WechatUser();

        Optional.of(id).ifPresent(wechatUser::setId);
        Optional.ofNullable(name).ifPresent(wechatUser::setName);

        return wechatUserRepository.save(wechatUser);
    }

    WechatUser addUserByWechatUser(@NotNull String id) throws WechatUserExistException {
        WechatUser wechatUser = wechatUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (wechatUser.getUser() != null) {
            throw new WechatUserExistException();
        }

        wechatUser.setUser(new User());

        return wechatUserRepository.save(wechatUser);
    }

    WechatUser update(@NotNull String id, @NotNull String name) {
        WechatUser wechatUser = wechatUserRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        Optional.of(name).ifPresent(wechatUser::setName);

        return wechatUserRepository.save(wechatUser);
    }

    WechatUser updateUser(@NotNull String id, @Nullable Long userId) {
        WechatUser wechatUser = wechatUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (wechatUserRepository.existsByUserId(userId)) {
            throw new DuplicateUserException();
        } else if (userId == null) {
            wechatUser.setUser(null);
        } else if (userRepository.existsById(userId)) {
            User user = new User();
            user.setId(userId);

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

        String accessToken = response.getString("access_token");
        long expiresIn = response.getLongValue("expires_in") * 1000L;

        WechatUser wechatUser = wechatUserRepository.findById(id).orElse(new WechatUser(id));

        wechatUser.setAccessToken(accessToken);
        wechatUser.setTokenExpiresTime(new Timestamp(now + expiresIn));

        wechatUserRepository.save(wechatUser);

        return wechatUser;
    }

    WechatUser loadById(String id) {
        return wechatUserRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    Page<WechatUser> loadAll(Integer pageNumber) {
        return wechatUserRepository.findAll(PageRequest.of(pageNumber, 20));
    }

    Page<WechatUser> loadAllByName(String name, Integer pageNumber) {
        return wechatUserRepository.findAllByName(name, PageRequest.of(pageNumber, 20));
    }

    boolean checkById(String id) {
        return wechatUserRepository.existsById(id);
    }
}
