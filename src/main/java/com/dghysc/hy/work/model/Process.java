package com.dghysc.hy.work.model;

import com.dghysc.hy.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
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

    @JsonIgnore
    @ManyToOne
    private User createUser;

    private Timestamp createTime;

    @JsonIgnore
    @ManyToOne
    private User updateUser;

    private Timestamp updateTime;

    @JsonIgnore
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
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

    public void setWorkProcesses(Set<WorkProcess> workProcesses) {
        this.workProcesses = workProcesses;
    }
}
