package com.dghysc.hy.wechat.repo;

import com.dghysc.hy.wechat.model.WechatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Wechat User Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 */
@Repository
public interface WechatUserRepository extends JpaRepository<WechatUser, String>,
        JpaSpecificationExecutor<WechatUser> {

    @Query("SELECT wechatUser FROM WechatUser wechatUser where wechatUser.user.id = :id")
    Optional<WechatUser> findByUserId(Long id);
}
