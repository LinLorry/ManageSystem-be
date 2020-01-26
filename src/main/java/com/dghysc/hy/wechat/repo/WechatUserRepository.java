package com.dghysc.hy.wechat.repo;

import com.dghysc.hy.wechat.model.WechatUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Wechat User Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface WechatUserRepository extends JpaRepository<WechatUser, String> {

    Page<WechatUser> findAllByName(String name, Pageable pageable);

    @Query(value = "SELECT " +
            "CASE WHEN COUNT(wechatUser) > 0 " +
            "THEN TRUE ELSE FALSE END " +
            "FROM WechatUser wechatUser " +
            "WHERE wechatUser.user.id = ?1")
    Boolean existsByUserId(Long id);
}
