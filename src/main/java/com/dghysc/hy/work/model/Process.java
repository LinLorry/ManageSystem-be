package com.dghysc.hy.work.model;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Process Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class Process {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    private String comment;

    @Column(nullable = false, updatable = false)
    private Timestamp createTime;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private User createUser;

    @Column(nullable = false)
    private Timestamp updateTime;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private User updateUser;

    @JsonIgnore
    @OneToMany(mappedBy = "process")
    private Set<WorkProcess> workProcesses = new HashSet<>();

    public Process() {
    }

    public Process(String name, User createUser, Timestamp createTime) {
        this.name = name;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = createUser;
        this.updateTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getCreateUser() {
        return createUser;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public User getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(User updateUser) {
        this.updateUser = updateUser;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Set<WorkProcess> getWorkProcesses() {
        return workProcesses;
    }

    @JsonAnyGetter
    public Map<String, Object> getInfo() {
        return EntityUtil.getCreateAndUpdateInfo(createUser, updateUser);
    }
}
