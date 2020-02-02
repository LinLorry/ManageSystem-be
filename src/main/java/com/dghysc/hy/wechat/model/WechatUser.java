package com.dghysc.hy.wechat.model;

import com.dghysc.hy.user.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The Wechat User Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class WechatUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(length = 32)
    private String name;

    private String accessToken;

    private Timestamp tokenExpiresTime;

    @OneToOne
    @JoinColumn
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String openId) {
        this.id = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getTokenExpiresTime() {
        return tokenExpiresTime;
    }

    public void setTokenExpiresTime(Timestamp tokenFailureTime) {
        this.tokenExpiresTime = tokenFailureTime;
    }
}
