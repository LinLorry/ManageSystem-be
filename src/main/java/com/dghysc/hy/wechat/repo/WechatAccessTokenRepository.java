package com.dghysc.hy.wechat.repo;

import com.dghysc.hy.wechat.model.WechatAccessToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Wechat Access Token Repository
 * @author lorry
 * @author lin864464995@163.com
 */
@Repository
public interface WechatAccessTokenRepository extends CrudRepository<WechatAccessToken, String> {
}
