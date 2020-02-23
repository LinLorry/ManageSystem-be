package com.dghysc.hy.wechat.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Wechat Access Token Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class WechatAccessToken implements Serializable {
    @Id
    private String appId;

    @Column(length = 512)
    private String accessToken;

    private Timestamp expiresTime;

    public WechatAccessToken() {
    }

    public WechatAccessToken(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Timestamp getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Timestamp failureTime) {
        this.expiresTime = failureTime;
    }
}
