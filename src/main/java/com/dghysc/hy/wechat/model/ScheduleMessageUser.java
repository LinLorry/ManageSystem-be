package com.dghysc.hy.wechat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Optional;

/**
 * Schedule Message User Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
@Table(name = "schedule_message_user")
public class ScheduleMessageUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, updatable = false)
    private String id;  // wechat user open id.

    @ManyToOne(optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "id", nullable = false,
            insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private WechatUser wechatUser;

    public ScheduleMessageUser() { }

    public ScheduleMessageUser(@NotNull String id) {
        Optional.of(id).ifPresent(this::setId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WechatUser getWechatUser() {
        return wechatUser;
    }
}
