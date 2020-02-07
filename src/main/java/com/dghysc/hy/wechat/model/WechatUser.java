package com.dghysc.hy.wechat.model;

import com.dghysc.hy.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    // TODO wechat user info update.
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(length = 32)
    private String name;

    @JsonIgnore
    private String accessToken;

    @JsonIgnore
    private Timestamp tokenExpiresTime;

    @JsonIgnore
    private String refreshToken;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn
    private User user;

    public WechatUser() {
    }

    public WechatUser(String id) {
        this.id = id;
    }

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

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
