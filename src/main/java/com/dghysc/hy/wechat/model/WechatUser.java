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

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(length = 32)
    private String name;

    private String nickname;

    private Integer sex;

    private String province;

    private String city;

    private String country;

    private String headImgUrl;

    @JsonIgnore
    private String accessToken;

    @JsonIgnore
    private Timestamp tokenExpiresTime;

    @JsonIgnore
    private String refreshToken;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn
    private User user;

    @JsonIgnore
    private Long oldUserId;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Timestamp getTokenExpiresTime() {
        return tokenExpiresTime;
    }

    public void setTokenExpiresTime(Timestamp tokenFailureTime) {
        this.tokenExpiresTime = tokenFailureTime;
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

    public Long getOldUserId() {
        return oldUserId;
    }

    public void setOldUserId(Long oldUserId) {
        this.oldUserId = oldUserId;
    }
}
