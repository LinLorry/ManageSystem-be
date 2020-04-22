package com.dghysc.hy.wechat.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Schedule Message User Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class ScheduleMessageUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, updatable = false)
    private String id;  // wechat user open id.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
