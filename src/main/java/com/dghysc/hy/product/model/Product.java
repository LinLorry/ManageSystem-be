package com.dghysc.hy.product.model;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.work.model.Work;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * The Product Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String serial;

    private Timestamp createTime;

    @ManyToOne
    @JsonIgnore
    private User createUser;

    @ManyToOne
    @JsonIgnore
    private Work work;

    private Timestamp endTime;

    private Timestamp finishTime;

    @JsonIgnore
    private ProductStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    // TODO 切换中文
    @JsonAnyGetter
    public HashMap<String, Object> getInfo() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("workId", work.getId());
        map.put("workName", work.getName());
        map.put("createUser", createUser.getName());

        switch (status) {
            case PROGRESS:
                map.put("status", "progress");
                break;
            case FINISH:
                map.put("status", "finish");
                break;
            default:
                map.put("status", "unknown");
        }

        return map;
    }
}
