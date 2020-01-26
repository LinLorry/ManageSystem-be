package com.dghysc.hy.wechat;

import com.dghysc.hy.exception.DuplicateUserException;
import com.dghysc.hy.exception.UserNoFoundException;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.wechat.model.WechatUser;
import com.dghysc.hy.wechat.repo.WechatUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * The Wechat User Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class WechatUserService {

    private final UserRepository userRepository;

    private final WechatUserRepository wechatUserRepository;

    public WechatUserService(UserRepository userRepository, WechatUserRepository wechatUserRepository) {
        this.userRepository = userRepository;
        this.wechatUserRepository = wechatUserRepository;
    }

    WechatUser add(@NotNull String id, @Nullable String name) {
        WechatUser wechatUser = new WechatUser();

        Optional.of(id).ifPresent(wechatUser::setId);
        Optional.ofNullable(name).ifPresent(wechatUser::setName);

        return wechatUserRepository.save(wechatUser);
    }

    WechatUser update(@NotNull String id, @NotNull String name) {
        WechatUser wechatUser = wechatUserRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        Optional.of(name).ifPresent(wechatUser::setName);

        return wechatUserRepository.save(wechatUser);
    }

    WechatUser updateUser(@NotNull String id, @Nullable Long userId) {
        WechatUser wechatUser = wechatUserRepository.findById(id).orElseThrow(EntityNotFoundException::new);

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
